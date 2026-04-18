package com.myst1cs04p.spycord.common.events;

import com.myst1cs04p.spycord.common.config.IPluginConfig;
import com.myst1cs04p.spycord.common.logging.CompositeLogSink;
import com.myst1cs04p.spycord.common.logging.DebugLogger;
import com.myst1cs04p.spycord.common.logging.LogEntry;
import com.myst1cs04p.spycord.common.stats.ActivityTracker;

import java.util.List;

/**
 * The single place where domain events are validated, formatted, and routed to sinks.
 *
 * <p>Platform listeners translate raw Bukkit/Paper events into {@link SpyEvent}s and
 * call {@link #process(SpyEvent)}. All business logic lives here - filtering, template
 * substitution, module checks - so listeners stay thin.
 *
 * <h3>Message templates (config {@code messages.*})</h3>
 * Each event type has a corresponding config key under {@code messages}. Templates
 * support the following placeholders:
 * <ul>
 *   <li>{@code {sender}} - The name of the command sender (or {@code CONSOLE})</li>
 *   <li>{@code {command}} - The full command string including arguments</li>
 *   <li>{@code {player}} - The player's name (gamemode / connection events)</li>
 *   <li>{@code {old_mode}} - Previous gamemode</li>
 *   <li>{@code {new_mode}} - New gamemode</li>
 *   <li>{@code {action}} - {@code joined} or {@code left} (connection events)</li>
 * </ul>
 */
public class EventPipeline {

    // Default templates
    // servers see no change in output unless they customise config.yml.
    private static final String DEFAULT_COMMAND    = "`{sender}` ran: `{command}`";
    private static final String DEFAULT_GAMEMODE   = "`{player}` switched gamemode from `{old_mode}` to `{new_mode}`";
    private static final String DEFAULT_CONNECTION = "`{player}` {action} the server.";

    private final IPluginConfig config;
    private final CompositeLogSink sink;
    private final ActivityTracker tracker;
    private final DebugLogger debug;

    public EventPipeline(IPluginConfig config, CompositeLogSink sink,
                         ActivityTracker tracker, DebugLogger debug) {
        this.config  = config;
        this.sink    = sink;
        this.tracker = tracker;
        this.debug   = debug;
    }

    public void process(SpyEvent event) {
        if (!config.getBoolean("enabled", true)) {
            debug.log("Plugin is disabled globally, event dropped: " + event.getClass().getSimpleName());
            return;
        }

        if (event instanceof CommandExecutedEvent e) {
            handleCommand(e);
        } else if (event instanceof GameModeChangedEvent e) {
            handleGameMode(e);
        } else if (event instanceof PlayerConnectionEvent e) {
            handleConnection(e);
        }
    }

    // Handlers

    private void handleCommand(CommandExecutedEvent event) {
        if (!config.getBoolean("modules.command-logger", true)) {
            debug.log("Command-logger module is disabled, skipping.");
            return;
        }

        List<String> sensitiveCommands = config.getStringList("command-logging.sensitive-commands")
                .stream().map(String::toLowerCase).toList();

        String root = event.getCommand().split(" ")[0].replace("/", "").toLowerCase();

        if (!sensitiveCommands.contains(root)) {
            debug.log("Command '" + root + "' is not in the sensitive-commands list, skipping.");
            return;
        }

        String sender  = event.isConsole() ? "CONSOLE" : event.getSenderName();
        String template = config.getString("messages.command", DEFAULT_COMMAND);
        String message  = template
                .replace("{sender}",  sender)
                .replace("{command}", event.getCommand());

        debug.log("Logging command event: " + message);
        tracker.recordCommand();
        sink.write(new LogEntry(message));
    }

    private void handleGameMode(GameModeChangedEvent event) {
        if (!config.getBoolean("modules.gamemode-logger", true)) {
            debug.log("Gamemode-logger module is disabled, skipping.");
            return;
        }

        String template = config.getString("messages.gamemode-change", DEFAULT_GAMEMODE);
        String message  = template
                .replace("{player}",   event.getPlayerName())
                .replace("{old_mode}", event.getOldMode())
                .replace("{new_mode}", event.getNewMode());

        debug.log("Logging gamemode event: " + message);
        tracker.recordGamemodeChange();
        sink.write(new LogEntry(message));
    }

    private void handleConnection(PlayerConnectionEvent event) {
        if (!config.getBoolean("modules.join-logger", true)) {
            debug.log("Join-logger module is disabled, skipping.");
            return;
        }

        String action   = event.getType() == PlayerConnectionEvent.Type.JOIN ? "joined" : "left";
        String template = config.getString("messages.connection", DEFAULT_CONNECTION);
        String message  = template
                .replace("{player}", event.getPlayerName())
                .replace("{action}", action);

        debug.log("Logging connection event: " + message);
        tracker.recordJoinQuit();
        sink.write(new LogEntry(message));
    }
}