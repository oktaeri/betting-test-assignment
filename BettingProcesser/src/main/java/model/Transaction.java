package model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Transaction {
    @NonNull
    private UUID playerId;
    @NonNull
    private String transactionType;
    private UUID matchId;
    @NonNull
    private int coinsAmount;
    private String betSide;
}
