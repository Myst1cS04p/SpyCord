package com.myst1cs04p.spycord.common.config;

import java.util.List;

/**
* Abstraction over whatever config backend the platform uses (Bukkit's config.yml, etc.)
* Common logic reads through this interface and never touches platform config directly.
*/
public interface IPluginConfig {

String getString(String path, String fallback);

boolean getBoolean(String path, boolean fallback);

List<String> getStringList(String path);

/**
* Reload config from disk. Called when /spycord reload is executed.
*/
void reload();
}

