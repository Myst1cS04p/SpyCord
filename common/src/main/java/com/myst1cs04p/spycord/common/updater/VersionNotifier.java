package com.myst1cs04p.spycord.common.updater;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Platform-agnostic version notifier.
 * The scheduler abstraction (IUpdateScheduler) is injected so Bukkit and Paper
 * can each provide their own repeating task mechanism.
 */
public class VersionNotifier {

    private final VersionChecker checker;
    private final String currentVersion;
    private Consumer<String> onUpdateCallback;

    public VersionNotifier(Logger logger, String owner, String repo, String currentVersion) {
        this.checker = new VersionChecker(logger, owner, repo);
        this.currentVersion = currentVersion;
    }

    public VersionNotifier onUpdate(Consumer<String> callback) {
        this.onUpdateCallback = callback;
        return this;
    }

    /**
     * Performs one version check. Call this from whatever repeating scheduler
     * the platform provides (BukkitRunnable, Paper async scheduler, etc.)
     */
    public void checkOnce() {
        checker.fetchLatestVersion().thenAccept(latest -> {
            if (latest == null) return;
            if (checker.isNewerVersion(latest, currentVersion)) {
                if (onUpdateCallback != null) {
                    onUpdateCallback.accept(latest);
                }
            }
        });
    }
}

