package assignment.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Player {
    @NonNull
    private UUID id;
    private int balance = 0;
}
