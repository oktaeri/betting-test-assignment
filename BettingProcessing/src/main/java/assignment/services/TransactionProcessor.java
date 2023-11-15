package assignment.services;

import assignment.model.Match;
import assignment.model.Player;
import assignment.model.Transaction;
import assignment.model.TransactionType;

import java.util.List;

public class TransactionProcessor {
    private final List<Player> players;
    private final List<Match> matches;
    private final BetProcessor betProcessor;

    public TransactionProcessor(List<Player> players, List<Match> matches) {
        this.players = players;
        this.matches = matches;
        this.betProcessor = new BetProcessor(matches);
    }

    public void processTransactions(){
        for (Player player : players){
            for (Transaction transaction : player.getTransactions()) {
                processTransaction(player, transaction);
            }
        }
    }

    private void processTransaction(Player player, Transaction transaction){
        switch (transaction.getTransactionType()) {
            case DEPOSIT -> processDeposit(player, transaction);
            case WITHDRAW -> processWithdrawal(player, transaction);
            case BET -> betProcessor.processBet(player, transaction);
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
}
