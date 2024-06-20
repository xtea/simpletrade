package io.xtea;

import io.xtea.core.KLine;

import java.util.List;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 21:57
 */
public interface KLineFetcher {

    public List<KLine> query(String startDate, String endDate, String interval) throws Exception;

    public List<KLine> query(String startDate, String interval) throws Exception;

}
