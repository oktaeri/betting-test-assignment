package util;

import assignment.model.Player;
import assignment.model.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileParser {
    public List<Player> parsePlayerData(String filePath){
        List<Player> players = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] splitLine = line.split(",");
                if (!playerInList(splitLine[0], players)){
                    players.add(new Player(UUID.fromString(splitLine[0])));
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return players;
    }

    private boolean playerInList(String playerId, List<Player> players) {
        return players.stream().anyMatch(player -> Objects.equals(player.getId().toString(), playerId));
    }
}
