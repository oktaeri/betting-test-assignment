package assignment;

import assignment.model.Match;
import assignment.model.Player;
import assignment.services.TransactionProcessor;
import util.MatchDataFileParser;
import util.PlayerDataFileParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        PlayerDataFileParser playerParser = new PlayerDataFileParser();
        MatchDataFileParser matchParser = new MatchDataFileParser();

        String playerDataFilePath = "src/main/resources/player_data.txt";
        String matchDataFilePath = "src/main/resources/match_data.txt";

        List<Player> players = playerParser.parsePlayerData(playerDataFilePath);
        List<Match> matches = matchParser.parseMatchData(matchDataFilePath);

        TransactionProcessor processor = new TransactionProcessor(players, matches);

        processor.processTransactions();
    }
}
