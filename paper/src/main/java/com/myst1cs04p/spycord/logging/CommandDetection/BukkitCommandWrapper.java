package com.myst1cs04p.spycord.logging.CommandDetection;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import com.myst1cs04p.spycord.SpyCord;

public class BukkitCommandWrapper extends Command {
    private final Command delegate;
    BukkitCommandWrapper(Command delegate) {
        super(delegate.getName());
        this.delegate = delegate;
        try {
            this.setAliases(delegate.getAliases());
            this.setDescription(delegate.getDescription());
            this.setUsage(delegate.getUsage());
            this.setPermission(delegate.getPermission());
        } catch (Exception e) {
            SpyCord.getInstance().getLogger().log(Level.SEVERE, "[SPYCORD]: " + e.getMessage());;
        }
    }
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if(!SpyCord.getInstance().getIsEnabled("command-logger") || !SpyCord.getInstance().getIsEnabled()) {
            return delegate.execute(sender, commandLabel, args);
        }

        SpyCord.getCommandLogger().log(String.format("[%s]: %s", sender.getName(), commandLabel));
        return delegate.execute(sender, commandLabel, args);
    }

    @Override
    public @NotNull List<String> tabComplete(
            @NotNull CommandSender sender,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        return delegate.tabComplete(sender, alias, args);
    }

}
