package assignment.util;

import assignment.model.Player;
import assignment.model.ResultData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ResultDataToFile {
    public void write(ResultData data, String filepath){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
            if (data.getLegalPlayers().isEmpty()){
                writer.write("\n");
            } else {
                for (Player legalPlayer: data.getLegalPlayers()) {
                    writer.write(legalPlayer.toString() + "\n");
                }
            }

            writer.write("\n");

            if (data.getIllegalPlayers().isEmpty()){
                writer.write("\n");
            } else {
                for (Player illegalPlayer: data.getIllegalPlayers()) {
                    writer.write(illegalPlayer.illegalToString() + "\n");
                }
            }

            writer.write("\n");

            writer.write(String.valueOf(data.getCasinoBalance()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
