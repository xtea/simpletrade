package io.xtea.strategy.ema;

import com.tigerbrokers.stock.openapi.client.util.DateUtils;
import io.xtea.KLineFetcher;
import io.xtea.core.KLine;
import io.xtea.core.Order;
import io.xtea.core.TradeResult;
import io.xtea.core.TradeSimulator;
import io.xtea.core.TradeUtils;
import io.xtea.strategy.BanlanceException;
import io.xtea.strategy.test.MockTradeExecutor;
import io.xtea.usstock.TigerStockKlineFetcher;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 21:56
 */
public class SimpleEMALongShortQQQ {

    MockTradeExecutor executor = new MockTradeExecutor();

    KLineFetcher kLineFetcher;

    EMACalculator emaCalculator = new EMACalculator(2 / 13.0);

    String warmUpStartDate;
    String warmUpEndDate;
    String startDate;
    Optional<String> endDate = Optional.empty();

    boolean debug = true;

    public SimpleEMALongShortQQQ(String warmUpStartDate, String warmUpEndDate, String startDate,
                                 KLineFetcher kLineFetcher, EMACalculator emaCalculator) {
        this.warmUpStartDate = warmUpStartDate;
        this.warmUpEndDate = warmUpEndDate;
        this.startDate = startDate;
        this.kLineFetcher = kLineFetcher;
        this.emaCalculator = emaCalculator;
    }

    public SimpleEMALongShortQQQ(String warmUpStartDate, String warmUpEndDate, String startDate,
                                 Optional<String> endDate,
                                 KLineFetcher kLineFetcher, EMACalculator emaCalculator) {
        this.warmUpStartDate = warmUpStartDate;
        this.warmUpEndDate = warmUpEndDate;
        this.startDate = startDate;
        this.kLineFetcher = kLineFetcher;
        this.emaCalculator = emaCalculator;
        this.endDate = endDate;
    }

    public TradeSimulator execute(String interval) throws Exception {
        TradeSimulator simulator = new TradeSimulator(1000);
        // warm up.
        List<KLine> lines = kLineFetcher.query(warmUpStartDate, warmUpEndDate, interval);
        for (KLine line : lines) {
            emaCalculator.calculateEMA(line.getClosePrice());
        }
        System.out.println("Warp up done with price" + emaCalculator.getAvgPrice());
        lines = kLineFetcher.query(startDate, endDate.orElse(DateUtils.printSystemDate()), interval);
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
//        double base = 0;
//        for (double i = 0.01; i < 1; i += 0.01) {
//            SimpleEMALongShortQQQ simpleEMALongShort = new SimpleEMALongShortQQQ(
//                    "2018-01-01",
//                    "2020-12-30",
//                    "2021-01-01",
//                    new TigerStockKlineFetcher(),
//                    new EMACalculator(i)
//            );
//            TradeSimulator simulator = simpleEMALongShort.execute("day");
//            if (simulator.getWin() > base) {
//                System.out.println(String.format("Win %f by thread hold %f", simulator.getWin(), i));
//                base = simulator.getWin();
//            }
//        }


        SimpleEMALongShortQQQ simpleEMALongShort = new SimpleEMALongShortQQQ(
                "2022-01-01",
                "2023-12-31",
                "2024-01-01",
                //Optional.of("2021-12-31"),
                new TigerStockKlineFetcher(),
                new EMACalculator(0.1)
        );
        TradeSimulator simulator = simpleEMALongShort.execute("day");
        System.out.println(simulator.printCsv());
    }

}
