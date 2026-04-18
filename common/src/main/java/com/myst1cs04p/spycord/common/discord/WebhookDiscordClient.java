package com.myst1cs04p.spycord.common.discord;

import com.myst1cs04p.spycord.common.logging.DebugLogger;
import com.myst1cs04p.spycord.common.stats.ActivityTracker;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Sends messages to a Discord webhook asynchronously via {@link CompletableFuture}.
 *
 * <p>Records every HTTP response into the supplied {@link ActivityTracker} so that
 * bStats custom charts and the activity digest have accurate delivery data.
 * Verbose lifecycle events are gated behind {@link DebugLogger}.
 */
public class WebhookDiscordClient implements IDiscordClient {

    private final Logger logger;
    private final ActivityTracker tracker;
    private final DebugLogger debug;
    private URL webhookUrl;

    public WebhookDiscordClient(Logger logger, String webhookUrl,
                                ActivityTracker tracker, DebugLogger debug) {
        this.logger  = logger;
        this.tracker = tracker;
        this.debug   = debug;
        setWebhookUrl(webhookUrl);
    }

    @Override
    public void setWebhookUrl(String url) {
        try {
            this.webhookUrl = URI.create(url).toURL();
            debug.log("Webhook URL updated: " + url);
        } catch (Exception e) {
            logger.warning("[SpyCord] Invalid webhook URL: " + url + " - " + e.getMessage());
            this.webhookUrl = null;
        }
    }

    @Override
    public void send(String message) {
        if (webhookUrl == null) {
            logger.warning("[SpyCord] Cannot send to Discord: webhook URL is not configured.");
            tracker.recordWebhookFailure();
            return;
        }

        URL target = webhookUrl;
        debug.log("Dispatching webhook message (async): " + message);

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
                if (code == 204) {
                    debug.log("Webhook delivery successful (HTTP 204).");
                    tracker.recordWebhookSuccess();
                } else {
                    logger.warning("[SpyCord] Discord webhook returned unexpected HTTP " + code);
                    tracker.recordWebhookFailure();
                }

            } catch (Exception e) {
                logger.warning("[SpyCord] Failed to send Discord message: " + e.getMessage());
                tracker.recordWebhookFailure();
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