package io.xtea.core;

import io.xtea.strategy.BanlanceException;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 21:35
 */
public class TradeSimulator {

    List<TradeResult> tradeResults = new ArrayList<>();

    private double initBalance;

    private double balance;

    public TradeSimulator(double balance) {
        this.initBalance = balance;
        this.balance = balance;
    }

    public void add(TradeResult tradeResult) throws BanlanceException {
        balance *= (1 + tradeResult.winPercent());
        tradeResult.setBalance(balance);
        if (balance <= 0) {
            throw new BanlanceException(tradeResult);
        }
        tradeResults.add(tradeResult);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TradeSimulator{");
        sb.append("balance=").append(balance + " ");
        double win = getWin();
        sb.append("win=").append(win + "% ");
        sb.append("\nTradeResult=").append(tradeResults);
        sb.append('}');
        return sb.toString();
    }

    public double getWin() {
        return (balance - initBalance) / initBalance * 100D;
    }

    public String printCsv() {
        StringBuilder sb = new StringBuilder("buy,sell,win,balance \n");
        for (TradeResult tr : tradeResults) {
            sb.append(tr.csv());
        }
        return sb.toString();
    }
}
