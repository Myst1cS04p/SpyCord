package com.myst1cs04p.spycord.commands;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.SpyCord;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class VersionCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(SpyCord plugin) {
        return Commands.literal("version").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();

            if (sender.hasPermission("spycord.version")) {
                plugin.log(Component.text("Running SpyCord Version: ", NamedTextColor.WHITE)
                        .append(Component.text(plugin.getPluginMeta().getVersion(), NamedTextColor.GOLD)), sender);
                return 1;
            }
            return 0;
        });
    }
}
