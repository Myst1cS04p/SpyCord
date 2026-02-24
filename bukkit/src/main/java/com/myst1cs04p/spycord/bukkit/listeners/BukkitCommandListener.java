package com.myst1cs04p.spycord.bukkit.listeners;

import com.myst1cs04p.spycord.common.events.CommandExecutedEvent;
import com.myst1cs04p.spycord.common.events.EventPipeline;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * Translates Bukkit command events into domain events and feeds them to the pipeline.
 */
public class BukkitCommandListener implements Listener {

    private final EventPipeline pipeline;

    public BukkitCommandListener(EventPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        pipeline.process(new CommandExecutedEvent(
                event.getPlayer().getName(),
                event.getMessage(),
                false
        ));
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        pipeline.process(new CommandExecutedEvent(
                "CONSOLE",
                event.getCommand(),
                true
        ));
    }
}

