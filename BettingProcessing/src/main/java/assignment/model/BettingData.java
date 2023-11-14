package assignment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.*;

@Getter
@Setter
public class BettingData {
    private List<Player> players;
    private List<Match> matches;
}
