package com.myst1cs04p.spycord.paper.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.common.ServiceRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create(JavaPlugin plugin, ServiceRegistry services) {
        return Commands.literal("reload").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (!sender.hasPermission("spycord.reload")) return 0;

            services.getConfig().reload();
            services.getDiscordClient().setWebhookUrl(
                    services.getConfig().getString("webhook-url", ""));
            services.getDiscordClient().send(
                    "The configuration has been **reloaded** by **" + sender.getName() + "**");
            sender.sendMessage("[SpyCord] Configuration reloaded.");
            return 1;
        });
    }
}

