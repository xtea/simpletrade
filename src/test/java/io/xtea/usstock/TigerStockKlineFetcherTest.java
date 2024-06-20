package io.xtea.usstock;

import io.xtea.core.KLine;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-04-23 22:07
 */
public class TigerStockKlineFetcherTest {

    TigerStockKlineFetcher tigerStockKlineFetcher = new TigerStockKlineFetcher();


    @Test
    void test() throws Exception {
        List<KLine> result = tigerStockKlineFetcher.query("2023-01-01", "day");
        System.out.println(result);
    }
}