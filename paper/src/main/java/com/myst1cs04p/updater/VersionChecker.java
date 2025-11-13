package com.myst1cs04p.updater;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * Simple async version checker for GitHub releases.
 * 
 * Fetches the latest release version from a GitHub repository.
 * 
 * Example:
 * new VersionChecker(plugin, "Myst1cS04p", "SpyCord")
 * .fetchLatestVersion()
 * .thenAccept(version -> plugin.getLogger().info("Latest: " + version));
 */
public class VersionChecker {

    private final JavaPlugin plugin;
    private final String owner;
    private final String repo;

    private String latestVersion;

    public VersionChecker(JavaPlugin plugin, String owner, String repo) {
        this.plugin = plugin;
        this.owner = owner;
        this.repo = repo;
    }

    /**
     * Fetches the latest version asynchronously from GitHub.
     * 
     * @return CompletableFuture that completes with the version string, or null on
     *         failure.
     */
    public CompletableFuture<String> fetchLatestVersion() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";
                HttpURLConnection connection = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject releaseData = (JSONObject) new JSONParser().parse(response.toString());
                this.latestVersion = (String) releaseData.get("tag_name");

                return latestVersion;

            } catch (Exception e) {
                plugin.getLogger().warning("[Updater] Failed to fetch version info from GitHub: " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Compares two semantic version strings.
     * 
     * @return true if the first version is newer.
     */
    public boolean isNewerVersion(String latest, String current) {
        if (latest == null || current == null)
            return false;

        String[] latestParts = latest.replaceAll("[^0-9.]", "").split("\\.");
        String[] currentParts = current.replaceAll("[^0-9.]", "").split("\\.");

        int length = Math.max(latestParts.length, currentParts.length);
        for (int i = 0; i < length; i++) {
            int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
            int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;

            if (latestPart > currentPart)
                return true;
            if (latestPart < currentPart)
                return false;
        }
        return false;
    }

    public String getLatestVersionCached() {
        return latestVersion;
    }
}
