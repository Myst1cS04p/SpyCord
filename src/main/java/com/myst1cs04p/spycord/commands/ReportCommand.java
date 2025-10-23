package com.myst1cs04p.spycord.commands;

import org.bukkit.command.CommandExecutor;

import com.myst1cs04p.spycord.SpyCord;

public class ReportCommand implements CommandExecutor {

    private final SpyCord plugin;
    public ReportCommand(SpyCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        plugin.log("This feature has not yet been implemented.", sender);
        return false;
    }
    
}
