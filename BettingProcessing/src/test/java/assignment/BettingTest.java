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

public class BettingTest {
    @Test
    public void givenMatchDataFile_whenCheckingExistence_thenFileExists(){
        String filepath = "src/main/resources/match_data.txt";

        Assertions.assertTrue(new File(filepath).isFile());
    }

    @Test
    public void givenPlayerDataFile_whenCheckingExistence_thenFileExists(){
        String filepath = "src/main/resources/player_data.txt";

        Assertions.assertTrue(new File(filepath).isFile());
    }

    @Test
    public void givenPlayerDataFile_whenParsingPlayers_thenPlayersParsedOnce(){
        String filepath = "src/main/resources/player_data.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Assertions.assertEquals(2, players.size());
    }

    @Test
    public void givenPlayerDataFile_whenParsingPlayers_thenPlayersGetDefaultBalanceAfterParsing(){
        String filepath = "src/main/resources/player_data.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        for (Player player : players) {
            Assertions.assertEquals(0, player.getBalance());
        }
    }

    @Test
    public void givenPlayerDataFileWithOneDataLine_whenParsingPlayers_thenReturnsOnePlayer(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Assertions.assertEquals(1, players.size());
    }

    @Test
    public void givenTestPlayerDataFileEmptyBet_whenParsingPlayers_thenTransactionGetsAddedToPlayer(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);

        Assertions.assertEquals(1, player.getTransactions().size());
    }

    @Test
    public void givenTestPlayerDataFileEmptyBet_whenParsingPlayers_thenCorrectTransactionTypeAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(TransactionType.BET, transaction.getTransactionType());
    }

    @Test
    public void givenTestPlayerDataDepositFile_whenParsingPlayers_thenCorrectCoinsAmountAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(550, transaction.getCoinsAmount());
    }

    @Test
    public void givenTestPlayerDataBetWithInvalidAmountAndSide_whenParsingPlayers_thenInvalidCoinsAmountDoesNotGetAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_negative_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(0, transaction.getCoinsAmount());
    }

    @Test
    public void givenTestPlayerDataBetWithAmountAndSide_whenParsingPlayers_thenCorrectBetSideAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(MatchResult.B, transaction.getBetSide());
    }

    @Test
    public void givenTestPlayerDataFileBetWithInvalidSide_whenParsingPlayers_thenInvalidBetSideDoesNotGetAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_amount_and_invalid_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertNull(transaction.getBetSide());
    }

    @Test
    public void givenMatchDataFile_whenParsingMatches_thenAllMatchesGetParsedToMatchClass(){
        String filepath = "src/main/resources/match_data.txt";
        MatchDataFileParser parser = new MatchDataFileParser();
        List<Match> matches = parser.parseMatchData(filepath);

        Assertions.assertEquals(13, matches.size());
    }

    @Test
    public void givenTestPlayerDataFileDeposit_whenProcessingTransactions_thenDepositedAmountGetsAddedToPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(550, player.getBalance());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndWithdrawLegal_whenProcessingTransactions_thenLegalWithdrawalAmountGetsDeductedFromPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(350, player.getBalance());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndWithdrawIllegal_whenProcessingTransactions_thenIllegalWithdrawalAmountDoesNotGetDeductedFromPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(550, player.getBalance());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndWithdrawIllegal_whenProcessingTransactions_thenIllegalWithdrawalGetsAddedToPlayerIllegalActions(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNotNull(player.getIllegalAction());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndWithdrawLegal_whenProcessingTransactions_thenIllegalWithdrawalDoesNotGetAddedToPlayerIllegalActions(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionService transactionProcessor = new TransactionService(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNull(player.getIllegalAction());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndBetWinningSide_whenProcessingTransactions_thenPlayerWinsBetAndGetsCoinsAddedToBalance(){
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_winning_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(1275, player.getBalance());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndBetWinningSide_whenProcessingTransactions_thenPlayerWinsBetAndWonMatchesCountIncreases(){
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_winning_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(1, player.getMatchesWon());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndBetLosingSide_whenProcessingTransactions_thenPlayerLosesBetAndPlayerBalanceDecreases(){
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_losing_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(50, player.getBalance());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndBetWinningSide_whenProcessingTransactions_thenPlayerWinsBetAndCasinoBalanceIsNegative(){
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_winning_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(-725, result.getCasinoBalance());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndBetLosingSide_whenProcessingTransactions_thenPlayerLosesBetAndCasinoBalanceIncreases(){
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_losing_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(500, result.getCasinoBalance());
    }

    @Test
    public void givenTestPlayerDataFileBetWithAmountAndSide_whenProcessingTransactions_thenIllegalBetIsAddedToPlayerIllegalAction() {
        String playerFilepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNotNull(player.getIllegalAction());
    }

    @Test
    public void givenTestPlayerDataFileBetWithAmountAndSide_whenProcessingTransactions_thenIllegalBetDoesNotAffectCasinoBalance(){
        String playerFilepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(0, result.getCasinoBalance());
    }

    @Test
    public void givenSamplePlayerDataFile_whenProcessingTransactions_thenCasinoBalanceIsCorrect(){
        String playerFilepath = "src/main/resources/player_data.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(75, result.getCasinoBalance());
    }

    @Test
    public void givenSamplePlayerDataFile_whenProcessingTransactions_thenPlayerBalanceIsCorrect(){
        String playerFilepath = "src/main/resources/player_data.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(2725, player.getBalance());
    }

    @Test
    public void givenSamplePlayerDataFile_whenProcessingTransactions_thenPlayerWinRatioIsCorrect(){
        String playerFilepath = "src/main/resources/player_data.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(BigDecimal.valueOf(0.14), player.getWinRatio());
    }

    @Test
    public void givenSamplePlayerDataFile_whenProcessingTransactions_thenPlayerToStringIsCorrect(){
        String playerFilepath = "src/main/resources/player_data.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals("163f23ed-e9a9-4e54-a5b1-4e1fc86f12f4 2725 0,14", player.toString());
    }

    @Test
    public void givenSamplePlayerDataFile_whenProcessingTransactions_thenIllegalPlayerToStringIsCorrect(){
        String playerFilepath = "src/main/resources/player_data.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionService transactionProcessor = new TransactionService(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(1);

        Assertions.assertEquals("4925ac98-833b-454b-9342-13ed3dfd3ccf WITHDRAW null 8093 null", player.illegalToString());
    }

    @Test
    public void givenPlayerAndIllegalBet_whenProcessingBets_thenIllegalTransactionIsAddedToPlayerIllegalAction(){
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
    public void givenNonExistentFile_whenParsingPlayerData_thenThrowsRuntimeException(){
        String fakeFile = "this_does_not_exist.txt";

        Assertions.assertThrows(RuntimeException.class, () -> {
            new PlayerDataFileParser().parsePlayerData(fakeFile);
        });
    }

    @Test
    public void givenNonExistentFile_whenParsingMatchData_thenThrowsRuntimeException(){
        String fakeFile = "this_does_not_exist.txt";

        Assertions.assertThrows(RuntimeException.class, () -> {
            new MatchDataFileParser().parseMatchData(fakeFile);
        });
    }

    @Test
    public void givenTestPlayerDataFileNegativeDeposit_whenParsingPlayerData_thenDepositAmountGetsChangedToZero(){
        String filepath = "src/test/resources/test_player_data_deposit_negative.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Transaction transaction = players.get(0).getTransactions().get(0);

        Assertions.assertEquals(0, transaction.getCoinsAmount());
    }

    @Test
    public void givenTestPlayerDataFileInvalidDeposit_whenParsingPlayerData_thenThrowsRuntimeException(){
        String invalidFile = "src/test/resources/test_player_data_deposit_invalid.txt";

        Assertions.assertThrows(RuntimeException.class, () -> {
            new PlayerDataFileParser().parsePlayerData(invalidFile);
        });
    }

    @Test
    public void givenTestPlayerDataFileNegativeWithdraw_whenParsingPlayerData_thenDepositAmountGetsChangedToZero(){
        String filepath = "src/test/resources/test_player_data_withdraw_negative.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Transaction transaction = players.get(0).getTransactions().get(0);

        Assertions.assertEquals(0, transaction.getCoinsAmount());
    }

    @Test
    public void givenTestPlayerDataFileInvalidWithdraw_whenParsingPlayerData_thenThrowsRuntimeException(){
        String invalidFile = "src/test/resources/test_player_data_withdraw_invalid.txt";

        Assertions.assertThrows(RuntimeException.class, () -> {
            new PlayerDataFileParser().parsePlayerData(invalidFile);
        });
    }

    @Test
    public void givenTestPlayerDataFileInvalidBetAmount_whenParsingPlayerData_thenThrowsRuntimeException(){
        String invalidFile = "src/test/resources/test_player_data_bet_with_invalid_amount_and_side.txt";

        Assertions.assertThrows(RuntimeException.class, () -> {
            new PlayerDataFileParser().parsePlayerData(invalidFile);
        });
    }

    @Test
    public void givenOnlyLegalPlayers_whenProcessingTransactions_thenResultFileHasNoIllegalPlayers() throws IOException {
        String expected = "163f23ed-e9a9-4e54-a5b1-4e1fc86f12f4 2725 0,14\n" +
                "4925ac98-833b-454b-9342-13ed3dfd3ccf 723 0,50\n" +
                "\n" +
                "\n" +
                "\n" +
                "-148";
        String playerFilepath = "src/test/resources/test_player_data_all_legal.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();
        ResultDataToFile resultDataToFile = new ResultDataToFile();
        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);
        TransactionService transactionProcessor = new TransactionService(players, matches);

        String resultPath = "src/test/resources/test_result.txt";
        resultDataToFile.write(transactionProcessor.processTransactions(), resultPath);

        String fileContent = Files.readString(Paths.get(resultPath));

        Assertions.assertEquals(expected, fileContent);
    }

    @Test
    public void givenOnlyIlegalPlayers_whenProcessingTransactions_thenResultFileHasNoLegalPlayers() throws IOException {
        String expected = "\n" +
                "\n" +
                "163f23ed-e9a9-4e54-a5b1-4e1fc86f12f4 BET a3815c17-9def-4034-a21f-65369f6d4a56 200000 A\n" +
                "4925ac98-833b-454b-9342-13ed3dfd3ccf WITHDRAW null 8093 null\n" +
                "\n" +
                "0";
        String playerFilepath = "src/test/resources/test_player_data_all_illegal.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();
        ResultDataToFile resultDataToFile = new ResultDataToFile();
        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);
        TransactionService transactionProcessor = new TransactionService(players, matches);

        String resultPath = "src/test/resources/test_result.txt";
        resultDataToFile.write(transactionProcessor.processTransactions(), resultPath);

        String fileContent = Files.readString(Paths.get(resultPath));

        Assertions.assertEquals(expected, fileContent);
    }

}
