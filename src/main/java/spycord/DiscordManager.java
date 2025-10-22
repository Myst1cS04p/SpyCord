package spycord;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class DiscordManager {

    private final SpyCord plugin;

    public DiscordManager(SpyCord plugin) {
        this.plugin = plugin;
    }

    public void sendToDiscord(String message) {
        String webhookUrl = plugin.getConfig().getString("webhook-url");
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            plugin.getLogger().warning("Webhook URL is not set in config.yml!");
            return;
        }

        try {
            URL url = URI.create(webhookUrl).toURL();
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonPayload = String.format("{\"content\": \"%s\"}", escapeJson(message));

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
                plugin.getLogger().warning("Failed to send webhook: HTTP " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
