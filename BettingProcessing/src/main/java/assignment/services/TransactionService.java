package assignment.services;

import assignment.model.*;

import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private final List<Player> players;
    private final BetService betProcessor;

    public TransactionService(List<Player> players, List<Match> matches) {
        this.players = players;
        this.betProcessor = new BetService(matches);
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

    private long getCasinoBalance(List<Player> legalPlayers){
        long casinoBalance = 0;

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
        WithdrawalService withdrawalProcessor = new WithdrawalService();
        DepositService depositProcessor = new DepositService();

        switch (transaction.getTransactionType()) {
            case DEPOSIT -> depositProcessor.processDeposit(player, transaction);
            case WITHDRAW -> withdrawalProcessor.processWithdrawal(player, transaction);
            case BET -> betProcessor.processBet(player, transaction);
        }
    }
}
