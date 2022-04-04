package cz.acrobits.csp.app_users_management.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;

public class DateTimeTestHelper {
    public static boolean assertIsLocalDateTime(String jsonDateTime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(jsonDateTime, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception ex) {
            System.out.println(format("Can't convert following string to LocalDateTime: %s", jsonDateTime));
            return false;
        }
        return true;
    }

    public static LocalDateTime toLocalDateTime(String jsonDateTime) {
        return LocalDateTime.parse(jsonDateTime, DateTimeFormatter.ISO_DATE_TIME);
    }
}
