package com.myst1cs04p.spycord.common.logging;

import java.util.Arrays;
import java.util.List;

/**
 * Fans a single log entry out to multiple ILogSink implementations.
 * This is the object you inject wherever logging is needed.
 *
 * Example:
 *   new CompositeLogSink(fileLogSink, discordLogSink)
 */
public class CompositeLogSink implements ILogSink {

    private final List<ILogSink> sinks;

    public CompositeLogSink(ILogSink... sinks) {
        this.sinks = Arrays.asList(sinks);
    }

    @Override
    public void write(LogEntry entry) {
        for (ILogSink sink : sinks) {
            sink.write(entry);
        }
    }
}

