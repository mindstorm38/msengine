package io.msengine.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class GameLoggerFormatter extends Formatter {
	
	private final Date date = new Date();
	
	@Override
	public synchronized String format(LogRecord record) {
		this.date.setTime(record.getMillis());
		String throwable = "";
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println();
			record.getThrown().printStackTrace(pw);
			pw.close();
			throwable = sw.toString();
		}
		return String.format("%1$tD %1$tT [%3$s] <%2$s> %4$s %5$s%n", this.date, record.getLoggerName(), record.getLevel().getLocalizedName(), this.formatMessage(record), throwable);
	}
	
	public static void setupRootLogger() {
		for (Handler handler : Logger.getLogger("").getHandlers()) {
			if (handler.getClass() == ConsoleHandler.class) {
				handler.setFormatter(new GameLoggerFormatter());
			}
		}
	}
	
}
