package com.myst1cs04p.spycord.paper.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.common.ServiceRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class StatusCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create(ServiceRegistry services) {
        return Commands.literal("status").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            boolean enabled = services.getConfig().getBoolean("enabled", true);

            sender.sendMessage(Component.text("[SpyCord] SpyCord is ", NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text(
                            enabled ? "actively logging" : "not actively logging",
                            enabled ? NamedTextColor.GREEN : NamedTextColor.RED))
                    .append(Component.text(" commands.", NamedTextColor.WHITE)));
            return 1;
        });
    }
}

