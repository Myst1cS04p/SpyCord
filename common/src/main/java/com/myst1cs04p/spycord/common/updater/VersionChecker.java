package com.myst1cs04p.spycord.common.updater;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Fetches the latest release version from GitHub asynchronously.
 */
public class VersionChecker {

    private final Logger logger;
    private final String owner;
    private final String repo;

    private volatile String cachedLatestVersion;

    public VersionChecker(Logger logger, String owner, String repo) {
        this.logger = logger;
        this.owner = owner;
        this.repo = repo;
    }

    /**
     * @return CompletableFuture resolving to the latest version tag, or null on failure.
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

                JSONObject release = (JSONObject) new JSONParser().parse(response.toString());
                this.cachedLatestVersion = (String) release.get("tag_name");
                return cachedLatestVersion;

            } catch (Exception e) {
                logger.warning("[SpyCord] Failed to fetch version info from GitHub: " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Returns true if latest is a higher semantic version than current.
     */
    public boolean isNewerVersion(String latest, String current) {
        if (latest == null || current == null) return false;

        String[] lp = latest.replaceAll("[^0-9.]", "").split("\\.");
        String[] cp = current.replaceAll("[^0-9.]", "").split("\\.");

        int length = Math.max(lp.length, cp.length);
        for (int i = 0; i < length; i++) {
            int l = i < lp.length ? Integer.parseInt(lp[i]) : 0;
            int c = i < cp.length ? Integer.parseInt(cp[i]) : 0;
            if (l > c) return true;
            if (l < c) return false;
        }
        return false;
    }

    public String getCachedLatestVersion() {
        return cachedLatestVersion;
    }
}

