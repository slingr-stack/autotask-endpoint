package io.slingr.endpoints.autotask.ws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;

public class DateHelper {
    private static SimpleDateFormat utcFormatter1 = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSz" );
    private static SimpleDateFormat utcFormatter2 = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
    private static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
            .toFormatter();

    static public Date convertFromDateTime(String str) {
        try {
            LocalDateTime date = LocalDateTime.parse(str);
            return Date.from(date.atZone(ZoneId.of("EST5EDT")).toInstant());
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Date time [%s] cannot be parsed", str), e);
        }
    }

    static public String convertToDateTime(Date date) {
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("EST5EDT"));
        return formatter.format(ldt);
    }

    public static Date convertFromUtcDateTime(String str) {
        str = str.substring( 0, str.length() - 1) + "GMT-00:00";
        try {
            return utcFormatter1.parse( str );
        } catch (ParseException e) {
            try {
                return utcFormatter2.parse(str);
            } catch (ParseException e2) {
                throw new IllegalArgumentException(String.format("Date time [%s] cannot be parsed", str), e2);
            }
        }
    }
}
