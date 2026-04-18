package com.myst1cs04p.spycord.common;

import com.myst1cs04p.spycord.common.config.IPluginConfig;
import com.myst1cs04p.spycord.common.discord.IDiscordClient;
import com.myst1cs04p.spycord.common.discord.WebhookDiscordClient;
import com.myst1cs04p.spycord.common.events.EventPipeline;
import com.myst1cs04p.spycord.common.logging.CompositeLogSink;
import com.myst1cs04p.spycord.common.logging.DebugLogger;
import com.myst1cs04p.spycord.common.logging.DiscordLogSink;
import com.myst1cs04p.spycord.common.logging.FileLogSink;
import com.myst1cs04p.spycord.common.stats.ActivityTracker;

import java.util.logging.Logger;

/**
 * Wires all common services together and exposes them via getters.
 *
 * <p>Constructed once in the platform plugin's {@code onEnable()} and held for the
 * lifetime of the plugin. Owning construction of {@link WebhookDiscordClient} here
 * (rather than in the platform plugin) allows {@link ActivityTracker} and
 * {@link DebugLogger} to be injected without circular dependency gymnastics.
 */
public class ServiceRegistry {

    private final IPluginConfig config;
    private final IDiscordClient discordClient;
    private final CompositeLogSink logSink;
    private final EventPipeline eventPipeline;
    private final ActivityTracker activityTracker;

    /**
     * @param config A ready-to-read config (typically a {@code CachedPluginConfig}
     *               wrapping the platform's raw config).
     * @param logger The platform's logger -- used by all services that need console output.
     */
    public ServiceRegistry(IPluginConfig config, Logger logger) {
        this.config          = config;
        this.activityTracker = new ActivityTracker();

        DebugLogger debug    = new DebugLogger(logger, config);

        this.discordClient   = new WebhookDiscordClient(
                logger,
                config.getString("webhook-url", ""),
                activityTracker,
                debug
        );

        FileLogSink    fileSink    = new FileLogSink("command.log", logger);
        DiscordLogSink discordSink = new DiscordLogSink(discordClient);
        this.logSink               = new CompositeLogSink(fileSink, discordSink);

        this.eventPipeline = new EventPipeline(config, logSink, activityTracker, debug);
    }

    public IPluginConfig     getConfig()          { return config; }
    public IDiscordClient    getDiscordClient()    { return discordClient; }
    public CompositeLogSink  getLogSink()          { return logSink; }
    public EventPipeline     getEventPipeline()    { return eventPipeline; }
    public ActivityTracker   getActivityTracker()  { return activityTracker; }
}