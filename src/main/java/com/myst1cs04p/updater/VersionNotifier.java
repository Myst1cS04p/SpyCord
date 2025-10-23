package com.myst1cs04p.updater;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.function.Consumer;

/**
 * A reusable module that periodically checks for updates to your plugin
 * and runs a callback when a newer version is available.
 *
 * Example:
 * new GenericVersionNotifier(this, "Myst1cS04p", "SpyCord")
 * .onUpdate(version -> getLogger().info("New version available: " + version))
 * .start();
 */
public class VersionNotifier {

    private final JavaPlugin plugin;
    private final VersionChecker versionChecker;
    private Consumer<String> onUpdateCallback = null;

    private long checkIntervalTicks = 12 * 60 * 60 * 20L; // default: 12 hours

    public VersionNotifier(JavaPlugin plugin, String author, String repoName) {
        this.plugin = plugin;
        this.versionChecker = new VersionChecker(plugin, author, repoName);
    }

    /**
     * Sets how often to check for updates (in ticks).
     */
    public VersionNotifier withInterval(long ticks) {
        this.checkIntervalTicks = ticks;
        return this;
    }

    /**
     * Sets a callback to run when a new version is detected.
     */
    public VersionNotifier onUpdate(Consumer<String> callback) {
        this.onUpdateCallback = callback;
        return this;
    }

    /**
     * Starts the repeating update check.
     */
    public void start() {
    new BukkitRunnable() {
        @Override
        public void run() {
            versionChecker.fetchLatestVersion().thenAccept(latest -> {
                if (latest == null) return;

                String current = plugin.getPluginMeta().getVersion();
                if (versionChecker.isNewerVersion(latest, current)) {
                    if (onUpdateCallback != null) {
                        Bukkit.getScheduler().runTask(plugin, () -> onUpdateCallback.accept(latest));
                    } else {
                        plugin.getLogger().info("[Updater] New version available: " + latest);
                    }
                }
            });
        }
    }.runTaskTimer(plugin, 0L, checkIntervalTicks);
}

}
