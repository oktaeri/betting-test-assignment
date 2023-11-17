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
    void givenMatchDataFile_whenCheckingExistence_thenFileExists(){
        Assertions.assertTrue(new File(MATCH_DATA_FILE).isFile());
    }

    @Test
    void givenPlayerDataFile_whenCheckingExistence_thenFileExists(){
        Assertions.assertTrue(new File(PLAYER_DATA_FILE).isFile());
    }

    @Test
    void givenPlayerDataFile_whenParsingPlayers_thenPlayersParsedOnce(){
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(PLAYER_DATA_FILE);

        Assertions.assertEquals(2, players.size());
    }

    @Test
    void givenPlayerDataFile_whenParsingPlayers_thenPlayersGetDefaultBalanceAfterParsing(){
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(PLAYER_DATA_FILE);

        for (Player player : players) {
            Assertions.assertEquals(0, player.getBalance());
        }
    }

    @Test
    void givenPlayerDataFileWithOneDataLine_whenParsingPlayers_thenReturnsOnePlayer(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);

        Assertions.assertEquals(1, players.size());
    }

    @Test
    void givenTestPlayerDataFileEmptyBet_whenParsingPlayers_thenTransactionGetsAddedToPlayer(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);

        Assertions.assertEquals(1, player.getTransactions().size());
    }

    @Test
    void givenTestPlayerDataFileEmptyBet_whenParsingPlayers_thenCorrectTransactionTypeAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(TransactionType.BET, transaction.getTransactionType());
    }

    @Test
    void givenTestPlayerDataDepositFile_whenParsingPlayers_thenCorrectCoinsAmountAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(550, transaction.getCoinsAmount());
    }

    @Test
    void givenTestPlayerDataBetWithInvalidAmountAndSide_whenParsingPlayers_thenInvalidCoinsAmountDoesNotGetAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_negative_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(0, transaction.getCoinsAmount());
    }

    @Test
    void givenTestPlayerDataBetWithAmountAndSide_whenParsingPlayers_thenCorrectBetSideAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(MatchResult.B, transaction.getBetSide());
    }

    @Test
    void givenTestPlayerDataFileBetWithInvalidSide_whenParsingPlayers_thenInvalidBetSideDoesNotGetAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_amount_and_invalid_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertNull(transaction.getBetSide());
    }

    @Test
    void givenMatchDataFile_whenParsingMatches_thenAllMatchesGetParsedToMatchClass(){
        MatchDataFileParser parser = new MatchDataFileParser();

        List<Match> matches = parser.parseMatchData(MATCH_DATA_FILE);

        Assertions.assertEquals(13, matches.size());
    }

    @Test
    void givenTestPlayerDataFileDeposit_whenProcessingTransactions_thenDepositedAmountGetsAddedToPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(550, player.getBalance());
    }

    @Test
    void givenTestPlayerDataFileDepositAndWithdrawLegal_whenProcessingTransactions_thenLegalWithdrawalAmountGetsDeductedFromPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(350, player.getBalance());
    }

    @Test
    void givenTestPlayerDataFileDepositAndWithdrawIllegal_whenProcessingTransactions_thenIllegalWithdrawalAmountDoesNotGetDeductedFromPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(550, player.getBalance());
    }

    @Test
    void givenTestPlayerDataFileDepositAndWithdrawIllegal_whenProcessingTransactions_thenIllegalWithdrawalGetsAddedToPlayerIllegalActions(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNotNull(player.getIllegalAction());
    }

    @Test
    void givenTestPlayerDataFileDepositAndWithdrawLegal_whenProcessingTransactions_thenIllegalWithdrawalDoesNotGetAddedToPlayerIllegalActions(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();

        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNull(player.getIllegalAction());
    }

    @Test
    void givenTestPlayerDataFileDepositAndBetWinningSide_whenProcessingTransactions_thenPlayerWinsBetAndGetsCoinsAddedToBalance(){
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
    void givenTestPlayerDataFileDepositAndBetWinningSide_whenProcessingTransactions_thenPlayerWinsBetAndWonMatchesCountIncreases(){
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
    void givenTestPlayerDataFileDepositAndBetLosingSide_whenProcessingTransactions_thenPlayerLosesBetAndPlayerBalanceDecreases(){
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
    void givenTestPlayerDataFileDepositAndBetWinningSide_whenProcessingTransactions_thenPlayerWinsBetAndCasinoBalanceIsNegative(){
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
    void givenTestPlayerDataFileDepositAndBetLosingSide_whenProcessingTransactions_thenPlayerLosesBetAndCasinoBalanceIncreases(){
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
    void givenTestPlayerDataFileBetWithAmountAndSide_whenProcessingTransactions_thenIllegalBetIsAddedToPlayerIllegalAction() {
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
    void givenTestPlayerDataFileBetWithAmountAndSide_whenProcessingTransactions_thenIllegalBetDoesNotAffectCasinoBalance(){
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
    void givenSamplePlayerDataFile_whenProcessingTransactions_thenCasinoBalanceIsCorrect(){
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(PLAYER_DATA_FILE);
        List<Match> matches = matchParser.parseMatchData(MATCH_DATA_FILE);
        TransactionService transactionProcessor = new TransactionService(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(75, result.getCasinoBalance());
    }

    @Test
    void givenSamplePlayerDataFile_whenProcessingTransactions_thenPlayerBalanceIsCorrect(){
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
    void givenSamplePlayerDataFile_whenProcessingTransactions_thenPlayerWinRatioIsCorrect(){
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
    void givenSamplePlayerDataFile_whenProcessingTransactions_thenPlayerToStringIsCorrect(){
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
    void givenSamplePlayerDataFile_whenProcessingTransactions_thenIllegalPlayerToStringIsCorrect(){
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
    void givenPlayerAndIllegalBet_whenProcessingBets_thenIllegalTransactionIsAddedToPlayerIllegalAction(){
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
    void givenNonExistentFile_whenParsingPlayerData_thenThrowsRuntimeException(){
        String fakeFile = "this_does_not_exist.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new PlayerDataFileParser().parsePlayerData(fakeFile));
    }

    @Test
    void givenNonExistentFile_whenParsingMatchData_thenThrowsRuntimeException(){
        String fakeFile = "this_does_not_exist.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new MatchDataFileParser().parseMatchData(fakeFile));
    }

    @Test
    void givenTestPlayerDataFileNegativeDeposit_whenParsingPlayerData_thenDepositAmountGetsChangedToZero(){
        String filepath = "src/test/resources/test_player_data_deposit_negative.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Transaction transaction = players.get(0).getTransactions().get(0);

        Assertions.assertEquals(0, transaction.getCoinsAmount());
    }

    @Test
    void givenTestPlayerDataFileInvalidDeposit_whenParsingPlayerData_thenThrowsRuntimeException(){
        String invalidFile = "src/test/resources/test_player_data_deposit_invalid.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new PlayerDataFileParser().parsePlayerData(invalidFile));
    }

    @Test
    void givenTestPlayerDataFileNegativeWithdraw_whenParsingPlayerData_thenDepositAmountGetsChangedToZero(){
        String filepath = "src/test/resources/test_player_data_withdraw_negative.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Transaction transaction = players.get(0).getTransactions().get(0);

        Assertions.assertEquals(0, transaction.getCoinsAmount());
    }

    @Test
    void givenTestPlayerDataFileInvalidWithdraw_whenParsingPlayerData_thenThrowsRuntimeException(){
        String invalidFile = "src/test/resources/test_player_data_withdraw_invalid.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new PlayerDataFileParser().parsePlayerData(invalidFile));
    }

    @Test
    void givenTestPlayerDataFileInvalidBetAmount_whenParsingPlayerData_thenThrowsRuntimeException(){
        String invalidFile = "src/test/resources/test_player_data_bet_with_invalid_amount_and_side.txt";

        Assertions.assertThrows(RuntimeException.class, () -> new PlayerDataFileParser().parsePlayerData(invalidFile));
    }

    @Test
    void givenOnlyLegalPlayers_whenProcessingTransactions_thenResultFileHasNoIllegalPlayers() throws IOException {
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
    void givenOnlyIllegalPlayers_whenProcessingTransactions_thenResultFileHasNoLegalPlayers() throws IOException {
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
    void givenSampleData_whenProcessingTransactions_thenResultFileHasCorrectContent() throws IOException {
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
