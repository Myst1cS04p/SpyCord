package spycord;


import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import spycord.commands.*;
import updater.VersionChecker;

public class SpyCord extends JavaPlugin {
    private static SpyCord instance;
    public boolean isEnabled = true;
    private static DiscordManager discordManager;
    private final CommandListener commandListener = new CommandListener(this);
    private final VersionChecker versionChecker = new VersionChecker("Myst1cS04p", "SpyCord", this);
    
    @Override
    public void onEnable() {
        saveDefaultConfig(); // Creates a config file if not alr present
        instance = this;
        discordManager = new DiscordManager(this, getConfig().getString("webhook-url"));
        isEnabled = getConfig().getBoolean("enabled", true);

        getServer().getPluginManager().registerEvents(commandListener, this);
        registerCommands();

        new BukkitRunnable() {
            @Override
            public void run() {
                versionChecker.checkLatestVersion();
                if(versionChecker.latestVersion != null && 
                   versionChecker.isNewerVersion(versionChecker.latestVersion, getPluginMeta().getVersion())) {
                    discordManager.sendToDiscord("# Version " + versionChecker.latestVersion + " is availible for SpyCord\n"+
                    "## You can get it from: " +
                    "- **Modrinth**: https://modrinth.com/plugin/spycord" + 
                    "- **Github**: https://github.com/Myst1cS04p/SpyCord/releases" +
                    "- **SpigotMC**: https://www.spigotmc.org/resources/spycord.129615/"+
                    "- **PaperMC Hangar**: https://hangar.papermc.io/Myst1cS04p/Spycord");
                }
            }
        }.runTaskTimer(this, 0, 12 * 60 * 60 * 20); // Check every 12 hours

        discordManager.sendToDiscord("@everyone **✅THE PLUGIN HAS BEEN ENABLED AND WILL LOG COMMANDS✅**");

        List<String> commandList = commandListener.GetSensitiveCommands();
        String formattedList = commandList.stream().map(cmd -> "*" + cmd.trim() + "*").collect(Collectors.joining("\n"));

        discordManager.sendToDiscord("## Command being logged: \n" + formattedList);

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
        if(!isEnabled){
            discordManager.sendToDiscord("@everyone **🛑🛑THE PLUGIN HAS BEEN DISABLED AND WILL NOT LOG COMMANDS🛑🛑**");
        }
        discordManager.SetWebhookUrl(getConfig().getString("webhook-url"));
    }

    @Override
    public void onDisable() {
        discordManager.sendToDiscord("@everyone **🛑🛑THE PLUGIN HAS BEEN DISABLED AND WILL NOT LOG COMMANDS🛑🛑**\n-# This could be due to the server closing.");
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