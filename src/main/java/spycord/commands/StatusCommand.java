package spycord.commands;
import org.bukkit.command.CommandExecutor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import spycord.SpyCord;

public class StatusCommand implements CommandExecutor {

    private final SpyCord plugin;

    public StatusCommand(SpyCord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        plugin.Log(
            Component.text("Spycord is ", NamedTextColor.WHITE)
                .append(Component.text(plugin.isEnabled ? "actively logging " : "not actively logging ", plugin.isEnabled ? NamedTextColor.GREEN : NamedTextColor.RED))
                .append(Component.text("commands.", NamedTextColor.WHITE)),
                sender);
        return true;
    }
}
