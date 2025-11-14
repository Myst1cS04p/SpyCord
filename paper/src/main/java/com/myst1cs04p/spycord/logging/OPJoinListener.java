package com.myst1cs04p.spycord.logging;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.myst1cs04p.spycord.SpyCord;

public class OPJoinListener implements Listener {

    private final SpyCord plugin;

    public OPJoinListener(SpyCord plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!plugin.getIsEnabled() && !plugin.getIsEnabled("join-logger")){
            return;
        }

        
        if(event.getPlayer().isOp()){
            String logEntry = String.format("[OP] %s joined the game.", event.getPlayer().getName());
            SpyCord.getCommandLogger().log(logEntry);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if(!plugin.getIsEnabled() && !plugin.getIsEnabled("join-logger")){
            return;
        }

        
        if(event.getPlayer().isOp()){
            String logEntry = String.format("[OP] %s left the game.", event.getPlayer().getName());
            SpyCord.getCommandLogger().log(logEntry);
        }

    }

}
