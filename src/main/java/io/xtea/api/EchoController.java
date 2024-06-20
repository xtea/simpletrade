package io.xtea.api;

import io.xtea.WeeklyBTCJob;
import io.xtea.core.Order;
import io.xtea.core.TradeExecutor;
import io.xtea.core.TradeResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-31 23:17
 */
@RestController
public class EchoController {

    @Autowired
    WeeklyBTCJob weeklyBTCJob;


    @Autowired
    @Qualifier("btcTradeExecutor")
    TradeExecutor executor;


    @GetMapping(value = "/")
    public String echo() throws IOException {
        return "ok";
    }

    @GetMapping(value = "/run")
    public String job() throws IOException {
        weeklyBTCJob.run();
        return "ok";
    }

    @GetMapping(value = "/sell")
    public String sell() throws Exception {
        TradeResult sell = executor.sell(Order.builder()
                .time(System.currentTimeMillis())
                .price(Double.MAX_VALUE)
                .build());
        return sell.toString();
    }


    @GetMapping(value = "/buy")
    public String buy() throws Exception {
        return executor.buy(42800d, System.currentTimeMillis()).toString();
    }

}
