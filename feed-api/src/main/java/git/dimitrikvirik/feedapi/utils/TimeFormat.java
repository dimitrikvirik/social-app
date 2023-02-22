package git.dimitrikvirik.feedapi.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormat {

	public static DateTimeFormatter zoneDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
}
