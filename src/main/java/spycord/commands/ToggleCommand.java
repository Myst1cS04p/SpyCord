package spycord.commands;

import org.bukkit.command.CommandExecutor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import spycord.SpyCord;

public class ToggleCommand implements CommandExecutor {

    private final SpyCord plugin;
    public ToggleCommand(SpyCord plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender.hasPermission("spycord.toggle")) {
            plugin.isEnabled = !plugin.isEnabled;
            plugin.Log(Component.text("Toggling plugin ", TextColor.fromHexString("#fff"))
                    .append(Component.text(plugin.isEnabled ? "enabled" : "disabled", TextColor.fromHexString(
                            plugin.isEnabled ? "#0f0" : "#f00"))), sender);
            return true;
        }
        return false;
    }
    
}
