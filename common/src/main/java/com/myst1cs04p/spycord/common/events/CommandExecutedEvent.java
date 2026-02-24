package com.myst1cs04p.spycord.common.events;

/**
 * Fired when a tracked command is executed by a player or console.
 */
public final class CommandExecutedEvent implements SpyEvent {

    private final String senderName;
    private final String command;
    private final boolean isConsole;

    public CommandExecutedEvent(String senderName, String command, boolean isConsole) {
        this.senderName = senderName;
        this.command = command;
        this.isConsole = isConsole;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getCommand() {
        return command;
    }

    public boolean isConsole() {
        return isConsole;
    }
}

