package assignment;

import assignment.model.*;
import assignment.services.TransactionProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.MatchDataFileParser;
import util.PlayerDataFileParser;

import java.io.File;
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

        Assertions.assertEquals(players.size(), 2);
    }

    @Test
    public void givenPlayerDataFile_whenParsingPlayers_thenPlayersGetDefaultBalanceAfterParsing(){
        String filepath = "src/main/resources/player_data.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        for (Player player : players) {
            Assertions.assertEquals(player.getBalance(), 0);
        }
    }

    @Test
    public void givenPlayerDataFileWithOneDataLine_whenParsingPlayers_thenReturnsOnePlayer(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Assertions.assertEquals(players.size(), 1);
    }

    @Test
    public void givenTestPlayerDataFileEmptyBet_whenParsingPlayers_thenTransactionGetsAddedToPlayer(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);

        Assertions.assertEquals(player.getTransactions().size(), 1);
    }

    @Test
    public void givenTestPlayerDataFileEmptyBet_whenParsingPlayers_thenCorrectTransactionTypeAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(transaction.getTransactionType(), TransactionType.BET);
    }

    @Test
    public void givenTestPlayerDataDepositFile_whenParsingPlayers_thenCorrectCoinsAmountAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(transaction.getCoinsAmount(), 550);
    }

    @Test
    public void givenTestPlayerDataBetWithInvalidAmountAndSide_whenParsingPlayers_thenInvalidCoinsAmountDoesNotGetAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_invalid_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(transaction.getCoinsAmount(), 0);
    }

    @Test
    public void givenTestPlayerDataBetWithAmountAndSide_whenParsingPlayers_thenCorrectBetSideAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(transaction.getBetSide(), MatchResult.B);
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

        Assertions.assertEquals(matches.size(), 13);
    }

    @Test
    public void givenTestPlayerDataFileDeposit_whenProcessingTransactions_thenDepositedAmountGetsAddedToPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(player.getBalance(), 550);
    }

    @Test
    public void givenTestPlayerDataFileDepositAndWithdrawLegal_whenProcessingTransactions_thenLegalWithdrawalAmountGetsDeductedFromPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(player.getBalance(), 350);
    }

    @Test
    public void givenTestPlayerDataFileDepositAndWithdrawIllegal_whenProcessingTransactions_thenIllegalWithdrawalAmountDoesNotGetDeductedFromPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(player.getBalance(), 550);
    }

    @Test
    public void givenTestPlayerDataFileDepositAndWithdrawIllegal_whenProcessingTransactions_thenIllegalWithdrawalGetsAddedToPlayerIllegalActions(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNotNull(player.getIllegalAction());
    }

    @Test
    public void givenTestPlayerDataFileDepositAndWithdrawLegal_whenProcessingTransactions_thenIllegalWithdrawalDoesNotGetAddedToPlayerIllegalActions(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
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

        TransactionProcessor transactionProcessor = new TransactionProcessor(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(player.getBalance(), 1275);
    }

    @Test
    public void givenTestPlayerDataFileDepositAndBetWinningSide_whenProcessingTransactions_thenPlayerWinsBetAndWonMatchesCountIncreases(){
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_winning_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionProcessor transactionProcessor = new TransactionProcessor(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(player.getMatchesWon(), 1);
    }

    @Test
    public void givenTestPlayerDataFileDepositAndBetLosingSide_whenProcessingTransactions_thenPlayerLosesBetAndPlayerBalanceDecreases(){
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_losing_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionProcessor transactionProcessor = new TransactionProcessor(players, matches);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(player.getBalance(), 50);
    }

    @Test
    public void givenTestPlayerDataFileDepositAndBetWinningSide_whenProcessingTransactions_thenPlayerWinsBetAndCasinoBalanceIsNotAffected(){
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_winning_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionProcessor transactionProcessor = new TransactionProcessor(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(result.getCasinoBalance(), 0);
    }

    @Test
    public void givenTestPlayerDataFileDepositAndBetLosingSide_whenProcessingTransactions_thenPlayerLosesBetAndCasinoBalanceIncreases(){
        String playerFilepath = "src/test/resources/test_player_data_deposit_and_bet_losing_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionProcessor transactionProcessor = new TransactionProcessor(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(result.getCasinoBalance(), 500);
    }

    @Test
    public void givenTestPlayerDataFileBetWithAmountAndSide_whenProcessingTransactions_thenIllegalBetIsAddedToPlayerIllegalAction(){
        String playerFilepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        String matchesFilepath = "src/main/resources/match_data.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        List<Player> players = playerParser.parsePlayerData(playerFilepath);
        List<Match> matches = matchParser.parseMatchData(matchesFilepath);

        TransactionProcessor transactionProcessor = new TransactionProcessor(players, matches);
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

        TransactionProcessor transactionProcessor = new TransactionProcessor(players, matches);
        ResultData result = transactionProcessor.processTransactions();

        Assertions.assertEquals(result.getCasinoBalance(), 0);
    }
}
