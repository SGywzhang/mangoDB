package client.cs4224c.util;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;
import java.util.TimeZone;

public class TimeUtility {

    private static final FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS z", TimeZone.getTimeZone("UTC"));

    public static String format(Date date) {
        return fastDateFormat.format(date);
    }
}
