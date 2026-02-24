package com.myst1cs04p.spycord.common.discord;

/**
* Abstraction over the Discord webhook transport.
* Implementations send messages asynchronously.
*/
public interface IDiscordClient {

/**
* Send a message to Discord. Fire-and-forget; never blocks the calling thread.
*
* @param message Raw markdown message content.
*/
void send(String message);

/**
* Update the webhook URL at runtime (e.g. after a config reload).
*
* @param url New webhook URL string.
*/
void setWebhookUrl(String url);
}

