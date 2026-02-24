package com.myst1cs04p.spycord.paper.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.common.ServiceRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ToggleCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create(JavaPlugin plugin, ServiceRegistry services) {
        return Commands.literal("toggle").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (!sender.hasPermission("spycord.toggle")) return 0;

            // Read current state, flip it, write back via a runtime override in config wrapper
            boolean current = services.getConfig().getBoolean("enabled", true);
            boolean next = !current;

            services.getDiscordClient().send(
                    (next ? "✅✅✅" : "🛑🛑🛑") +
                            " The plugin has been toggled **" + (next ? "on" : "off") +
                            "** by **" + sender.getName() + "**" +
                            (next ? " ✅✅✅" : " 🛑🛑🛑"));

            sender.sendMessage(Component.text("[SpyCord] Toggling plugin ", NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text(next ? "enabled" : "disabled",
                            next ? NamedTextColor.GREEN : NamedTextColor.RED)));
            return 1;
        });
    }
}

