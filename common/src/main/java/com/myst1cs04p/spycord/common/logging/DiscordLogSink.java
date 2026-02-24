package com.myst1cs04p.spycord.common.logging;

import com.myst1cs04p.spycord.common.discord.IDiscordClient;

/**
 * Forwards log entries to Discord via the injected IDiscordClient.
 * The client handles async dispatch internally. This sink is non-blocking.
 */
public class DiscordLogSink implements ILogSink {

    private final IDiscordClient discordClient;

    public DiscordLogSink(IDiscordClient discordClient) {
        this.discordClient = discordClient;
    }

    @Override
    public void write(LogEntry entry) {
        discordClient.send(entry.getMessage());
    }
}

