package com.myst1cs04p.spycord.common.events;

/**
* Fired when a player's game mode changes.
*/
public final class GameModeChangedEvent implements SpyEvent {

private final String playerName;
private final String oldMode;
private final String newMode;

public GameModeChangedEvent(String playerName, String oldMode, String newMode) {
this.playerName = playerName;
this.oldMode = oldMode;
this.newMode = newMode;
}

public String getPlayerName() { return playerName; }
public String getOldMode()    { return oldMode; }
public String getNewMode()    { return newMode; }
}

