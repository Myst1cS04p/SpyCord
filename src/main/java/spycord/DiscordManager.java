package spycord;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class DiscordManager {

    private final SpyCord plugin;
    private URL webhookUrl;


    public void SetWebhookUrl(String webhookUrl) {
        try {
            this.webhookUrl = URI.create(webhookUrl).toURL();
        } catch (Exception e) {
            plugin.Log("Invalid webhook URL: " + webhookUrl);
            plugin.getLogger().severe(e.toString());
        }
    }

    public DiscordManager(SpyCord plugin, String webhookUrl) {
        this.plugin = plugin;
        try {
            this.webhookUrl = URI.create(webhookUrl).toURL();
        } catch (Exception e) {
            plugin.Log("Invalid webhook URL: " + webhookUrl);
            plugin.getLogger().severe(e.toString());
        }
    }

    public void sendToDiscord(String message) {
        if(webhookUrl == null) {
            plugin.Log("Webhook URL is not set.");
            return;
        }

        try {            
            HttpURLConnection connection = (HttpURLConnection) webhookUrl.openConnection();

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
