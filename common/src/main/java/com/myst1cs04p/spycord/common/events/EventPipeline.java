package com.myst1cs04p.spycord.common.events;

import com.myst1cs04p.spycord.common.config.IPluginConfig;
import com.myst1cs04p.spycord.common.logging.CompositeLogSink;
import com.myst1cs04p.spycord.common.logging.LogEntry;

import java.util.List;

/**
 * The single place where domain events become log entries.
 *
 * Platform listeners translate raw Bukkit/Paper events into SpyEvents and
 * call pipeline.process(event). All business logic (which commands to track, which modules are enabled) lives here, not in the listeners.
 */
public class EventPipeline {

    private final IPluginConfig config;
    private final CompositeLogSink sink;

    public EventPipeline(IPluginConfig config, CompositeLogSink sink) {
        this.config = config;
        this.sink = sink;
    }

    public void process(SpyEvent event) {
        if (!config.getBoolean("enabled", true)) return;

        if (event instanceof CommandExecutedEvent e) {
            handleCommand(e);
        } else if (event instanceof GameModeChangedEvent e) {
            handleGameMode(e);
        } else if (event instanceof PlayerConnectionEvent e) {
            handleConnection(e);
        }
    }

    private void handleCommand(CommandExecutedEvent event) {
        if (!config.getBoolean("modules.command-logger", true)) return;

        List<String> sensitiveCommands = config.getStringList("command-logging.sensitive-commands")
                .stream().map(String::toLowerCase).toList();

        String root = event.getCommand().split(" ")[0].replace("/", "").toLowerCase();
        if (!sensitiveCommands.contains(root)) return;

        String sender = event.isConsole() ? "CONSOLE" : event.getSenderName();
        sink.write(new LogEntry(String.format("[%s] %s", sender, event.getCommand())));
    }

    private void handleGameMode(GameModeChangedEvent event) {
        if (!config.getBoolean("modules.gamemode-logger", true)) return;

        sink.write(new LogEntry(String.format("[%s] [OP] switched gamemode from %s to %s",
                event.getPlayerName(), event.getOldMode(), event.getNewMode())));
    }

    private void handleConnection(PlayerConnectionEvent event) {
        if (!config.getBoolean("modules.join-logger", true)) return;

        String action = event.getType() == PlayerConnectionEvent.Type.JOIN ? "joined" : "left";
        sink.write(new LogEntry(String.format("[OP] %s %s the game.",
                event.getPlayerName(), action)));
    }
}

