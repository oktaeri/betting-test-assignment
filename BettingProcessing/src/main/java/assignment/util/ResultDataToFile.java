package assignment.util;

import assignment.model.Player;
import assignment.model.ResultData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public class ResultDataToFile {
    private String getResultString(ResultData data){
        String legalPlayersData = data.getLegalPlayers().stream()
                .map(Player::toString)
                .collect(Collectors.joining("\n"));

        String illegalPlayersData = data.getIllegalPlayers().stream()
                .map(Player::illegalToString)
                .collect(Collectors.joining("\n"));

        return legalPlayersData + "\n\n" + illegalPlayersData + "\n\n" + data.getCasinoBalance();
    }
    public void write(ResultData data, String filepath){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
            writer.write(getResultString(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
