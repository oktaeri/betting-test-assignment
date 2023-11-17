package assignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResultData {
    private List<Player> legalPlayers;
    private List<Player> illegalPlayers;
    private long casinoBalance;
}
