package assignment.model;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Transaction {
    @NonNull
    private TransactionType transactionType;
    private UUID matchId;
    private int coinsAmount;
    private MatchResult betSide;

    @Override
    public String toString(){
        return transactionType + " " + matchId + " " + coinsAmount + " " + betSide;
    }
}
