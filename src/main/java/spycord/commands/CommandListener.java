package spycord.commands;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.server.ServerCommandEvent;

import spycord.SpyCord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommandListener implements Listener {

    private final File logFile;
    private final SpyCord plugin;

    public CommandListener(SpyCord plugin) {
        this.plugin = plugin;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        logFile = new File(dataFolder, "commands.log");
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> GetSensitiveCommands() {
        return plugin.getConfig().getStringList("sensitive-commands");
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if(!plugin.isEnabled) {
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
