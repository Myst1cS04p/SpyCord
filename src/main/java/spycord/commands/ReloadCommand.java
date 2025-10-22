package spycord.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import spycord.SpyCord;

public class ReloadCommand implements CommandExecutor {

    private final SpyCord plugin;

    public ReloadCommand(SpyCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.ReloadPlugin();
        SpyCord.getDiscord().sendToDiscord("@everyone the configuration has been **reloaded** by **" + sender.getName() + "**");
        plugin.Log("Configuration reloaded.", sender);
        return true;
    }
    
}
