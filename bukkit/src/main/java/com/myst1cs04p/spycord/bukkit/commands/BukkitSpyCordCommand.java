package com.myst1cs04p.spycord.bukkit.commands;

import com.myst1cs04p.spycord.common.ServiceRegistry;
import com.myst1cs04p.spycord.common.config.IPluginConfig;
import com.myst1cs04p.spycord.common.discord.IDiscordClient;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Legacy CommandExecutor-based command handler for Bukkit.
 * Handles /spycord <subcommand> using the common ServiceRegistry.
 */
public class BukkitSpyCordCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final ServiceRegistry services;

    public BukkitSpyCordCommand(JavaPlugin plugin, ServiceRegistry services) {
        this.plugin = plugin;
        this.services = services;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("[SpyCord] Use /spycord help for a list of commands.");
            return true;
        }

        IPluginConfig config = services.getConfig();
        IDiscordClient discord = services.getDiscordClient();

        switch (args[0].toLowerCase()) {

            case "reload" -> {
                if (!sender.hasPermission("spycord.reload")) {
                    sender.sendMessage("[SpyCord] You don't have permission to do that.");
                    return true;
                }
                config.reload();
                discord.setWebhookUrl(config.getString("webhook-url", ""));
                discord.send("The configuration has been **reloaded** by **" + sender.getName() + "**");
                sender.sendMessage("[SpyCord] Configuration reloaded.");
            }

            case "status" -> {
                boolean enabled = config.getBoolean("enabled", true);
                sender.sendMessage("[SpyCord] SpyCord is " + (enabled ? "actively logging" : "not actively logging") + " commands.");
            }

            case "toggle" -> {
                if (!sender.hasPermission("spycord.toggle")) {
                    sender.sendMessage("[SpyCord] You don't have permission to do that.");
                    return true;
                }
                sender.sendMessage("[SpyCord] Toggle is not yet implemented in the Bukkit module.");
            }

            case "version" -> {
                if (!sender.hasPermission("spycord.version")) {
                    sender.sendMessage("[SpyCord] You don't have permission to do that.");
                    return true;
                }
                sender.sendMessage("[SpyCord] Running SpyCord version: " + plugin.getDescription().getVersion());
            }

            case "report" -> {
                sender.sendMessage("[SpyCord] This feature has not yet been implemented.");
            }

            case "help" -> {
                sender.sendMessage("""
                        [SpyCord] Commands:
                        /spycord help    - Displays this menu
                        /spycord reload  - Reloads the config
                        /spycord status  - Shows whether the plugin is actively logging
                        /spycord toggle  - Turns the plugin on or off
                        /spycord version - Displays the running plugin version
                        """);
            }

            default -> sender.sendMessage("[SpyCord] Unknown subcommand. Use /spycord help.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("help", "reload", "status", "toggle", "version", "report");
        }
        return List.of();
    }
}

