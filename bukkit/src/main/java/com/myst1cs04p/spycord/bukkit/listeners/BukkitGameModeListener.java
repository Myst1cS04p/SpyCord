package com.myst1cs04p.spycord.bukkit.listeners;

import com.myst1cs04p.spycord.common.events.GameModeChangedEvent;
import com.myst1cs04p.spycord.common.events.EventPipeline;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * Translates Bukkit gamemode events into domain events.
 */
public class BukkitGameModeListener implements Listener {

    private final EventPipeline pipeline;

    public BukkitGameModeListener(EventPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        pipeline.process(new GameModeChangedEvent(
                event.getPlayer().getName(),
                event.getPlayer().getGameMode().name(),
                event.getNewGameMode().name()
        ));
    }
}

