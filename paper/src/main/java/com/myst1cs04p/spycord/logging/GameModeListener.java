package com.myst1cs04p.spycord.logging;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import com.myst1cs04p.spycord.SpyCord;


public class GameModeListener implements Listener {
    private final SpyCord plugin;

    public GameModeListener(SpyCord plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if(!plugin.getIsEnabled() && !plugin.getIsEnabled("gamemode-logger")) {
            return;
        }

        String playerName = event.getPlayer().getName();
        GameMode newMode = event.getNewGameMode();
        GameMode oldMode = event.getPlayer().getGameMode();

        String logEntry = String.format("[%s] [OP] %s: switched gamemode from %s to %s\n",
                playerName,
                oldMode.name(),
                newMode.name());

        this.plugin.getCommandLogger().log(logEntry);;
    }
}
