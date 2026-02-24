package com.myst1cs04p.spycord.common.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable value object representing a single log event.
 */
public final class LogEntry {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String timestamp;
    private final String message;

    public LogEntry(String message) {
        this.timestamp = LocalDateTime.now().format(FORMATTER);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the formatted string as it would appear in a log file.
     */
    public String formatted() {
        return "[" + timestamp + "] " + message;
    }
}

