package assignment;

import assignment.model.Match;
import assignment.model.Player;
import assignment.model.ResultData;
import assignment.services.TransactionService;
import assignment.util.MatchDataFileParser;
import assignment.util.PlayerDataFileParser;
import assignment.util.ResultDataToFile;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String playerDataFilePath = "src/main/resources/player_data.txt";
        String matchDataFilePath = "src/main/resources/match_data.txt";
        String resultFilePath = "src/main/java/assignment/result.txt";

        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();
        ResultDataToFile resultDataToFile = new ResultDataToFile();

        List<Player> players = playerParser.parsePlayerData(playerDataFilePath);
        List<Match> matches = matchParser.parseMatchData(matchDataFilePath);

        TransactionService processor = new TransactionService(players, matches);

        ResultData resultData = processor.processTransactions();
        resultDataToFile.write(resultData, resultFilePath);
    }
}
