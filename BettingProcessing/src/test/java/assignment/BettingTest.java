package assignment;

import assignment.model.*;
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
        BettingData bettingData = new BettingData();

        bettingData.setPlayers(parser.parsePlayerData(filepath));

        Assertions.assertEquals(bettingData.getPlayers().size(), 2);
    }

    @Test
    public void playersGetDefaultBalanceAfterParsing(){
        String filepath = "src/main/resources/player_data.txt";
        PlayerDataFileParser parser = new PlayerDataFileParser();
        BettingData bettingData = new BettingData();

        bettingData.setPlayers(parser.parsePlayerData(filepath));

        for (Player player : bettingData.getPlayers()) {
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

}
