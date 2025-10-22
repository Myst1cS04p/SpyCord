package spycord.commands;

import org.bukkit.command.CommandExecutor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import spycord.SpyCord;

public class StatusCommand implements CommandExecutor {

    private final SpyCord plugin;

    public StatusCommand(SpyCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        plugin.Log(
            Component.text("Spycord is ", TextColor.fromHexString("#fff"))
                .append(Component.text(plugin.isEnabled ? "actively logging " : "not actively logging ", TextColor.fromHexString(plugin.isEnabled ? "#0f0" : "#f00")))
                .append(Component.text("commands.", TextColor.fromHexString("#fff"))),
                sender);
        return true;
    }
}
