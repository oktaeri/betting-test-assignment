package util;

import assignment.model.Player;
import assignment.model.Transaction;
import assignment.model.TransactionType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileParser {
    private Player findPlayerById(UUID playerId, List<Player> players) {
        return players.stream()
                .filter(player -> Objects.equals(player.getId(), playerId))
                .findFirst()
                .orElse(null);
    }

    public List<Player> parsePlayerData(String filePath){
        List<Player> players = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] splitLine = line.split(",");
                UUID playerId = UUID.fromString(splitLine[0]);

                Player player = findPlayerById(playerId, players);
                if (player == null) {
                    player = new Player(playerId);
                    players.add(player);
                }

                processTransaction(player, splitLine);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return players;
    }

    private void processTransaction(Player player, String[] data){
        TransactionType transactionType = TransactionType.valueOf(data[1]);

        UUID matchId = null;
        if (data.length > 2 && !data[2].isEmpty()){
            matchId = UUID.fromString(data[2]);
        }

        Transaction transaction = new Transaction(transactionType, matchId, 0, null);
        player.getTransactions().add(transaction);
    }
}
