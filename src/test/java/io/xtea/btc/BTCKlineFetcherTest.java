package io.xtea.btc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.xtea.core.KLine;

import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 20:51
 */
public class BTCKlineFetcherTest {

    BTCKlineFetcher fetcher = new BTCKlineFetcher();

    @Test
    public void happyCase() throws Exception {
        List<KLine> result = fetcher.query("2023-01-01", "2023-01-10", "1d");
        assertEquals(10, result.size());
        for (KLine kLine : result) {
            assertNotNull(kLine.getClosePrice());
            assertNotNull(kLine.getOpenPrice());
            assertNotNull(kLine.getHighPrice());
            assertNotNull(kLine.getLowPrice());
        }
    }

    @Test
    public void testQueryWithoutEnd() throws Exception {
        List<KLine> result = fetcher.query("2023-03-01", "1d");
        assertTrue(result.size() >= 30);
        for (KLine kLine : result) {
            assertNotNull(kLine.getClosePrice());
            assertNotNull(kLine.getOpenPrice());
            assertNotNull(kLine.getHighPrice());
            assertNotNull(kLine.getLowPrice());
        }
    }

    @Test
    public void testServerTime() {
        long serverTime = fetcher.getServerTime();
        System.out.println(new Date(serverTime));
        assertTrue(serverTime > 0);
    }
}