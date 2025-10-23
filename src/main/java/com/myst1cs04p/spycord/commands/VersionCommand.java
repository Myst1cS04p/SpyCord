package com.myst1cs04p.spycord.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import com.myst1cs04p.spycord.SpyCord;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class VersionCommand implements CommandExecutor {

    private final SpyCord plugin;

    public VersionCommand(com.myst1cs04p.spycord.SpyCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if(sender.hasPermission("spycord.version")) {
            plugin.log(Component.text("Running SpyCord Version: ", NamedTextColor.WHITE).append(Component.text(plugin.getPluginMeta().getVersion(), NamedTextColor.GOLD)), sender);
            return true;
        }
        return false;
    }
}
