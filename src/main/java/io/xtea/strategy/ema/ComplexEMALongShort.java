package io.xtea.strategy.ema;

import io.xtea.KLineFetcher;
import io.xtea.btc.BTCKlineFetcher;
import io.xtea.core.KLine;
import io.xtea.core.Order;
import io.xtea.core.TradeResult;
import io.xtea.core.TradeSimulator;
import io.xtea.core.TradeUtils;
import io.xtea.strategy.BanlanceException;
import io.xtea.strategy.test.MockTradeExecutor;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Failed.
 *
 * @author xtea
 * @date 2023-03-30 21:56
 */
public class ComplexEMALongShort {

    MockTradeExecutor executor = new MockTradeExecutor();

    KLineFetcher kLineFetcher = new BTCKlineFetcher();

    EMACalculator emaCalculator = new EMACalculator(2 / 13.0);

    String warmUpStartDate;
    String warmUpEndDate;
    String startDate;
    String endDate;

    public ComplexEMALongShort(String warmUpStartDate, String warmUpEndDate, String startDate) {
        this.warmUpStartDate = warmUpStartDate;
        this.warmUpEndDate = warmUpEndDate;
        this.startDate = startDate;
    }

    public ComplexEMALongShort(String warmUpStartDate, String warmUpEndDate, String startDate, String endDate) {
        this.warmUpStartDate = warmUpStartDate;
        this.warmUpEndDate = warmUpEndDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Map<String, EMACalculator> buildMonth(String interval, String startDate) throws Exception {
        Map<String, EMACalculator> timeCalculator = new LinkedHashMap<>();
        EMACalculator emaCalculator = new EMACalculator(2 / 13.0);
        List<KLine> lines = kLineFetcher.query(startDate, endDate, interval);
        for (KLine line : lines) {
            emaCalculator.calculateEMA(line.getClosePrice());
            timeCalculator.put(TradeUtils.toMonth(line.getOpenTime()), emaCalculator.toBuilder().build());
        }
        return timeCalculator;
    }

    public EMACalculator execute(String interval) throws Exception {

//        Map<String, EMACalculator> monthCalculatorMap = buildMonth("1M", warmUpStartDate);
        TradeSimulator simulator = new TradeSimulator(1000);
        // warm up.
        List<KLine> lines = kLineFetcher.query(warmUpStartDate, warmUpEndDate, interval);
        for (KLine line : lines) {
            emaCalculator.calculateEMA(line.getClosePrice());
        }
        System.out.println("Warp up done with price" + emaCalculator.getAvgPrice());
        lines = kLineFetcher.query(startDate, endDate, interval);
        try {
            for (KLine line : lines) {
//                EMACalculator monthEmaCalculator = monthCalculatorMap.get(TradeUtils.toMonth(line.getOpenTime()));
//                if (monthEmaCalculator == null) {
//                    throw new IllegalStateException("month calculator is null " + TradeUtils.toMonth(line.getOpenTime()));
//                }

                this.emaCalculator.calculateEMA(line.getClosePrice());
                if (line.getClosePrice() >= this.emaCalculator.getAvgPrice()) {
                    // buy.
                    if (executor.isEmpty()) {
                        executor.buy(line.getClosePrice(), line.getOpenTime());
                    }
                }
                if (line.getClosePrice() < this.emaCalculator.getAvgPrice()) {
                    if (!executor.isEmpty()) {
                        TradeResult tradeResult = executor.sell(Order.builder().price(line.getClosePrice()).time(line.getOpenTime()).build());
                        simulator.add(tradeResult);
                    }
                }
                String ans = line.getClosePrice() >= this.emaCalculator.getAvgPrice() ? "LONG" : "SHORT";
                System.out.println(TradeUtils.DATE_FORMAT.format(new Date(line.getOpenTime())) + " " + line.getClosePrice() + " " + this.emaCalculator.getAvgPrice() + " " + ans);
            }
            if (!executor.isEmpty()) {
                KLine line = lines.get(lines.size() - 1);
                TradeResult tradeResult = executor.sell(Order.builder().price(line.getClosePrice()).time(line.getOpenTime()).build());
                simulator.add(tradeResult);
            }
        } catch (BanlanceException e) {
            System.err.println(e.getTradeResult());
        }
        System.out.println(emaCalculator);
        System.out.println(simulator);
        return emaCalculator;
    }

    public static void main(String[] args) throws Exception {
        ComplexEMALongShort simpleEMALongShort = new ComplexEMALongShort("2019-06-30", "2020-01-01", "2020-01-02", "2023-03-31");
        String[] timeList = {
                "1w",
        };
        for (String interval : timeList) {
            System.out.println(interval + " \n");
            simpleEMALongShort.execute(interval);
        }
    }

}
