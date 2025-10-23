package com.myst1cs04p.spycord.commands;
import org.bukkit.command.CommandExecutor;

import com.myst1cs04p.spycord.SpyCord;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class StatusCommand implements CommandExecutor {

    private final SpyCord plugin;

    public StatusCommand(SpyCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        plugin.log(
            Component.text("Spycord is ", NamedTextColor.WHITE)
                .append(Component.text(plugin.getIsEnabled() ? "actively logging " : "not actively logging ", plugin.getIsEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED))
                .append(Component.text("commands.", NamedTextColor.WHITE)),
                sender);
        return true;
    }
}
