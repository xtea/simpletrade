package io.xtea.core;

import lombok.Builder;
import lombok.Data;

import java.text.NumberFormat;
import java.util.Date;

/**
 * Trade result.
 *
 * @author xtea
 * @date 2023-03-30 21:22
 */
@Data
@Builder
public class TradeResult {

    Order buy;
    Order sell;
    double balance;

    public boolean isWin() {
        return winMoney() > 0;
    }

    public double winPercent() {
        return (sell.getPrice() - buy.getPrice()) / buy.getPrice();
    }

    public String winPrint() {
        return String.format(".2f%%", winPercent() * 100);
    }

    public double winMoney() {
        return sell.getPrice() - buy.getPrice();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TradeResult{");
        sb.append("buy=").append(buy);
        sb.append(", sell=").append(sell);
        sb.append(", winP=").append(String.format("%.2f%%", winPercent() * 100));
        sb.append(", balance=").append(NumberFormat.getInstance().format(balance));
        sb.append("}\n");
        return sb.toString();
    }

    public String csv(){
        final StringBuilder sb = new StringBuilder();
        sb.append(TradeUtils.DATE_FORMAT.format(new Date(getBuy().getTime())) + ",");
        sb.append(TradeUtils.DATE_FORMAT.format(new Date(getSell().getTime())) + ",");
        sb.append(String.format("%.2f%%", winPercent() * 100) + ",");
        sb.append("\"" +NumberFormat.getInstance().format(balance) + "\"\n");
        return sb.toString();
    }
}
