package com.myst1cs04p.spycord.listeners;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;

import com.myst1cs04p.spycord.SpyCord;

public class Logger {

    private final SpyCord plugin;

    public Logger(SpyCord plugin){
        this.plugin = plugin;
    }


    public void log(String message){        
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (FileWriter writer = new FileWriter("command.log", true)) { // true = append mode
            writer.write("[" + time + "] " + message.toString() + System.lineSeparator());
        } catch (IOException e) {
            plugin.getLogger().severe("Error writing to log file: " + e.getMessage());
        }
        SpyCord.getDiscord().sendToDiscord(message);
    }
}
