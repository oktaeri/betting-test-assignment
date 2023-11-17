package assignment.util;

import assignment.model.Player;
import assignment.model.ResultData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ResultDataToFile {
    private String getResultString(ResultData data){
        StringBuilder resultString = new StringBuilder();

        if (data.getLegalPlayers().isEmpty()) {
            resultString.append("\n");
        } else {
            for (Player legalPlayer : data.getLegalPlayers()) {
                resultString.append(legalPlayer.toString() + "\n");
            }
        }

        resultString.append("\n");

        if (data.getIllegalPlayers().isEmpty()) {
            resultString.append("\n");
        } else {
            for (Player illegalPlayer : data.getIllegalPlayers()){
                resultString.append(illegalPlayer.illegalToString() + "\n");
            }
        }

        resultString.append("\n");
        resultString.append(data.getCasinoBalance());

        return resultString.toString();
    }
    public void write(ResultData data, String filepath){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
            writer.write(getResultString(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
