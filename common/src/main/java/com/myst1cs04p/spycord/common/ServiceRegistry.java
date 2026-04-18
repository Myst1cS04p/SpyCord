package com.myst1cs04p.spycord.common;

import com.myst1cs04p.spycord.common.config.IPluginConfig;
import com.myst1cs04p.spycord.common.discord.IDiscordClient;
import com.myst1cs04p.spycord.common.discord.WebhookDiscordClient;
import com.myst1cs04p.spycord.common.events.EventPipeline;
import com.myst1cs04p.spycord.common.logging.CompositeLogSink;
import com.myst1cs04p.spycord.common.logging.DebugLogSink;
import com.myst1cs04p.spycord.common.logging.DiscordLogSink;
import com.myst1cs04p.spycord.common.logging.FileLogSink;
import com.myst1cs04p.spycord.common.stats.ActivityTracker;

import java.util.logging.Logger;

/**
 * Wires all common services together and exposes them via getters.
 *
 * <p>The {@link DebugLogSink} is the third member of the {@link CompositeLogSink},
 * sitting after the file and Discord sinks. It only emits when {@code debug: true},
 * so there is no production overhead. Every event that clears the pipeline filters
 * and reaches {@code sink.write()} is automatically covered — no separate injection
 * into {@link EventPipeline} or {@link WebhookDiscordClient} required.
 */
public class ServiceRegistry {

    private final IPluginConfig    config;
    private final IDiscordClient   discordClient;
    private final CompositeLogSink logSink;
    private final EventPipeline    eventPipeline;
    private final ActivityTracker  activityTracker;

    public ServiceRegistry(IPluginConfig config, Logger logger) {
        this.config          = config;
        this.activityTracker = new ActivityTracker();

        this.discordClient = new WebhookDiscordClient(
                logger,
                config.getString("webhook-url", ""),
                activityTracker
        );

        FileLogSink   fileSink    = new FileLogSink("command.log", logger);
        DiscordLogSink discordSink = new DiscordLogSink(discordClient);
        DebugLogSink  debugSink   = new DebugLogSink(logger, config);

        // that passes the pipeline; DebugLogSink self-gates on the config flag.
        this.logSink = new CompositeLogSink(fileSink, discordSink, debugSink);

        this.eventPipeline = new EventPipeline(config, logSink, activityTracker);
    }

    public IPluginConfig    getConfig()          { return config; }
    public IDiscordClient   getDiscordClient()    { return discordClient; }
    public CompositeLogSink getLogSink()          { return logSink; }
    public EventPipeline    getEventPipeline()    { return eventPipeline; }
    public ActivityTracker  getActivityTracker()  { return activityTracker; }
}