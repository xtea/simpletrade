package io.xtea;

import io.xtea.core.KLine;
import io.xtea.core.Order;
import io.xtea.core.TradeExecutor;
import io.xtea.core.TradeResult;
import io.xtea.core.TradeUtils;
import io.xtea.notification.MessageSender;
import io.xtea.strategy.ema.EMACalculator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class WeeklyBTCJob {

    public static final String INTERVAL = "1w";
    public static final String BEGIN_DATE = "2017-07-31";
    @Autowired
    @Qualifier("btcKlineFetcher")
    KLineFetcher kLineFetcher;

    @Autowired
    @Qualifier("btcTradeExecutor")
    TradeExecutor executor;


    @Autowired
    MessageSender messageSender;

    /**
     * seconds minutes hours day-of-month month day-of-week
     * 0       0      8        *         *        ?
     */
    @Scheduled(cron = "0 0 2 * * MON")
//    @Scheduled(cron = "0 */1 * ? * *")
    public void run() {
        try {
            log.info("Run at {}", new Date());
            messageSender.send("Start run at " + new Date());
            EMACalculator emaCalculator = trainEMA();
            List<KLine> lines = kLineFetcher.query(TradeUtils.getBeforeDate(7), INTERVAL);
            log.info("get recent kline {}", lines);
            if (lines.isEmpty()) {
                log.warn("Unable fetch recent kline by {}", TradeUtils.getBeforeDate(7));
                messageSender.send("ERROR! Unable fetch recent kline.");
                return;
            }
            KLine kLine = lines.get(lines.size() - 1);
            log.info("current price {}, avg price {}", kLine.getClosePrice(), emaCalculator.getAvgPrice());
            messageSender.send(String.format("current price is %,.2f, avg price is  %,.2f", kLine.getClosePrice(), emaCalculator.getAvgPrice()));
            if (kLine.getClosePrice() >= emaCalculator.getAvgPrice()) {
                // buy.
                if (executor.isEmpty()) {
                    executor.buy(kLine.getClosePrice(), kLine.getOpenTime());
                    messageSender.send(String.format("Buy with %,.2f", kLine.getClosePrice()));
                } else {
                    log.info("Current holding, skip CALL.");
                    messageSender.send("Current holding, skip CALL.");
                }
            }
            if (kLine.getClosePrice() < emaCalculator.getAvgPrice()) {
                // sell.
                if (!executor.isEmpty()) {
                    TradeResult tradeResult = executor.sell(Order.builder().price(kLine.getClosePrice()).time(kLine.getOpenTime()).build());
                    messageSender.send(String.format("Sell with %,.2f", kLine.getClosePrice()));
                    log.info("trade result is {}", tradeResult);
                } else {
                    log.info("Current empty, skip PUT.");
                    messageSender.send("Current empty, skip PUT.");
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            messageSender.send("ERROR!" + e.getMessage());
        }
    }


    public EMACalculator trainEMA() throws Exception {
        EMACalculator emaCalculator = EMACalculator.getInstance();
        List<KLine> lines = kLineFetcher.query(BEGIN_DATE, INTERVAL);
        log.info("query kline from {} to {}, result size is {}", BEGIN_DATE, TradeUtils.DATE_FORMAT.format(new Date()), lines.size());
        for (KLine line : lines) {
            emaCalculator.calculateEMA(line.getClosePrice());
        }
        log.info("Warp up done with price = {}", emaCalculator.getAvgPrice());
        messageSender.send("Finish calculating, BTC avg price = " + emaCalculator.getAvgPrice());
        return emaCalculator;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startUp() {
        messageSender.send("Server startup at " + new Date().toLocaleString());
    }
}
