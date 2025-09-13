package spycord;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpyCord extends JavaPlugin {

    private static SpyCord instance;
    private static DiscordManager dm;
    
    @Override
    public void onEnable() {

        saveDefaultConfig(); // Creates a config file if not alr present
        instance = this;
        dm = new DiscordManager(this);

        getServer().getPluginManager().registerEvents(new CommandListener(), this);

        Commands commandHandler = new Commands(this);
        this.getCommand("spycord").setExecutor(commandHandler);
        this.getCommand("spycord").setTabCompleter(commandHandler);
        
        getLogger().info("SpyCord Is Active");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SpyCord Is Inactive");
    }
    
    public static SpyCord getInstance() {
        return instance;
    }
    
    public static DiscordManager getDiscord() {
        return dm;
    }
}