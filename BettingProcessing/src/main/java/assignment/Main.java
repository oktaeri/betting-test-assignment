package assignment;

import assignment.model.Match;
import assignment.model.Player;
import assignment.model.ResultData;
import assignment.services.TransactionProcessor;
import util.MatchDataFileParser;
import util.PlayerDataFileParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // TODO: figure out how to get code coverage to 100%, parse results to file, fix casino balance
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        String playerDataFilePath = "src/main/resources/player_data.txt";
        String matchDataFilePath = "src/main/resources/match_data.txt";

        List<Player> players = playerParser.parsePlayerData(playerDataFilePath);
        List<Match> matches = matchParser.parseMatchData(matchDataFilePath);

        TransactionProcessor processor = new TransactionProcessor(players, matches);

        ResultData resultData = processor.processTransactions();
    }
}
