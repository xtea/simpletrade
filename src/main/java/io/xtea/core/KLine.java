package io.xtea.core;

import lombok.Builder;
import lombok.Data;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-29 22:45
 */
@Data
@Builder
public class KLine {

    long openTime;
    double openPrice;
    double highPrice;
    double lowPrice;
    double closePrice;
}
