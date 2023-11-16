package assignment.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Transaction {
    @NonNull
    private TransactionType transactionType;
    private UUID matchId;
    private int coinsAmount;
    private MatchResult betSide;

    public String toString(){
        return transactionType + " " + matchId + " " + coinsAmount + " " + betSide;
    }
}
