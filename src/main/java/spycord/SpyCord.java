package spycord;


import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import spycord.commands.*;

public class SpyCord extends JavaPlugin {

    private static SpyCord instance;
    public boolean isEnabled = true;
    private static DiscordManager discordManager;
    private final CommandListener commandListener = new CommandListener(this);
    
    @Override
    public void onEnable() {
        saveDefaultConfig(); // Creates a config file if not alr present
        instance = this;
        discordManager = new DiscordManager(this, getConfig().getString("webhook-url"));
        isEnabled = getConfig().getBoolean("enabled", true);

        getServer().getPluginManager().registerEvents(commandListener, this);
        registerCommands();

        discordManager.sendToDiscord("@everyone ✅THE PLUGIN HAS BEEN ENABLED AND WILL LOG COMMANDS✅");
        discordManager.sendToDiscord("Command being logged: \n" + commandListener.GetSensitiveCommands().toString());

        splash();
    }

    private void registerCommands(){
        getCommand("version").setExecutor(new VersionCommand(this));
        getCommand("reload").setExecutor(new ReloadCommand(this));
        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("status").setExecutor(new StatusCommand(this));
        getCommand("toggle").setExecutor(new ToggleCommand(this));
    }
    
    public void Log(String message){
        getLogger().info("\u001B[35m\u001B[1m[SPYCORD] \u001B[0m" + message);
    }
    public void Log(String message, CommandSender sender){
        sender.sendMessage(Component.text("[SPYCORD] ", NamedTextColor.LIGHT_PURPLE)
            .append(Component.text(message, NamedTextColor.WHITE)));
    }
    public void Log(Component message, CommandSender sender){
        sender.sendMessage(Component.text("[SPYCORD] ", NamedTextColor.LIGHT_PURPLE)
            .append(message));
    }

    public void ReloadPlugin(){
        reloadConfig();
        isEnabled = getConfig().getBoolean("enabled", true);
        discordManager.sendToDiscord("@everyone 🛑🛑THE PLUGIN HAS BEEN DISABLED AND WILL NOT LOG COMAMNDS🛑🛑");
        discordManager.SetWebhookUrl(getConfig().getString("webhook-url"));
    }

    @Override
    public void onDisable() {
        getLogger().info("SpyCord Is Inactive");
    }
    
    public static SpyCord getInstance() {
        return instance;
    }
    
    public static DiscordManager getDiscord() {
        return discordManager;
    }

    private void splash(){
        getLogger().info("\n\u001B[34m" + 
                        "┏━┓┏━┓╻ ╻┏━╸┏━┓┏━┓╺┳┓\n"+
                        "┗━┓┣━┛┗┳┛┃  ┃ ┃┣┳┛ ┃┃\n"+
                        "┗━┛╹   ╹ ┗━╸┗━┛╹┗╸╺┻┛\n\u001B[0m");
        getLogger().info("Running Version " + getPluginMeta().getVersion() + "\n");
        getLogger().info("\n\u001B[35m"+
                        "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n"+
                        "@@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@\n"+
                        "@@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@\n"+    
                        "@@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@\n"+
                        "@@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@\n"+
                        "@@@@@         @@@@@@@@@         @@@@@\n"+
                        "@@@@@         @@@@@@@@@         @@@@@\n"+
                        "@@@@@         @@@@@@@@@         @@@@@\n"+
                        "@@@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@\n"+
                        "@@@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@\n"+
                        "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n\u001B[0m");
    }
}