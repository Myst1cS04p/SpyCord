package com.myst1cs04p.spycord.common.events;

/**
* Fired when an OP player joins or leaves the server.
*/
public final class PlayerConnectionEvent implements SpyEvent {

public enum Type { JOIN, QUIT }

private final String playerName;
private final Type type;

public PlayerConnectionEvent(String playerName, Type type) {
this.playerName = playerName;
this.type = type;
}

public String getPlayerName() { return playerName; }
public Type getType()         { return type; }
}

