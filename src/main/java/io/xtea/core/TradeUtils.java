package io.xtea.core;

import lombok.experimental.UtilityClass;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 20:40
 */
@UtilityClass
public class TradeUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    public long getBeginTime(String sDate) throws ParseException {
        Date date = DATE_FORMAT.parse(sDate);
        return date.getTime();
    }

    public String toMonth(long time) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM");
        return DATE_FORMAT.format(new Date(time));
    }

    public long getCloseTime(String sDate) throws ParseException {
        Date date = DATE_FORMAT.parse(sDate);
        date = DateUtils.setHours(date, 23);
        date = DateUtils.setMinutes(date, 59);
        date = DateUtils.setSeconds(date, 59);
        return date.getTime();
    }

    public String getBeforeDate(int day) {
        Date date = DateUtils.addDays(new Date(), -7);
        return DATE_FORMAT.format(date);
    }
}
