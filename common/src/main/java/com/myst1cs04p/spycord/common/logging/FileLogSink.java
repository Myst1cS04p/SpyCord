package com.myst1cs04p.spycord.common.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Appends log entries to a flat file (command.log by default).
 */
public class FileLogSink implements ILogSink {

    private final String filePath;
    private final Logger logger;

    public FileLogSink(String filePath, Logger logger) {
        this.filePath = filePath;
        this.logger = logger;
    }

    @Override
    public void write(LogEntry entry) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(entry.formatted() + System.lineSeparator());
        } catch (IOException e) {
            logger.severe("[SpyCord] Failed to write to log file: " + e.getMessage());
        }
    }
}

