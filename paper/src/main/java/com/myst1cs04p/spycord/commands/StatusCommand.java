package com.myst1cs04p.spycord.commands;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.SpyCord;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class StatusCommand {


    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(SpyCord plugin){
        return Commands.literal("status")
            .executes(ctx ->{
                CommandSender sender = ctx.getSource().getSender();

                plugin.log(
                    Component.text("Spycord is ", NamedTextColor.WHITE)
                        .append(Component.text(plugin.getIsEnabled() ? "actively logging " : "not actively logging ", plugin.getIsEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED))
                        .append(Component.text("commands.", NamedTextColor.WHITE)),
                        sender);
                return 1;
            });
    }
}
