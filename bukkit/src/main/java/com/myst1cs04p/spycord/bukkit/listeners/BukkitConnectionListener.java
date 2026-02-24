package com.myst1cs04p.spycord.bukkit.listeners;

import com.myst1cs04p.spycord.common.events.EventPipeline;
import com.myst1cs04p.spycord.common.events.PlayerConnectionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Translates Bukkit join/quit events into domain events.
 */
public class BukkitConnectionListener implements Listener {

    private final EventPipeline pipeline;

    public BukkitConnectionListener(EventPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().isOp()) return;
        pipeline.process(new PlayerConnectionEvent(
                event.getPlayer().getName(),
                PlayerConnectionEvent.Type.JOIN
        ));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!event.getPlayer().isOp()) return;
        pipeline.process(new PlayerConnectionEvent(
                event.getPlayer().getName(),
                PlayerConnectionEvent.Type.QUIT
        ));
    }
}

