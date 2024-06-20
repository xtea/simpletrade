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
import java.util.List;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 21:56
 */
public class SimpleEMALongShort {

    MockTradeExecutor executor = new MockTradeExecutor();

    KLineFetcher kLineFetcher;

    EMACalculator emaCalculator = new EMACalculator(2 / 13.0);

    String warmUpStartDate;
    String warmUpEndDate;
    String startDate;

    boolean debug = true;

    public SimpleEMALongShort(String warmUpStartDate, String warmUpEndDate, String startDate, KLineFetcher kLineFetcher, EMACalculator emaCalculator) {
        this.warmUpStartDate = warmUpStartDate;
        this.warmUpEndDate = warmUpEndDate;
        this.startDate = startDate;
        this.kLineFetcher = kLineFetcher;
        this.emaCalculator = emaCalculator;
    }

    public TradeSimulator execute(String interval) throws Exception {
        TradeSimulator simulator = new TradeSimulator(1000);
        // warm up.
        List<KLine> lines = kLineFetcher.query(warmUpStartDate, warmUpEndDate, interval);
        for (KLine line : lines) {
            emaCalculator.calculateEMA(line.getClosePrice());
        }
        System.out.println("Warp up done with price" + emaCalculator.getAvgPrice());
        lines = kLineFetcher.query(startDate, interval);
        try {
            for (KLine line : lines) {
                emaCalculator.calculateEMA(line.getClosePrice());
                if (line.getClosePrice() >= emaCalculator.getAvgPrice()) {
                    // buy.
                    if (executor.isEmpty()) {
                        executor.buy(line.getClosePrice(), line.getOpenTime());
                    }
                }
                if (line.getClosePrice() < emaCalculator.getAvgPrice()) {
                    if (!executor.isEmpty()) {
                        TradeResult tradeResult = executor.sell(Order.builder().price(line.getClosePrice()).time(line.getOpenTime()).build());
                        simulator.add(tradeResult);
                    }
                }
                if (debug) {
                     String ans = line.getClosePrice() >= emaCalculator.getAvgPrice() ? "LONG" : "SHORT";
                     System.out.println(TradeUtils.DATE_FORMAT.format(new Date(line.getOpenTime())) + " " + line.getClosePrice() + " " + emaCalculator.getAvgPrice() + " " + ans);
                }
            }
            if (!executor.isEmpty()) {
                KLine line = lines.get(lines.size() - 1);
                TradeResult tradeResult = executor.sell(Order.builder().price(line.getClosePrice()).time(line.getOpenTime()).build());
                simulator.add(tradeResult);
            }
        } catch (BanlanceException e) {
            System.err.println(e.getTradeResult());
        }
        if (debug) {
            System.out.println(emaCalculator);
            System.out.println(simulator);
        }

        return simulator;
    }

    public static void main(String[] args) throws Exception {
//        TradeSimulator top = null;
//        double threshold = 0.0D;
//        for (double i = 0.01; i < 1.0; i += 0.01) {
//            SimpleEMALongShort simpleEMALongShort = new SimpleEMALongShort(
//                    "2022-07-31",
//                    "2022-12-31",
//                    "2023-01-01",
//                    new TigerStockKlineFetcher(),
//                    new EMACalculator(i)
//            );
//            String[] timeList = {
//                    "min60",
//            };
//            for (String interval : timeList) {
//                System.out.println(interval + " \n");
//                TradeSimulator simulator = simpleEMALongShort.execute(interval);
//                if (top == null) {
//                    top = simulator;
//                    threshold = i;
//                } else {
//                    if (simulator.getWin() > top.getWin()) {
//                        top = simulator;
//                        threshold = i;
//                    }
//                }
//            }
//        }
//        System.out.println(threshold);
//        System.out.println(top);

//        TradeSimulator top = null;
//        double threshold = 0.0D;
//        for (double i = 0.01; i < 1.0; i += 0.01) {
//            SimpleEMALongShort simpleEMALongShort = new SimpleEMALongShort(
//                    "2018-01-01",
//                    "2018-06-31",
//                    "2018-07-01",
//                    new BTCKlineFetcher(),
//                    new EMACalculator(i)
//            );
//            String[] timeList = {
//                    "1w",
//            };
//            for (String interval : timeList) {
//                System.out.println(interval + " \n");
//                TradeSimulator simulator = simpleEMALongShort.execute(interval);
//                if (top == null) {
//                    top = simulator;
//                    threshold = i;
//                } else {
//                    if (simulator.getWin() > top.getWin()) {
//                        top = simulator;
//                        threshold = i;
//                    }
//                }
//            }
//        }
//        System.out.println(threshold);
//        System.out.println(top);


        SimpleEMALongShort simpleEMALongShort = new SimpleEMALongShort(
                "2018-01-01",
                "2018-06-30",
                "2018-07-01",
                new BTCKlineFetcher(),
                new EMACalculator(0.24)
        );
        String[] timeList = {
                "1w",
        };
        TradeSimulator simulator = simpleEMALongShort.execute("1w");
        System.out.println(simpleEMALongShort);
    }

}
