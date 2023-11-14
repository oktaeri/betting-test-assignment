package assignment;

import assignment.model.BettingData;
import assignment.model.Player;
import util.FileParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        BettingData bettingData = new BettingData();
        FileParser parser = new FileParser();

        String playerDataFilePath = "src/main/resources/player_data.txt";

        bettingData.setPlayers(parser.parsePlayerData(playerDataFilePath));

        for (Player player : bettingData.getPlayers()) {
            System.out.println(player.getId());
            System.out.println(player.getBalance());
        }
    }
}
