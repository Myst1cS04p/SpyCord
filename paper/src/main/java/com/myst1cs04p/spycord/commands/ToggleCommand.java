package com.myst1cs04p.spycord.commands;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.SpyCord;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ToggleCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(SpyCord plugin) {
        return Commands.literal("toggle").executes(ctx -> {

            CommandSender sender = ctx.getSource().getSender();

            if (sender.hasPermission("spycord.toggle")) {
                plugin.togglePlugin();

                SpyCord.getDiscord()
                        .sendToDiscord((plugin.getIsEnabled() ? "✅✅✅" : "🛑🛑🛑")
                                + "The plugin has been toggled **" + (plugin.getIsEnabled() ? "on" : "off")
                                + "** by **" + sender.getName() + "**" + (plugin.getIsEnabled() ? "✅✅✅" : "🛑🛑🛑"));

                plugin.log(Component.text("Toggling plugin ", NamedTextColor.WHITE)
                        .append(Component.text(plugin.getIsEnabled() ? "enabled" : "disabled",
                                plugin.getIsEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED)),
                        sender);
                return 1;
            }
            return 0;
        });
    }
}
