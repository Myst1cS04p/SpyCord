package com.myst1cs04p.spycord.paper.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class VersionCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create(JavaPlugin plugin) {
        return Commands.literal("version").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (!sender.hasPermission("spycord.version")) return 0;

            sender.sendMessage(Component.text("[SpyCord] Running version: ", NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text(plugin.getPluginMeta().getVersion(), NamedTextColor.GOLD)));
            return 1;
        });
    }
}

