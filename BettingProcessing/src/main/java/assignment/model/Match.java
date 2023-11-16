package assignment.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Match {
    @NonNull
    private UUID id;
    @NonNull
    private BigDecimal returnRateA;
    @NonNull
    private BigDecimal returnRateB;
    @NonNull
    private MatchResult result;
}
