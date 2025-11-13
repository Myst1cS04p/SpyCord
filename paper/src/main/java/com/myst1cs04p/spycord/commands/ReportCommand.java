package com.myst1cs04p.spycord.commands;
import org.bukkit.command.CommandSender;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.SpyCord;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class ReportCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(SpyCord plugin){
        return Commands.literal("report").executes(ctx->{
            CommandSender sender = ctx.getSource().getSender();
            plugin.log("This feature has not yet been implemented.", sender);
            return 1;
        });
    }
    
}
