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
    public void matchDataFileExists(){
        String filepath = "src/main/resources/match_data.txt";

        Assertions.assertTrue(new File(filepath).isFile());
    }

    @Test
    public void playerDataFileExists(){
        String filepath = "src/main/resources/player_data.txt";

        Assertions.assertTrue(new File(filepath).isFile());
    }

    @Test
    public void playersGetParsedOnlyOnce(){
        String filepath = "src/main/resources/player_data.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Assertions.assertEquals(players.size(), 2);
    }

    @Test
    public void playersGetDefaultBalanceAfterParsing(){
        String filepath = "src/main/resources/player_data.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        for (Player player : players) {
            Assertions.assertEquals(player.getBalance(), 0);
        }
    }

    @Test
    public void playerDataFileWithOneDataLineReturnsOnePlayer(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Assertions.assertEquals(players.size(), 1);
    }

    @Test
    public void transactionGetsAddedToPlayer(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);

        Assertions.assertEquals(player.getTransactions().size(), 1);
    }

    @Test
    public void correctTransactionTypeGetsAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_empty_bet.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(transaction.getTransactionType(), TransactionType.BET);
    }

    @Test
    public void correctCoinsAmountGetsAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(transaction.getCoinsAmount(), 550);
    }

    @Test
    public void invalidCoinsAmountDoesNotGetAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_invalid_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(transaction.getCoinsAmount(), 0);
    }

    @Test
    public void correctBetSideGetsAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_amount_and_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(transaction.getBetSide(), MatchResult.B);
    }

    @Test
    public void invalidBetSideDoesNotGetAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_bet_with_amount_and_invalid_side.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertNull(transaction.getBetSide());
    }

    @Test
    public void allMatchesGetParsedToClass(){
        String filepath = "src/main/resources/match_data.txt";
        MatchDataFileParser parser = new MatchDataFileParser();
        List<Match> matches = parser.parseMatchData(filepath);

        Assertions.assertEquals(matches.size(), 13);
    }

    @Test
    public void depositedAmountGetsAddedToPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(player.getBalance(), 550);
    }

    @Test
    public void legalWithdrawalAmountGetsDeductedFromPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(player.getBalance(), 350);
    }

    @Test
    public void illegalWithdrawalAmountDoesNotGetDeductedFromPlayerBalance(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertEquals(player.getBalance(), 550);
    }

    @Test
    public void illegalWithdrawalGetsAddedToPlayerIllegalActions(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_illegal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNotNull(player.getIllegalAction());
    }

    @Test
    public void illegalWithdrawalDoesNotGetAddedToPlayerIllegalActions(){
        String filepath = "src/test/resources/test_player_data_deposit_and_withdraw_legal.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        TransactionProcessor transactionProcessor = new TransactionProcessor(players, null);
        transactionProcessor.processTransactions();

        Player player = players.get(0);

        Assertions.assertNull(player.getIllegalAction());
    }

    @Test
    public void playerWinsBetAndGetsCoinsAddedToBalance(){
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
    public void playerWinsBetAndWonMatchesCountIncreases(){
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
    public void playerLosesBetAndPlayerBalanceDecreases(){
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
    public void playerWinsBetAndCasinoBalanceIsNotAffected(){
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
    public void playerLosesBetAndCasinoBalanceIncreases(){
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
    public void illegalBetIsAddedToPlayerIllegalAction(){
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
    public void illegalBetDoesNotAffectCasinoBalance(){
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
