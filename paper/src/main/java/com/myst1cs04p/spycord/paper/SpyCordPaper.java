package com.myst1cs04p.spycord.paper;

import com.myst1cs04p.spycord.bukkit.SpyCordBukkit;
import com.myst1cs04p.spycord.paper.commands.*;
import com.myst1cs04p.spycord.common.updater.VersionNotifier;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.util.logging.Level;

/**
 * Paper entry point. Extends SpyCordBukkit so all Bukkit-compatible listeners
 * and service wiring are inherited. Paper-exclusive features (Brigadier commands,
 * async scheduler, bStats) are layered on top here.
 */
public final class SpyCordPaper extends SpyCordBukkit {

    @Override
    protected void startVersionChecker() {
        String currentVersion = getPluginMeta().getVersion();

        VersionNotifier notifier = new VersionNotifier(getLogger(), "Myst1cS04p", "SpyCord", currentVersion)
                .onUpdate(version -> {
                    String message = """
                            # New SpyCord Version Available!
                            ## Version %s
                            - **Modrinth**: https://modrinth.com/plugin/spycord
                            - **GitHub**: https://github.com/Myst1cS04p/SpyCord/releases
                            - **SpigotMC**: https://www.spigotmc.org/resources/spycord.129615/
                            - **Hangar**: https://hangar.papermc.io/Myst1cS04p/Spycord
                            """.formatted(version);

                    getLogger().log(Level.INFO, "A new version of SpyCord is available: {0}", version);
                    services.getDiscordClient().send(message);
                });

        // Paper's scheduler — runs every 12 hours (12 * 60 * 60 * 20 ticks)
        getServer().getScheduler().runTaskTimerAsynchronously(this,
                notifier::checkOnce, 0L, 12 * 60 * 60 * 20L);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();

        // Override Bukkit's legacy command registration with Brigadier
        registerCommands();
    }

    private void registerCommands() {
        LiteralCommandNode<CommandSourceStack> root = Commands.literal("spycord")
                .then(ReloadCommand.create(this, services))
                .then(ReportCommand.create(this))
                .then(StatusCommand.create(services))
                .then(ToggleCommand.create(this, services))
                .then(VersionCommand.create(this))
                .then(HelpCommand.create(this))
                .build();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, cmd ->
                cmd.registrar().register(root));
    }

}

