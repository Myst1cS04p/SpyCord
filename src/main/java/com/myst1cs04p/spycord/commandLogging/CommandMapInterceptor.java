package com.myst1cs04p.spycord.commandLogging;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import com.myst1cs04p.spycord.SpyCord;

public class CommandMapInterceptor implements CommandMap {
    private final CommandMap original;
    private final SpyCord plugin;

    CommandMapInterceptor(CommandMap original, SpyCord plugin) {
        this.original = original;
        this.plugin = plugin;
    }

    @Override
    public boolean dispatch(CommandSender sender, String commandLine) {
        plugin.getLogger().info("[Intercepted] " + sender.getName() + " ran: /" + commandLine);
        
        return original.dispatch(sender, commandLine);
    }

    // Delegate everything else
    @Override
    public void registerAll(String fallbackPrefix, java.util.List<Command> commands) {
        original.registerAll(fallbackPrefix, commands);
    }

    @Override
    public boolean register(String fallbackPrefix, Command command) {
        return original.register(fallbackPrefix, command);
    }

    @Override
    public boolean register(String fallbackPrefix, String label, Command command) {
        return original.register(fallbackPrefix, label, command);
    }

    @Override
    public Command getCommand(String name) {
        return original.getCommand(name);
    }

    @Override
    public java.util.Map<String, Command> getKnownCommands() {
        return original.getKnownCommands();
    }

    @Override
    public void clearCommands() {
        original.clearCommands();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String commandLine) {
        return original.tabComplete(sender, commandLine);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String commandLine, Location targetPos) {
        return original.tabComplete(sender, commandLine, targetPos);
    }

}
