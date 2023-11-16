package assignment.services;

import assignment.model.Player;
import assignment.model.Transaction;

public class WithdrawalService {
    public void processWithdrawal(Player player, Transaction transaction){
        int playerBalance = player.getBalance();
        int transactionCoinsAmount = transaction.getCoinsAmount();

        if (playerBalance > transactionCoinsAmount){
            player.setBalance(player.getBalance() - transactionCoinsAmount);
        } else {
            player.setIllegalAction(transaction);
        }
    }
}
