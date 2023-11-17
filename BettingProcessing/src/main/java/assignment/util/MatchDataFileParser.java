package assignment.util;

import assignment.model.Match;
import assignment.model.MatchResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchDataFileParser {
    public List<Match> parseMatchData(String filePath){
        List<Match> matches = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] splitLine = line.split(",");
                UUID matchId = UUID.fromString(splitLine[0]);
                BigDecimal winRateA = new BigDecimal(splitLine[1]);
                BigDecimal winRateB = new BigDecimal(splitLine[2]);
                MatchResult result = MatchResult.valueOf(splitLine[3]);

                matches.add(new Match(matchId, winRateA, winRateB, result));
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return matches;
    }
}
