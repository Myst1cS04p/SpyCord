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
    private List<String> commandList;
    private boolean isModeWhitelist;

    public CommandMapInterceptor(CommandMap original, SpyCord plugin, boolean mode) {
        this.original = original;
        this.plugin = plugin;
        this.commandList = plugin.getSensitiveCommands();
        this.isModeWhitelist = mode;
    }

    public void UpdateMode(boolean Mode){
        this.isModeWhitelist = Mode;
    }
    public void UpdateCommands(List<String> commandList){
        this.commandList = commandList;
    }

    @Override
    public boolean dispatch(CommandSender sender, String commandLine) {
        if (!plugin.isEnabled()) {
            // If the plugin is disabled, just dispatch the command normally
            return original.dispatch(sender, commandLine);
        }

        // Normalize commandLine for comparison
        String lowerCommandLine = commandLine.toLowerCase();

        // Check if the command matches any in the list
        boolean matchesList = commandList.stream()
                .anyMatch(lowerCommandLine::contains);

        // Decide whether to log based on mode and match
        if ((isModeWhitelist && matchesList) || (!isModeWhitelist && !matchesList)) {
            plugin.getCommandLogger().log(" [" + sender + "] /" + commandLine);
        }

        // Always dispatch the command regardless
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
