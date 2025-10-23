package com.myst1cs04p.spycord.commands;

import org.bukkit.command.CommandExecutor;

import com.myst1cs04p.spycord.SpyCord;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ToggleCommand implements CommandExecutor {

    private final SpyCord plugin;

    public ToggleCommand(SpyCord plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label,
            String[] args) {
        if (sender.hasPermission("spycord.toggle")) {
            plugin.togglePlugin();

            SpyCord.getDiscord()
                    .sendToDiscord((plugin.getIsEnabled() ? "✅✅✅" : "🛑🛑🛑") + 
                    "@everyone The plugin has been toggled **" + 
                    (plugin.getIsEnabled() ? "on" : "off") + 
                    "** by **" + sender.getName() + "**" + 
                    (plugin.getIsEnabled() ? "✅✅✅" : "🛑🛑🛑"));

            plugin.log(Component.text("Toggling plugin ", NamedTextColor.WHITE)
                    .append(Component.text(plugin.getIsEnabled() ? "enabled" : "disabled",
                            plugin.getIsEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED)),
                    sender);
            return true;
        }
        return false;
    }

}
