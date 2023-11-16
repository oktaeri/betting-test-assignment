package assignment.services;

import assignment.model.Player;
import assignment.model.Transaction;

public class DepositService {
    public void processDeposit(Player player, Transaction transaction){
        player.setBalance(player.getBalance() + transaction.getCoinsAmount());
    }
}
