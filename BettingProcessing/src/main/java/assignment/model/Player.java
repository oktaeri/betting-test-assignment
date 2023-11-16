package assignment.model;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Player {
    @NonNull
    private UUID id;
    private int balance = 0;
    private List<Transaction> transactions = new ArrayList<>();
    private int matchesWon = 0;
    private int profit = 0;
    private int loss = 0;
    private Transaction illegalAction;

    private BigDecimal getMatchCount(){
        BigDecimal matches = BigDecimal.valueOf(0);
        for (Transaction transaction: transactions) {
            if (transaction.getTransactionType() == TransactionType.BET){
                matches = matches.add(BigDecimal.valueOf(1));
            }
        }
        return matches;
    }

    public BigDecimal getWinRatio(){
        return  BigDecimal.valueOf(matchesWon)
                .divide(getMatchCount(), 2, RoundingMode.HALF_UP);

    }

    public String toString(){
        return this.id + " " + this.balance + " " + getWinRatio().toString().replace(".", ",");
    }

    public String illegalToString() {
        return this.id + " " + illegalAction.toString();
    }

}
