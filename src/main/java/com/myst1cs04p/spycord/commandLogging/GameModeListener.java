package com.myst1cs04p.spycord.commandLogging;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.myst1cs04p.spycord.SpyCord;


public class GameModeListener implements Listener {
    private final SpyCord plugin;

    public GameModeListener(SpyCord plugin) {
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
    }
}
