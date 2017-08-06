package chat.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
	//2017-08-06 00:21:00.0
	static Pattern hhmmssPattern = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}");
	//static Pattern hhmmssPattern = Pattern.compile("[\\d]");

	public static String getNow4db() {
		ZonedDateTime now = ZonedDateTime.now();
		String nowString = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss"));
		return nowString;
	}

	public static String conv2hhmmss(String nowString) {
		if( null==nowString ) {
			return null;
		}
		System.out.println("nowString="+nowString);
		Matcher m1 = hhmmssPattern.matcher(nowString);
		m1.find();
		return m1.group();
	}
}
