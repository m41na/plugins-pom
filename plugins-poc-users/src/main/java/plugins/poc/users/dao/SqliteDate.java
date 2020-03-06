package plugins.poc.users.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SqliteDate {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static Date fromString(String source) {
        try {
            if (source != null) {
                return DATE_FORMAT.parse(source);
            } else {
                return null;
            }
        } catch (ParseException e) {
            throw new RuntimeException("Could not correctly parse date string", e);
        }
    }

    public static String toString(Date source) {
        return source != null ? DATE_FORMAT.format(source) : null;
    }
}
