package com.myst1cs04p.spycord.common.logging;

/**
 * A single destination for plugin log entries.
 * Multiple sinks are composed together via CompositeLogSink.
 */
public interface ILogSink {

    /**
     * Write a log entry to this sink.
     *
     * @param entry Pre-formatted log entry including timestamp.
     */
    void write(LogEntry entry);
}

