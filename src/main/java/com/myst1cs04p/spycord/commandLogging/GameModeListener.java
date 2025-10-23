package com.myst1cs04p.spycord.commandLogging;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.server.ServerCommandEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.myst1cs04p.spycord.SpyCord;


public class GameModeListener implements Listener {
    private final Spycord plugin;

    public GameModeListener(Spycord plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if(!plugin.getIsEnabled()) {
            return;
        }

        String playerName = event.getPlayer().getName();
        GameMode newMode = event.getNewGameMode();
        GameMode oldMode = event.getPlayer().getGameMode();

        boolean isOp = event.getPlayer().isOp();
        if (!isOp) {
            return; // Skip non-OPs
        }

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = String.format("[%s] [OP] %s: switched gamemode from %s to %s\n",
                time,
                playerName,
                oldMode.name(),
                newMode.name());

        SpyCord.getDiscord().sendToDiscord(logEntry);
        
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void OnConsoleCommand(ServerCommandEvent event) {
        if (!plugin.getIsEnabled()) {
            return;
        }
        String senderName = event.getSender().getName();
        String command = event.getCommand(); // No slash prefix

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String logEntry = String.format("[%s] [CONSOLE] %s: /%s\n", time, senderName, command);
        SpyCord.getDiscord().sendToDiscord(logEntry);

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.getIsEnabled()) {
            return;
        }
        String playerName = event.getPlayer().getName();
        String command = event.getMessage(); // Full command with slash
        boolean isOp = event.getPlayer().isOp();

        if (command.toLowerCase().startsWith("/login") || command.toLowerCase().startsWith("/register")) {
            return;
        }

        if (!event.getPlayer().isOp()) {
            return; // Skip non-OPs
        }

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String logEntry = String.format("[%s] %s%s: %s\n",
                time,
                isOp ? "[OP] " : "",
                playerName,
                command);

        SpyCord.getDiscord().sendToDiscord(logEntry);

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        // This method is intentionally left empty to avoid duplicate logging
    }
}
