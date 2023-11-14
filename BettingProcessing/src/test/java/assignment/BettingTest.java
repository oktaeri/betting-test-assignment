package assignment;

import assignment.model.BettingData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.FileParser;

import java.io.File;

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

    @Test public void playersGetParsedOnlyOnce(){
        String filepath = "src/main/resources/player_data.txt";
        FileParser parser = new FileParser();
        BettingData bettingData = new BettingData();

        bettingData.setPlayers(parser.parsePlayerData(filepath));

        Assertions.assertEquals(bettingData.getPlayers().size(), 2);
    }
}
