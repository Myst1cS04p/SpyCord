package com.myst1cs04p.spycord.commands;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.SpyCord;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class HelpCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(SpyCord plugin){
        return Commands.literal("help")
            .executes(ctx -> {
                CommandSender sender = ctx.getSource().getSender();
                plugin.log(Component.text(
                    "SPYCORD COMMANDS", NamedTextColor.AQUA)
                    .append(Component.text("\n-------------------", NamedTextColor.WHITE))
                    .append(Component.text("\n/spycord ", NamedTextColor.AQUA))
                    .append(Component.text("help: Displays this menu", NamedTextColor.WHITE))
                    .append(Component.text("\n/spycord ", NamedTextColor.AQUA))
                    .append(Component.text("reload: Reloads the config"))
                    .append(Component.text("\n/spycord ", NamedTextColor.AQUA))
                    .append(Component.text("status: Shows whether the plugin is actively logging", NamedTextColor.WHITE))
                    .append(Component.text("\n/spycord ", NamedTextColor.AQUA))
                    .append(Component.text("toggle: Turns the plugin on or off", NamedTextColor.WHITE))
                    .append(Component.text("\n/spycord ", NamedTextColor.AQUA))
                    .append(Component.text("version: Displays the running plugin version", NamedTextColor.WHITE)), sender);
                return 1;
            });
    }
    
}
