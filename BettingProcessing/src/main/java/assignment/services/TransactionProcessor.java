package assignment.services;

import assignment.model.Match;
import assignment.model.Player;
import assignment.model.Transaction;
import assignment.model.TransactionType;

import java.util.List;

public class TransactionProcessor {
    private List<Player> players;
    private List<Match> matches;

    public TransactionProcessor(List<Player> players, List<Match> matches) {
        this.players = players;
        this.matches = matches;
    }

    public void processTransactions(){
        for (Player player : players){
            for (Transaction transaction : player.getTransactions()) {
                processTransaction(player, transaction);
            }
        }
    }

    private void processTransaction(Player player, Transaction transaction){
        TransactionType transactionType = transaction.getTransactionType();

        switch (transaction.getTransactionType()) {
            case DEPOSIT:
                processDeposit(player, transaction);
                break;
            case WITHDRAW:
                processWithdrawal(player, transaction);
                break;
            case BET:
                processBet(player, transaction);
                break;
        }
    }

    private void processDeposit(Player player, Transaction transaction){
        player.setBalance(player.getBalance() + transaction.getCoinsAmount());
    }

    private void processWithdrawal(Player player, Transaction transaction){
        int playerBalance = player.getBalance();
        int transactionCoinsAmount = transaction.getCoinsAmount();

        if (playerBalance > transactionCoinsAmount){
            player.setBalance(player.getBalance() - transaction.getCoinsAmount());
        } else {
            player.setIllegalAction(transaction);
        }
    }

    private void processBet(Player player, Transaction transaction){

    }
}
