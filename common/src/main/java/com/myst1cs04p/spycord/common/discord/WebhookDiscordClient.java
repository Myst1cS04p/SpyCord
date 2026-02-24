package com.myst1cs04p.spycord.common.discord;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
* Sends messages to a Discord webhook asynchronously.
*/
public class WebhookDiscordClient implements IDiscordClient {

private final Logger logger;
private URL webhookUrl;

public WebhookDiscordClient(Logger logger, String webhookUrl) {
this.logger = logger;
setWebhookUrl(webhookUrl);
}

@Override
public void setWebhookUrl(String url) {
try {
this.webhookUrl = URI.create(url).toURL();
} catch (Exception e) {
logger.warning("[SpyCord] Invalid webhook URL: " + url + " — " + e.getMessage());
this.webhookUrl = null;
}
}

@Override
public void send(String message) {
if (webhookUrl == null) {
logger.warning("[SpyCord] Cannot send to Discord: webhook URL is not configured.");
return;
}

URL target = webhookUrl;

CompletableFuture.runAsync(() -> {
try {
HttpURLConnection connection = (HttpURLConnection) target.openConnection();
connection.setRequestMethod("POST");
connection.setDoOutput(true);
connection.setRequestProperty("Content-Type", "application/json");

String payload = String.format("{\"content\": \"%s\"}", escapeJson(message));

try (OutputStream os = connection.getOutputStream()) {
os.write(payload.getBytes());
os.flush();
}

int code = connection.getResponseCode();
if (code != 204) {
logger.warning("[SpyCord] Discord webhook returned unexpected HTTP " + code);
}

} catch (Exception e) {
logger.warning("[SpyCord] Failed to send Discord message: " + e.getMessage());
}
});
}

private String escapeJson(String text) {
return text
.replace("\\", "\\\\")
.replace("\"", "\\\"")
.replace("\n", "\\n")
.replace("\r", "");
}
}

