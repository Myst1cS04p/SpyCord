package com.myst1cs04p.spycord;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.myst1cs04p.spycord.commandLogging.CommandLogger;
import com.myst1cs04p.spycord.commandLogging.GameModeListener;
import com.myst1cs04p.spycord.commandLogging.Logger;
import com.myst1cs04p.spycord.commandLogging.OPJoinListener;
import com.myst1cs04p.updater.VersionNotifier;
import com.myst1cs04p.spycord.commands.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class SpyCord extends JavaPlugin {

    private static SpyCord instance;
    private static DiscordManager discordManager;
    private static Logger commandLogger;

    private boolean isEnabled;

    // -------------------- Lifecycle --------------------

    @Override
    public void onEnable() { 
        instance = this;
        saveDefaultConfig();

        this.isEnabled = getConfig().getBoolean("enabled", true);
        discordManager = new DiscordManager(this, getConfig().getString("webhook-url"));

        registerListeners();
        registerCommands();
        startVersionCheckerTask();

        discordManager.sendToDiscord("@everyone **✅ THE PLUGIN HAS BEEN ENABLED AND WILL LOG COMMANDS ✅**");
        new Metrics(this, 27671);

        commandLogger = new Logger(this);
        printCommandList();
        printSplash();
    }

    @Override
    public void onDisable() {
        discordManager.sendToDiscord("""
                @everyone **🛑 THE PLUGIN HAS BEEN DISABLED AND WILL NOT LOG COMMANDS 🛑**
                -# This could be due to the server closing.
                """);
    }

    // -------------------- Initialization Helpers --------------------

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new CommandLogger(this), this);
        Bukkit.getPluginManager().registerEvents(new GameModeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new OPJoinListener(this), this);
    }

    private void registerCommands() {
        LiteralCommandNode<CommandSourceStack> command = Commands.literal("spycord")
            .then(ReloadCommand.createCommand(this))
            .then(ReportCommand.createCommand(this))
            .then(StatusCommand.createCommand(this))
            .then(ToggleCommand.createCommand(this))
            .then(VersionCommand.createCommand(this))
            .build();
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, cmd ->{
            cmd.registrar().register(command);
        });
    }

    private void startVersionCheckerTask() {
        new VersionNotifier(this, "Myst1cS04p", "SpyCord")
                .onUpdate((String version) -> {
                    String message = """
                            # New SpyCord Version Available!
                            ## Version %s
                            - **Modrinth**: https://modrinth.com/plugin/spycord
                            - **GitHub**: https://github.com/Myst1cS04p/SpyCord/releases
                            - **SpigotMC**: https://www.spigotmc.org/resources/spycord.129615/
                            - **Hangar**: https://hangar.papermc.io/Myst1cS04p/Spycord
                            """.formatted(version);

                    getLogger().log(Level.INFO, "A new version of SpyCord is available: {0}", version);
                    SpyCord.getDiscord().sendToDiscord(message);
                })
                .withInterval(12 * 60 * 60 * 20L)
                .start();
    }

    // -------------------- Utility Methods --------------------



    private void printCommandList() {
        List<String> commandList = getSensitiveCommands();
        String formattedList = commandList.stream()
                .map(cmd -> "*" + cmd.trim() + "*")
                .collect(Collectors.joining("\n"));

        discordManager.sendToDiscord("## Commands being logged:\n" + formattedList);
    }

    private void printSplash() {
        getLogger().info("""
                \u001B[34m
                ┏━┓┏━┓╻ ╻┏━╸┏━┓┏━┓╺┳┓
                ┗━┓┣━┛┗┳┛┃  ┃ ┃┣┳┛ ┃┃
                ┗━┛╹   ╹ ┗━╸┗━┛╹┗╸╺┻┛
                \u001B[0m
                """);
        getLogger().info("""
                \u001B[35m
                @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@         @@@@@@@@@         @@@@@
                @@@@@         @@@@@@@@@         @@@@@
                @@@@@         @@@@@@@@@         @@@@@
                @@@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@
                @@@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@
                @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                \u001B[0m
                """);
    }

    // -------------------- Public Logging --------------------

    public void reloadPlugin() {
        reloadConfig();
        isEnabled = getConfig().getBoolean("enabled", true);
        if (!isEnabled) {
            discordManager.sendToDiscord("@everyone **🛑 THE PLUGIN HAS BEEN DISABLED AND WILL NOT LOG COMMANDS 🛑**");
        }

        printCommandList();
        discordManager.SetWebhookUrl(getConfig().getString("webhook-url"));
    }

    public void log(String message) {
        getLogger().log(Level.INFO, "\u001b[35m\u001b[1m[SPYCORD]\u001b[0m {0}", message);
    }

    public void log(String message, CommandSender sender) {
        sender.sendMessage(Component.text("[SPYCORD] ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(message, NamedTextColor.WHITE)));
    }

    public void log(Component message, CommandSender sender) {
        sender.sendMessage(Component.text("[SPYCORD] ", NamedTextColor.LIGHT_PURPLE)
                .append(message));
    }

    public void togglePlugin(){
        isEnabled = !isEnabled;
    }

    public List<String> getSensitiveCommands() {
        List<String> sensitiveCommands = getConfig().getStringList("sensitive-commands");
        return sensitiveCommands.stream().map(String::toLowerCase).toList();
    }

    
    // -------------------- Getters --------------------
    public Logger getCommandLogger(){
        return commandLogger;
    }
    
    public static SpyCord getInstance() {
        return instance;
    }

    public static DiscordManager getDiscord() {
        return discordManager;
    }

    public boolean getIsEnabled(){
        return isEnabled;
    }

    public boolean getIsEnabled(String module){
        return getConfig().getBoolean("modules." + module);
    }
}
