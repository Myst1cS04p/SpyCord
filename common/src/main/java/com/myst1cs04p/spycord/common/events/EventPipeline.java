package com.myst1cs04p.spycord.common.events;

import com.myst1cs04p.spycord.common.config.IPluginConfig;
import com.myst1cs04p.spycord.common.logging.CompositeLogSink;
import com.myst1cs04p.spycord.common.logging.LogEntry;
import com.myst1cs04p.spycord.common.stats.ActivityTracker;

import java.util.List;

/**
 * The single place where domain events are validated, formatted, and routed to sinks.
 *
 * <p>Platform listeners translate raw Bukkit/Paper events into {@link SpyEvent}s and
 * call {@link #process(SpyEvent)}. All business logic - filtering, template
 * substitution, module checks - lives here so listeners stay thin.
 *
 * <p>Debug output is handled automatically: the {@link CompositeLogSink} includes a
 * {@code DebugLogSink} that echoes every written entry to console when
 * {@code debug: true}. No separate debug injection is needed here.
 *
 * <h3>Message templates (config {@code messages.*})</h3>
 * Each event type has a corresponding config key under {@code messages}. Templates
 * support the following placeholders:
 * <ul>
 *   <li>{@code {sender}}   - Command sender name (or {@code CONSOLE})</li>
 *   <li>{@code {command}}  - Full command string including arguments</li>
 *   <li>{@code {player}}   - Player name (gamemode / connection events)</li>
 *   <li>{@code {old_mode}} - Previous gamemode</li>
 *   <li>{@code {new_mode}} - New gamemode</li>
 *   <li>{@code {action}}   - {@code joined} or {@code left} (connection events)</li>
 * </ul>
 */
public class EventPipeline {

    private static final String DEFAULT_COMMAND    = "`{sender}` ran: `{command}`";
    private static final String DEFAULT_GAMEMODE   = "`{player}` switched gamemode from `{old_mode}` to `{new_mode}`";
    private static final String DEFAULT_CONNECTION = "`{player}` {action} the server.";

    private final IPluginConfig    config;
    private final CompositeLogSink sink;
    private final ActivityTracker  tracker;

    public EventPipeline(IPluginConfig config, CompositeLogSink sink, ActivityTracker tracker) {
        this.config  = config;
        this.sink    = sink;
        this.tracker = tracker;
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

        String sender  = event.isConsole() ? "CONSOLE" : event.getSenderName();
        String message = config.getString("messages.command", DEFAULT_COMMAND)
                .replace("{sender}",  sender)
                .replace("{command}", event.getCommand());

        tracker.recordCommand();
        sink.write(new LogEntry(message));
    }

    private void handleGameMode(GameModeChangedEvent event) {
        if (!config.getBoolean("modules.gamemode-logger", true)) return;

        String message = config.getString("messages.gamemode-change", DEFAULT_GAMEMODE)
                .replace("{player}",   event.getPlayerName())
                .replace("{old_mode}", event.getOldMode())
                .replace("{new_mode}", event.getNewMode());

        tracker.recordGamemodeChange();
        sink.write(new LogEntry(message));
    }

    private void handleConnection(PlayerConnectionEvent event) {
        if (!config.getBoolean("modules.join-logger", true)) return;

        String action  = event.getType() == PlayerConnectionEvent.Type.JOIN ? "joined" : "left";
        String message = config.getString("messages.connection", DEFAULT_CONNECTION)
                .replace("{player}", event.getPlayerName())
                .replace("{action}", action);

        tracker.recordJoinQuit();
        sink.write(new LogEntry(message));
    }
}