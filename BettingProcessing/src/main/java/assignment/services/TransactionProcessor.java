package assignment.services;

import assignment.model.*;

import java.util.ArrayList;
import java.util.List;

public class TransactionProcessor {
    private final List<Player> players;
    private final BetProcessor betProcessor;

    public TransactionProcessor(List<Player> players, List<Match> matches) {
        this.players = players;
        this.betProcessor = new BetProcessor(matches);
    }

    public ResultData processTransactions(){
        for (Player player : players){
            for (Transaction transaction : player.getTransactions()) {
                processTransaction(player, transaction);
            }
        }

        List<Player> legalPlayers = new ArrayList<>(players);
        legalPlayers.removeAll(getIllegalPlayers());

        return new ResultData(legalPlayers, getIllegalPlayers(), getCasinoBalance(legalPlayers));
    }

    private int getCasinoBalance(List<Player> legalPlayers){
        int casinoBalance = 0;

        for (Player player : legalPlayers) {
            casinoBalance = casinoBalance + player.getLoss();
            casinoBalance = casinoBalance - player.getProfit();
        }

        return casinoBalance;
    }

    private List<Player> getIllegalPlayers(){
        List<Player> illegalPlayers = new ArrayList<>();

        for (Player player : players) {
            if (player.getIllegalAction() != null) {
                illegalPlayers.add(player);
            }
        }
        return illegalPlayers;
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
            player.setBalance(player.getBalance() - transactionCoinsAmount);
        } else {
            player.setIllegalAction(transaction);
        }
    }
}
