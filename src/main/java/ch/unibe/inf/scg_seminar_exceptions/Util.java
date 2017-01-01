package ch.unibe.inf.scg_seminar_exceptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Helper class for things like logging.
 */
public class Util {
	static Logger log;

	static String timestampAsString() {
		return new SimpleDateFormat("yyyy-MM-dd-HHmm").format(new Date());
	}

	static void initialize(String foldername) {
		try {
			log = Logger.getLogger("ch.unibe.inf.scg_seminar_exceptions");
			FileHandler fh = new FileHandler("logs/" + foldername + ".log", true);
			log.addHandler(fh);
			LogFormatter formatter = new LogFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			// of course we ignore this :)
		}
	}

	public static void logException(Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.getClass().getName() + ": ");
		sb.append(e.getMessage());
		sb.append("\n\n");
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		Util.log.severe(sb.toString());
	}
}
