package spycord.commands;

import org.bukkit.command.CommandExecutor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import spycord.SpyCord;

public class ToggleCommand implements CommandExecutor {

    private final SpyCord plugin;

    public ToggleCommand(SpyCord plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label,
            String[] args) {
        if (sender.hasPermission("spycord.toggle")) {
            plugin.isEnabled = !plugin.isEnabled;
            
            SpyCord.getDiscord()
                    .sendToDiscord((plugin.isEnabled ? "✅✅✅" : "🛑🛑🛑") + 
                    "@everyone The plugin has been toggled " + 
                    (plugin.isEnabled ? "on" : "off") + 
                    " by " + sender.getName() + 
                    (plugin.isEnabled ? "✅✅✅" : "🛑🛑🛑"));

            plugin.Log(Component.text("Toggling plugin ", NamedTextColor.WHITE)
                    .append(Component.text(plugin.isEnabled ? "enabled" : "disabled",
                            plugin.isEnabled ? NamedTextColor.GREEN : NamedTextColor.RED)),
                    sender);
            return true;
        }
        return false;
    }

}
