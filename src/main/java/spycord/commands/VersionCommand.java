package spycord.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import spycord.SpyCord;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class VersionCommand implements CommandExecutor {

    private final SpyCord plugin;

    public VersionCommand(spycord.SpyCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if(sender.hasPermission("spycord.version")) {
            plugin.Log(Component.text("Running SpyCord Version: ", TextColor.fromHexString("#fff")).append(Component.text(plugin.getPluginMeta().getVersion(), TextColor.fromHexString("#e6ea1aff"))), sender);
            return true;
        }
        return false;
    }
}
