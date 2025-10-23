package com.myst1cs04p.spycord.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.myst1cs04p.spycord.SpyCord;

public class ReloadCommand implements CommandExecutor {

    private final SpyCord plugin;

    public ReloadCommand(SpyCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadPlugin();
        SpyCord.getDiscord().sendToDiscord("@everyone the configuration has been **reloaded** by **" + sender.getName() + "**");
        plugin.log("Configuration reloaded.", sender);
        return true;
    }
    
}
