package updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


public class VersionChecker implements Listener {

    private final String owner;
    private final String repo;
    private final JavaPlugin plugin;
    public String latestVersion;
    private Component updateMessage;

    public VersionChecker(String owner, String repo, JavaPlugin plugin) {
        /**
        * Creates a version checker that checks for updates on GitHub.
        * <p>
        * This class listens for player join events and checks for plugin updates.
        *
        * @param owner The GitHub username or organization that owns the repository.
        * @param repo The name of the GitHub repository.
        * @param plugin The instance of the plugin using this version checker.
        */
        this.owner = owner;
        this.repo = repo;
        this.plugin = plugin;
        this.updateMessage = Component.text("A new version of the plugin is available: ", NamedTextColor.WHITE);
        this.latestVersion = getLatestVersion();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if(this.latestVersion == null) {
            plugin.getLogger().warning("Could not fetch the latest version information.");
            return;
        }

        Player player = event.getPlayer();
        if(isNewerVersion(this.latestVersion, plugin.getPluginMeta().getVersion())){
            player.sendMessage(this.updateMessage.append(Component.text(this.latestVersion, NamedTextColor.GOLD)));
        }
    }

    public boolean checkLatestVersion() {
        String latestVersion = getLatestVersion();
        if (latestVersion == null) {
            plugin.getLogger().warning("Could not fetch the latest version information.");
            return false;
        }

        String currentVersion = plugin.getPluginMeta().getVersion();
        if (isNewerVersion(latestVersion, currentVersion)) {
            plugin.getLogger().info("A new version of the plugin is available: " + latestVersion + " (current: " + currentVersion + ")");
            return true;
        } else {
            plugin.getLogger().info("You are using the latest version of the plugin: " + currentVersion);
            return false;
        }
    }   

    public String getLatestVersion() {
        try {
            // GitHub API endpoint for latest release
            String apiUrl = "https://api.github.com/repos/" + this.owner + "/" + this.repo + "/releases/latest";
            HttpURLConnection connection = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Use JSONObject's constructor that accepts a String
            JSONParser parser = new JSONParser();
            JSONObject releaseData = (JSONObject) parser.parse(response.toString());
            plugin.getLogger()
                    .info("Latest version fetched: " + releaseData.get("tag_name") + "\n\n\n" + releaseData);

            // Extract version number from the tag
            this.latestVersion = (String) releaseData.get("tag_name");
            return this.latestVersion;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isNewerVersion(String latest, String current) {
        String[] latestParts = latest.replaceAll("[^0-9.]", "").split("\\.");
        String[] currentParts = current.replaceAll("[^0-9.]", "").split("\\.");

        int length = Math.max(latestParts.length, currentParts.length);
        for (int i = 0; i < length; i++) {
            int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
            int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;

            if (latestPart > currentPart) {
                return true;
            } else if (latestPart < currentPart) {
                return false;
            }
        }
        return false; // Same version
    }
}
