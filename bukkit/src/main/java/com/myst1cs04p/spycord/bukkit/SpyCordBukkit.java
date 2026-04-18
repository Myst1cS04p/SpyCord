package com.myst1cs04p.spycord.bukkit;

import com.myst1cs04p.spycord.bukkit.commands.BukkitSpyCordCommand;
import com.myst1cs04p.spycord.bukkit.config.BukkitPluginConfig;
import com.myst1cs04p.spycord.bukkit.listeners.BukkitCommandListener;
import com.myst1cs04p.spycord.bukkit.listeners.BukkitConnectionListener;
import com.myst1cs04p.spycord.bukkit.listeners.BukkitGameModeListener;
import com.myst1cs04p.spycord.common.ServiceRegistry;
import com.myst1cs04p.spycord.common.config.CachedPluginConfig;
import com.myst1cs04p.spycord.common.config.IPluginConfig;
import com.myst1cs04p.spycord.common.stats.ActivityTracker;
import com.myst1cs04p.spycord.common.updater.VersionNotifier;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Bukkit entry point. */
public class SpyCordBukkit extends JavaPlugin {

    protected ServiceRegistry services;

    // The 12 commands shipped in the default config
    private static final Set<String> DEFAULT_SENSITIVE_COMMANDS = Set.of(
            "op", "deop", "ban", "pardon", "gamemode", "give",
            "stop", "kill", "spycord", "kick", "data", "gamerule"
    );

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Wrap the raw Bukkit config in a cache so every repeated key lookup
        // is served from a ConcurrentHashMap instead of YAML traversal.
        IPluginConfig config = new CachedPluginConfig(new BukkitPluginConfig(this));

        services = new ServiceRegistry(config, getLogger());

        registerListeners();
        registerCommands();

        services.getDiscordClient().send(
                config.getString("messages.plugin-enabled",
                        "**✅ THE PLUGIN HAS BEEN ENABLED AND WILL LOG COMMANDS ✅**"));

        printSplash();

        int pluginId = 29759;
        Metrics metrics = new Metrics(this, pluginId);
        registerCustomCharts(metrics, config);

        startVersionChecker();
        startActivityDigest(config);
    }

    @Override
    public void onDisable() {
        if (services != null) {
            IPluginConfig config = services.getConfig();
            services.getDiscordClient().send(
                    config.getString("messages.plugin-disabled",
                            "**🛑 THE PLUGIN HAS BEEN DISABLED AND WILL NOT LOG COMMANDS 🛑**\n" +
                            "-# This could be due to the server closing."));
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

    // CUSTOM BSTATS CHARTS 

    private void registerCustomCharts(Metrics metrics, IPluginConfig config) {
        ActivityTracker tracker = services.getActivityTracker();

        // Is the webhook properly configured?
        metrics.addCustomChart(new SimplePie("webhook_configured", () -> {
            String url = config.getString("webhook-url", "");
            return url.startsWith("https://discord.com/api/webhooks/") ? "Configured" : "Not configured";
        }));

        // How many sensitive commands has the plugin logged in total?
        metrics.addCustomChart(new SingleLineChart("commands_logged_total",
                () -> (int) Math.min(tracker.getTotalCommandsLogged(), Integer.MAX_VALUE)));

        // Have server admins customised the sensitive-commands list?
        metrics.addCustomChart(new SimplePie("command_customization_rate", () -> {
            List<String> configured = config.getStringList("command-logging.sensitive-commands");
            Set<String> current = Set.copyOf(
                    configured.stream().map(String::toLowerCase).toList());
            return current.equals(DEFAULT_SENSITIVE_COMMANDS) ? "Default list" : "Customised";
        }));

        // How many operators does this server have?
        metrics.addCustomChart(new SimplePie("op_count_range", () -> {
            int ops = Bukkit.getOperators().size();
            if (ops == 0)       return "0";
            if (ops <= 2)       return "1-2";
            if (ops <= 5)       return "3-5";
            if (ops <= 10)      return "6-10";
            return "11+";
        }));

        // Webhook delivery success vs failure breakdown
        metrics.addCustomChart(new AdvancedPie("webhook_delivery_rate", () -> {
            Map<String, Integer> result = new HashMap<>();
            long success = tracker.getTotalWebhookSuccess();
            long failure = tracker.getTotalWebhookFailure();
            if (success > 0) result.put("Success", (int) Math.min(success, Integer.MAX_VALUE));
            if (failure > 0) result.put("Failure", (int) Math.min(failure, Integer.MAX_VALUE));
            return result;
        }));
    }

    // Activity Digest

    /**
     * Schedules a periodic Discord message summarising what SpyCord monitored
     * during the last period. Skips the send entirely if nothing happened, to
     * avoid noise on quiet servers.
     *
     * <p>Interval is controlled by {@code activity-digest.interval} in config:
     * {@code daily} = 24 h, {@code weekly} = 7 days (default).
     */
    protected void startActivityDigest(IPluginConfig config) {
        if (!config.getBoolean("activity-digest.enabled", true)) return;

        String interval = config.getString("activity-digest.interval", "weekly");
        long ticks = interval.equalsIgnoreCase("daily")
                ? 24L * 60 * 60 * 20        //  1 day  in ticks
                : 7L * 24 * 60 * 60 * 20;   //  1 week in ticks

        String periodLabel = interval.equalsIgnoreCase("daily") ? "today" : "this week";

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            ActivityTracker.DigestSnapshot snapshot = services.getActivityTracker().flushPeriod();

            if (snapshot.isEmpty()) return;

            String message = String.format("""
                    **SpyCord Activity Digest**
                    Here's what SpyCord monitored %s:

                    - **%d** sensitive command%s logged
                    - **%d** gamemode change%s detected
                    - **%d** OP join/quit event%s recorded

                    -# SpyCord is actively protecting your server.
                    """,
                    periodLabel,
                    snapshot.commands(),       snapshot.commands()       == 1 ? "" : "s",
                    snapshot.gamemodeChanges(), snapshot.gamemodeChanges() == 1 ? "" : "s",
                    snapshot.joinQuitEvents(),  snapshot.joinQuitEvents()  == 1 ? "" : "s"
            );

            services.getDiscordClient().send(message);

        }, ticks, ticks); // delay = period (first digest fires after one full period)
    }

    // Version Checker

    protected void startVersionChecker() {
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

        Bukkit.getScheduler().runTaskTimerAsynchronously(this,
                notifier::checkOnce, 0, 12L * 60 * 60 * 20);
    }

    // Splash

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
    }
}