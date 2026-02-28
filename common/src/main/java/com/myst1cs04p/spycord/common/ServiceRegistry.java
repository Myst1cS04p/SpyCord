package com.myst1cs04p.spycord.common;

import com.myst1cs04p.spycord.common.config.IPluginConfig;
import com.myst1cs04p.spycord.common.discord.IDiscordClient;
import com.myst1cs04p.spycord.common.events.EventPipeline;
import com.myst1cs04p.spycord.common.logging.CompositeLogSink;
import com.myst1cs04p.spycord.common.logging.DiscordLogSink;
import com.myst1cs04p.spycord.common.logging.FileLogSink;

import java.util.logging.Logger;

/**
 * Wires all common services together.
 * Constructed once in the platform plugin's onEnable() and passed down by reference.
 */
public class ServiceRegistry {

    private final IPluginConfig config;
    private final IDiscordClient discordClient;
    private final CompositeLogSink logSink;
    private final EventPipeline eventPipeline;

    public ServiceRegistry(IPluginConfig config, IDiscordClient discordClient, Logger logger) {
        this.config = config;
        this.discordClient = discordClient;

        FileLogSink fileLogSink = new FileLogSink("command.log", logger);
        DiscordLogSink discordLogSink = new DiscordLogSink(discordClient);
        this.logSink = new CompositeLogSink(fileLogSink, discordLogSink);

        this.eventPipeline = new EventPipeline(config, logSink);
    }

    public IPluginConfig getConfig() {
        return config;
    }

    public IDiscordClient getDiscordClient() {
        return discordClient;
    }

    public CompositeLogSink getLogSink() {
        return logSink;
    }

    public EventPipeline getEventPipeline() {
        return eventPipeline;
    }
}

