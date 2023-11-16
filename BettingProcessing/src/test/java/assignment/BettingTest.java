package assignment;

import assignment.model.*;
import assignment.services.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.MatchDataFileParser;
import util.PlayerDataFileParser;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

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
        String filepath = "src/test/resources/test_player_data_bet_with_invalid_amount_and_side.txt";
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

        Assertions.assertEquals(player.toString(), "163f23ed-e9a9-4e54-a5b1-4e1fc86f12f4 2725 0,14");
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

        Assertions.assertEquals(player.toString(), "4925ac98-833b-454b-9342-13ed3dfd3ccf WITHDRAW null 8093 null");
    }
}
