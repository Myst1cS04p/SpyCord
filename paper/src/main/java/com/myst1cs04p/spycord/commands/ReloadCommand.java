package com.myst1cs04p.spycord.commands;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.SpyCord;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class ReloadCommand{

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(SpyCord plugin){
        return Commands.literal("reload")
            .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();

                    plugin.reloadPlugin();
                    SpyCord.getDiscord().sendToDiscord(
                            "@everyone the configuration has been **reloaded** by **" + sender.getName() + "**");
                    plugin.log("Configuration reloaded.", sender);
                    return 1;
            });
    }    
}
