package massage.cs4224c.util;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.TimeZone;

public class TimeUtility {

    private static final FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS", TimeZone.getTimeZone("UTC"));

    public static Long parse(String datetime) {
        try {
            return fastDateFormat.parse(datetime).getTime();
        } catch (ParseException e) {
            return 0L;
        }
    }
}