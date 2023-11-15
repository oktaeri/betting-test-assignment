package assignment.services;

import assignment.model.Match;
import assignment.model.Player;
import assignment.model.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BetProcessor {
    private final List<Match> matches;

    public BetProcessor(List<Match> matches) {
        this.matches = matches;
    }

    private Match findMatchById(UUID matchId) {
        return matches.stream()
                .filter(player -> Objects.equals(player.getId(), matchId))
                .findFirst()
                .orElse(null);
    }

    public void processBet(Player player, Transaction transaction){

    }
}
