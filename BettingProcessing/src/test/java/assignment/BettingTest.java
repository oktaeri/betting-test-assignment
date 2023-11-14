package assignment;

import assignment.model.BettingData;
import assignment.model.Player;
import assignment.model.Transaction;
import assignment.model.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.FileParser;

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
        FileParser parser = new FileParser();
        BettingData bettingData = new BettingData();

        bettingData.setPlayers(parser.parsePlayerData(filepath));

        Assertions.assertEquals(bettingData.getPlayers().size(), 2);
    }

    @Test
    public void playersGetDefaultBalanceAfterParsing(){
        String filepath = "src/main/resources/player_data.txt";
        FileParser parser = new FileParser();
        BettingData bettingData = new BettingData();

        bettingData.setPlayers(parser.parsePlayerData(filepath));

        for (Player player : bettingData.getPlayers()) {
            Assertions.assertEquals(player.getBalance(), 0);
        }
    }

    @Test
    public void playerDataFileWithOneDataLineReturnsOnePlayer(){
        String filepath = "src/test/resources/test_player_data_1.txt";
        FileParser parser = new FileParser();
        List<Player> players = parser.parsePlayerData(filepath);

        Assertions.assertEquals(players.size(), 1);
    }

    @Test
    public void transactionGetsAddedToPlayer(){
        String filepath = "src/test/resources/test_player_data_1.txt";
        FileParser parser = new FileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);

        Assertions.assertEquals(player.getTransactions().size(), 1);
    }

    @Test
    public void correctTransactionTypeGetsAddedToPlayerTransactions(){
        String filepath = "src/test/resources/test_player_data_1.txt";
        FileParser parser = new FileParser();
        List<Player> players = parser.parsePlayerData(filepath);
        Player player = players.get(0);
        Transaction transaction = player.getTransactions().get(0);

        Assertions.assertEquals(transaction.getTransactionType(), TransactionType.BET);
    }
}
