package assignment;

import assignment.model.BettingData;
import assignment.model.Player;
import util.PlayerDataFileParser;

public class Main {
    public static void main(String[] args) {
        BettingData bettingData = new BettingData();
        PlayerDataFileParser parser = new PlayerDataFileParser();

        String playerDataFilePath = "src/main/resources/player_data.txt";
        String matchDataFilePath = "src/main/resources/match_data.txt";

        bettingData.setPlayers(parser.parsePlayerData(playerDataFilePath));

        for (Player player : bettingData.getPlayers()) {
            System.out.println(player.getId());
            System.out.println(player.getBalance());
        }
    }
}
