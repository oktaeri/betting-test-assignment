package assignment;

import assignment.model.*;
import assignment.services.BetService;
import assignment.services.TransactionService;
import assignment.util.ResultDataToFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import assignment.util.MatchDataFileParser;
import assignment.util.PlayerDataFileParser;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class BettingTest {
    public static final String MATCH_DATA_FILE = "src/main/resources/match_data.txt";
    public static final String PLAYER_DATA_FILE = "src/main/resources/player_data.txt";
    public static final String TEST_RESULT_FILE = "src/test/resources/test_result.txt";

    @Test
    void matchDataFileExists() {
        Assertions.assertTrue(new File(MATCH_DATA_FILE).isFile());
    }

    @Test
    void playerDataFileExists() {
        Assertions.assertTrue(new File(PLAYER_DATA_FILE).isFile());
    }

    @Test
    void playersWithMultipleTransactionsAreParsedOnce() {
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(PLAYER_DATA_FILE);

        Assertions.assertEquals(2, players.size());
    }

    @Test
    void playersGetDefaultBalanceAfterParsing() {
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(PLAYER_DATA_FILE);

        for (Player player : players) {
            Assertions.assertEquals(0, player.getBalance());
        }
    }

    @Test
    void playerDataFileWithOneLegalTransactionLineReturnsOnePlayer() {
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);

        Assertions.assertEquals(1, players.size());
    }

    @Test
    void playerHasOneLegalTransaction() {
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);

        Assertions.assertEquals(1, player.getTransactions().size());
    }

    @Test
    void playerTransactionGetsCorrectTransactionType() {
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(TransactionType.BET, transaction.getTransactionType());
    }

    @Test
    void transactionBalanceIncreasesAfterLegalDepositTransaction() {
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(550, transaction.getCoinsAmount());
    }

    @Test
    void transactionBalanceDoesNotIncreaseAfterNegativeBetTransaction() {
        String filepath = "src/test/resources/test_player_data_bet_with_negative_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(0, transaction.getCoinsAmount());
    }

    @Test
    void playerTransactionHasCorrectBetSide() {
        String filepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(MatchResult.B, transaction.getBetSide());
    }

    @Test
    void playerTransactionHasNullBetSideIfSideIsInvalid() {
        String filepath = "src/test/resources/test_player_data_bet_with_amount_and_invalid_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertNull(transaction.getBetSide());
    }

    @Test
    void matchesGetParsedToMatchList() {
        MatchDataFileParser parser = new MatchDataFileParser();

        List<Match> matches = parser.parseMatchData(MATCH_DATA_FILE);

        Assertions.assertEquals(13, matches.size());
    }

    @Test
    void playerBalanceIncreasesAfterLegalDepositTransaction() {
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(550, player.getBalance());
    }

    @Test
    void playerBalanceDecreasesAfterLegalWithdrawTransaction() {
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(350, player.getBalance());
    }

    @Test
    void playerBalanceDoesNotDecreaseAfterIllegalWithdrawTransaction() {
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(550, player.getBalance());
    }

    @Test
    void illegalWithdrawTransactionGetsAddedToPlayerIllegalAction() {
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNotNull(player.getIllegalAction());
    }

    @Test
    void legalWithdrawTransactionDoesNotGetAddedToPlayerIllegalAction() {
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNull(player.getIllegalAction());
    }

    @Test
    void playerLegalBetWinIncreasesPlayerBalance() {
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_winning_side.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();
        Player player = players.get(0);

        Assertions.assertEquals(1275, player.getBalance());
    }

    @Test
    void playerLegalBetWinGetsAddedToPlayerMatchesWon() {
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_winning_side.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();
        Player player = players.get(0);

        Assertions.assertEquals(1, player.getMatchesWon());
    }

    @Test
    void playerLegalBetLossDecreasesPlayerBalance() {
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_losing_side.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();
        Player player = players.get(0);

        Assertions.assertEquals(50, player.getBalance());
    }

    @Test
    void playerBetWinDecreasesCasinoBalance() {
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_winning_side.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(-725, result.getCasinoBalance());
    }

    @Test
    void playerBetLossIncreasesCasinoBalance() {
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_losing_side.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(500, result.getCasinoBalance());
    }

    @Test
    void illegalBetIsAddedToPlayerIllegalAction() {
        String playerFilepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();
        Player player = players.get(0);

        Assertions.assertNotNull(player.getIllegalAction());
    }

    @Test
    void illegalBetDoesNotAffectCasinoBalance() {
        String playerFilepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(0, result.getCasinoBalance());
    }

    @Test
    void casinoBalanceIsCalculatedCorrectly() {
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(PLAYER_DATA_FILE);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(75, result.getCasinoBalance());
    }

    @Test
    void legalPlayerBalanceIsCalculatedCorrectly() {
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(PLAYER_DATA_FILE);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();
        Player player = players.get(0);

        Assertions.assertEquals(2725, player.getBalance());
    }

    @Test
    void legalPlayerWinRatioIsCalculatedCorrectly() {
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(PLAYER_DATA_FILE);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();
        Player player = players.get(0);

        Assertions.assertEquals(BigDecimal.valueOf(0.14), player.getWinRatio());
    }

    @Test
    void legalPlayerToStringIsCorrect() {
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(PLAYER_DATA_FILE);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();
        Player player = players.get(0);

        Assertions.assertEquals("163f23ed-e9a9-4e54-a5b1-4e1fc86f12f4 2725 0,14", player.toString());
    }

    @Test
    void illegalPlayerToStringIsCorrect() {
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(PLAYER_DATA_FILE);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();
        Player player = players.get(1);

        Assertions.assertEquals("4925ac98-833b-454b-9342-13ed3dfd3ccf WITHDRAW null 8093 null", player.illegalToString());
    }

    @Test
    void correctIllegalBetIsAddedToPlayerIllegalAction() {
        List<Match> matches = new ArrayList<>();
        Player player = new Player(UUID.randomUUID());
        Match match = new Match(UUID.randomUUID(), BigDecimal.valueOf(1.46), BigDecimal.valueOf(0.2), MatchResult.B);
        Transaction transaction = new Transaction(TransactionType.BET, match.getId(), 500, MatchResult.B);
        player.getTransactions().add(transaction);
        matches.add(match);
        BetService betService = new BetService(matches);

        betService.processBet(player, transaction);

        Assertions.assertEquals(transaction, player.getIllegalAction());
    }

    @Test
    void playerDataFileDoesNotExistThrowsRunTimeException() {
        String fakeFile = "this_does_not_exist.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new PlayerDataFileParser().parsePlayerData(fakeFile));
    }

    @Test
    void matchDataFileDoesNotExistThrowsRunTimeException() {
        String fakeFile = "this_does_not_exist.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new MatchDataFileParser().parseMatchData(fakeFile));
    }

    @Test
    void negativeDepositTransactionChangesToZero() {
        String filepath = "src/test/resources/test_player_data_deposit_negative.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Transaction transaction = players.get(0).getTransactions().get(0);

        Assertions.assertEquals(0, transaction.getCoinsAmount());
    }

    @Test
    void invalidDepositAmountThrowsRuntimeException() {
        String invalidFile = "src/test/resources/test_player_data_deposit_invalid.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new PlayerDataFileParser().parsePlayerData(invalidFile));
    }

    @Test
    void negativeWithdrawTransactionChangesToZero() {
        String filepath = "src/test/resources/test_player_data_withdraw_negative.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Transaction transaction = players.get(0).getTransactions().get(0);

        Assertions.assertEquals(0, transaction.getCoinsAmount());
    }

    @Test
    void invalidWithdrawTransactionThrowsRuntimeException() {
        String invalidFile = "src/test/resources/test_player_data_withdraw_invalid.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new PlayerDataFileParser().parsePlayerData(invalidFile));
    }

    @Test
    void invalidBetAmountThrowsRuntimeException() {
        String invalidFile = "src/test/resources/test_player_data_bet_with_invalid_amount_and_side.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new PlayerDataFileParser().parsePlayerData(invalidFile));
    }

    @Test
    void onlyLegalPlayersResultFileHasNoIllegalPlayers() throws IOException {
        String expected = """
                163f23ed-e9a9-4e54-a5b1-4e1fc86f12f4 2725 0,14
                4925ac98-833b-454b-9342-13ed3dfd3ccf 723 0,50



                -148""";
        String playerFilepath = "src/test/resources/test_player_data_all_legal.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();
        ResultDataToFile resultDataToFile = new ResultDataToFile();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        resultDataToFile.write(transactionProcessor.processTransactions(), TEST_RESULT_FILE);

        String fileContent = Files.readString(Paths.get(TEST_RESULT_FILE));

        Assertions.assertEquals(expected, fileContent);
    }

    @Test
    void onlyIllegalPlayersResultFileHasNoLegalPlayers() throws IOException {
        String expected = """


                163f23ed-e9a9-4e54-a5b1-4e1fc86f12f4 BET a3815c17-9def-4034-a21f-65369f6d4a56 200000 A
                4925ac98-833b-454b-9342-13ed3dfd3ccf WITHDRAW null 8093 null

                0""";
        String playerFilepath = "src/test/resources/test_player_data_all_illegal.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();
        ResultDataToFile resultDataToFile = new ResultDataToFile();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        resultDataToFile.write(transactionProcessor.processTransactions(), TEST_RESULT_FILE);

        String fileContent = Files.readString(Paths.get(TEST_RESULT_FILE));

        Assertions.assertEquals(expected, fileContent);
    }

    @Test
    void sampleDataResultIsCorrect() throws IOException {
        String expected = """
                163f23ed-e9a9-4e54-a5b1-4e1fc86f12f4 2725 0,14

                4925ac98-833b-454b-9342-13ed3dfd3ccf WITHDRAW null 8093 null

                75""";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();
        ResultDataToFile resultDataToFile = new ResultDataToFile();

        List<Player> players = playerParser.parsePlayerData(PLAYER_DATA_FILE);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        resultDataToFile.write(transactionProcessor.processTransactions(), TEST_RESULT_FILE);

        String fileContent = Files.readString(Paths.get(TEST_RESULT_FILE));

        Assertions.assertEquals(expected, fileContent);
    }

}
