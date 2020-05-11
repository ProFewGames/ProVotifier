package com.profewgames.provotifier;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

class LogFilter implements Filter {
	private String prefix;

	public LogFilter(final String prefix) {
		this.prefix = prefix;
	}

	@Override
	public boolean isLoggable(final LogRecord record) {
		record.setMessage(this.prefix + record.getMessage());
		return true;
	}
}
