package com.myst1cs04p.spycord.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import com.myst1cs04p.spycord.SpyCord;

public class CommandLogger implements Listener {
    private final SpyCord plugin;

    public CommandLogger(SpyCord spycord){
        this.plugin = spycord;
    }


    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if(!plugin.getIsEnabled("command-logger")){
            return;
        }
        List<String> commandList = plugin.getSensitiveCommands();
        Player player = event.getPlayer();

        String command = event.getMessage();
        
        if (commandList.contains(command.split(" ")[0].replace("/", ""))) {
            plugin.getCommandLogger().log(String.format(" [%s] %s", player.getName(), command));
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if(!plugin.getIsEnabled("command-logger")){
            return;
        }
        List<String> commandList = plugin.getSensitiveCommands();
        String command = event.getCommand();

        if (commandList.contains(command.split(" ")[0].replace("/", ""))) {
            plugin.getCommandLogger().log(String.format(" [CONSOLE] %s", command));
        }
        
    }
    
}
