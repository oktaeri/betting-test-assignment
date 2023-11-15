package assignment.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Player {
    @NonNull
    private UUID id;
    private int balance = 0;
    private List<Transaction> transactions = new ArrayList<>();
    private int matchesWon = 0;
    private Transaction illegalAction;
}
