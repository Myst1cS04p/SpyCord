package com.myst1cs04p.spycord.commandLogging;

import com.myst1cs04p.spycord.SpyCord;

public class Logger {
    private final SpyCord plugin;

    public Logger(SpyCord plugin){
        this.plugin = plugin;
    }

    public void Log(String message){
        plugin.getLogger().info(message);
    }
    
}
