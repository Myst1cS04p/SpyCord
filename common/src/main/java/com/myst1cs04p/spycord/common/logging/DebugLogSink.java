package com.myst1cs04p.spycord.common.logging;

import com.myst1cs04p.spycord.common.config.IPluginConfig;

import java.util.logging.Logger;

/**
 * A log sink that echoes every entry to the server console, gated behind the
 * {@code debug} config flag.
 *
 * <p>Add this to a {@link CompositeLogSink} alongside the file and Discord sinks.
 * When {@code debug: false} (the default) the check short-circuits and the method
 * returns immediately with no allocation. When {@code debug: true}, each log entry
 * that makes it past the {@link com.myst1cs04p.spycord.common.events.EventPipeline}
 * filters is printed to console in its full formatted form (timestamp included).
 *
 * <p>Reads the config flag on every call, so toggling debug mode via
 * {@code /spycord reload} takes effect immediately without a server restart.
 */
public class DebugLogSink implements ILogSink {

    private final Logger logger;
    private final IPluginConfig config;

    public DebugLogSink(Logger logger, IPluginConfig config) {
        this.logger = logger;
        this.config = config;
    }

    @Override
    public void write(LogEntry entry) {
        if (!config.getBoolean("debug", false)) return;
        logger.info("[SpyCord DEBUG] " + entry.formatted());
    }
}