package com.myst1cs04p.spycord.bukkit;

import com.myst1cs04p.spycord.bukkit.commands.BukkitSpyCordCommand;
import com.myst1cs04p.spycord.bukkit.config.BukkitPluginConfig;
import com.myst1cs04p.spycord.bukkit.listeners.BukkitCommandListener;
import com.myst1cs04p.spycord.bukkit.listeners.BukkitConnectionListener;
import com.myst1cs04p.spycord.bukkit.listeners.BukkitGameModeListener;
import com.myst1cs04p.spycord.common.ServiceRegistry;
import com.myst1cs04p.spycord.common.discord.WebhookDiscordClient;
import com.myst1cs04p.spycord.common.updater.VersionNotifier;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

// Bukkit entry point
public class SpyCordBukkit extends JavaPlugin {

    protected ServiceRegistry services;

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Initializes the config

        BukkitPluginConfig config = new BukkitPluginConfig(this);

        WebhookDiscordClient discord = new WebhookDiscordClient(
                getLogger(),
                config.getString("webhook-url", "")
        );

        services = new ServiceRegistry(config, discord, getLogger());

        registerListeners();
        registerCommands();

        services.getDiscordClient().send("**✅ THE PLUGIN HAS BEEN ENABLED AND WILL LOG COMMANDS ✅**");
        printSplash();
    }

    @Override
    public void onDisable() {
        if (services != null) {
            services.getDiscordClient().send("""
                    **🛑 THE PLUGIN HAS BEEN DISABLED AND WILL NOT LOG COMMANDS 🛑**
                    -# This could be due to the server closing.
                    """);
        }
    }

    protected void registerListeners() {
        getServer().getPluginManager().registerEvents(
                new BukkitCommandListener(services.getEventPipeline()), this);
        getServer().getPluginManager().registerEvents(
                new BukkitGameModeListener(services.getEventPipeline()), this);
        getServer().getPluginManager().registerEvents(
                new BukkitConnectionListener(services.getEventPipeline()), this);
    }

    protected void registerCommands() {
        BukkitSpyCordCommand handler = new BukkitSpyCordCommand(this, services);
        getCommand("spycord").setExecutor(handler);
        getCommand("spycord").setTabCompleter(handler);
    }

    protected void startVersionChecker(){
        String currentVersion = getDescription().getVersion();

        VersionNotifier notifier = new VersionNotifier(getLogger(), "Myst1cS04p", "SpyCord", currentVersion)
                .onUpdate(version -> {
                    String message = """
                            # New SpyCord Version Available!
                            ## Version %s
                            - **Modrinth**: https://modrinth.com/plugin/spycord
                            - **GitHub**: https://github.com/Myst1cS04p/SpyCord/releases
                            - **SpigotMC**: https://www.spigotmc.org/resources/spycord.129615/
                            - **Hangar**: https://hangar.papermc.io/Myst1cS04p/Spycord
                            """.formatted(version);

                    getLogger().info("A new version of SpyCord is available: " + version);
                    services.getDiscordClient().send(message);
                });

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, notifier::checkOnce, 0, 12 * 60 * 60 * 20L);
    }

    private void printSplash() {

                getLogger().info("""
                \u001B[35m
                @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@   ┏━┓┏━┓╻ ╻┏━╸┏━┓┏━┓╺┳┓
                @@@@@         @@@@@@@@@         @@@@@   ┗━┓┣━┛┗┳┛┃  ┃ ┃┣┳┛ ┃┃
                @@@@@         @@@@@@@@@         @@@@@   ┗━┛╹   ╹ ┗━╸┗━┛╹┗╸╺┻┛
                @@@@@         @@@@@@@@@         @@@@@
                @@@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@
                @@@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@
                @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                \u001B[0m
                """);
        getLogger().info("""
                \u001B[34m

                \u001B[0m
                """);
    }
}

