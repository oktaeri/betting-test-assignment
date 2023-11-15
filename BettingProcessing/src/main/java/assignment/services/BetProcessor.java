package assignment.services;

import assignment.model.Match;
import assignment.model.MatchResult;
import assignment.model.Player;
import assignment.model.Transaction;

import java.math.BigDecimal;
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

    private boolean isBetValid(Player player, int betAmount){
        return player.getBalance() > betAmount;
    }

    public int processBet(Player player, Transaction transaction){
        Match match = findMatchById(transaction.getMatchId());

        int betAmount = transaction.getCoinsAmount();
        if (!isBetValid(player, betAmount)){
            return 0;
        }
        if (match.getResult() == transaction.getBetSide()){

            BigDecimal returnRate = (transaction.getBetSide() == MatchResult.A) ?
                    match.getReturnRateA() :
                    match.getReturnRateB();

            BigDecimal winnings = returnRate.multiply(BigDecimal.valueOf(betAmount));

            player.setBalance(player.getBalance() + winnings.intValue());
            player.setMatchesWon(player.getMatchesWon() + 1);

            return 0;
        } else {
            player.setBalance(player.getBalance() - betAmount);

            return betAmount;
        }
    }
}
