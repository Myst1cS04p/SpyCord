package com.myst1cs04p.spycord.bukkit.config;

import com.myst1cs04p.spycord.common.config.IPluginConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Wraps Bukkit's FileConfiguration behind IPluginConfig.
 */
public class BukkitPluginConfig implements IPluginConfig {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public BukkitPluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public String getString(String path, String fallback) {
        return config.getString(path, fallback);
    }

    @Override
    public boolean getBoolean(String path, boolean fallback) {
        return config.getBoolean(path, fallback);
    }

    @Override
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Override
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    @Override
    public void setBoolean(String path, boolean value) {
        config.set(path, value);
    }
}

