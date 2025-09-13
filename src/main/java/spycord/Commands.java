package spycord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Commands implements TabExecutor {

    private final JavaPlugin plugin;

    public Commands(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage("Usage: /spycord <version|v|ver>");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        if (subcommand.equals("version") || subcommand.equals("v") || subcommand.equals("ver")) {
            String version = plugin.getDescription().getVersion();
            sender.sendMessage("Spycord plugin version: " + version);
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = Arrays.asList("version", "v", "ver");
            List<String> result = new ArrayList<>();
            String current = args[0].toLowerCase();

            for (String option : completions) {
                if (option.startsWith(current)) {
                    result.add(option);
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

}
