package com.myst1cs04p.spycord.paper.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReportCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create(JavaPlugin plugin) {
        return Commands.literal("report").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            sender.sendMessage("[SpyCord] This feature has not yet been implemented.");
            return 1;
        });
    }
}

