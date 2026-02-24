# SpyCord — Developer Documentation

> For server admins looking for setup instructions, see the [README](./README.md).
> This document is for contributors and maintainers navigating the codebase.

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Project Structure](#2-project-structure)
3. [Build & Run](#3-build--run)
4. [Config Reference](#4-config-reference)
5. [How to Add a New Event Type](#5-how-to-add-a-new-event-type)
6. [How to Add a New Command](#6-how-to-add-a-new-command)
7. [How to Add a New Log Sink](#7-how-to-add-a-new-log-sink)

---

## 1. Architecture Overview

SpyCord is split into three Gradle submodules. The dependency flow is strictly one-directional — lower modules never import from higher ones.

```
┌─────────────────────────────────────────────────────┐
│                      paper/                         │
│  SpyCordPaper, Brigadier commands, VersionNotifier  │
│  extends bukkit/, Paper 1.21+ only                  │
└───────────────────────┬─────────────────────────────┘
                        │ extends + depends on
┌───────────────────────▼─────────────────────────────┐
│                      bukkit/                        │
│  SpyCordBukkit, legacy CommandExecutor, listeners   │
│  Spigot/Bukkit 1.13+                                │
└───────────────────────┬─────────────────────────────┘
                        │ depends on
┌───────────────────────▼─────────────────────────────┐
│                      common/                        │
│  Interfaces, domain events, EventPipeline,          │
│  log sinks, Discord client, VersionChecker          │
│  Pure Java — zero Bukkit/Paper imports              │
└─────────────────────────────────────────────────────┘
```

### The data flow for a logged event

```
  Bukkit/Paper fires a platform event
            │
            ▼
  Platform Listener (bukkit/ or paper/)
  Translates it into a SpyEvent — no logic here,
  just wrapping fields into the domain type
            │
            ▼
  EventPipeline.process(SpyEvent)          ← lives in common/
  Checks: is the plugin enabled?
          is this module enabled?
          is this command on the watch list?
            │
            ▼
  CompositeLogSink.write(LogEntry)         ← lives in common/
  Fans out to all registered sinks
            │
     ┌──────┴──────┐
     ▼             ▼
FileLogSink   DiscordLogSink
(command.log) (async HTTP POST
               to webhook URL)
```

### Key design principles

**No statics.** There is no `SpyCord.getInstance()` or `SpyCord.getDiscord()`. All services are constructed once in `onEnable()` via `ServiceRegistry` and passed down by reference. If you find yourself reaching for a static, that's a signal to inject instead.

**Listeners are dumb.** A listener's only job is to translate a Bukkit/Paper event into a `SpyEvent` and call `pipeline.process()`. No filtering, no config reads, no logging calls. All of that lives in `EventPipeline`.

**`common/` is the source of truth.** Business logic — what gets logged, when, and how — lives in the common module. The platform modules are just wiring.

---

## 2. Project Structure

```
SpyCord/
├── settings.gradle.kts              # Declares all three submodules
├── build.gradle.kts                 # Root build config (Java 21, shared settings)
│
├── common/
│   ├── build.gradle.kts
│   └── src/main/java/com/myst1cs04p/spycord/common/
│       ├── ServiceRegistry.java         # Wires all services, constructed in onEnable()
│       │
│       ├── config/
│       │   └── IPluginConfig.java       # getString, getBoolean, getStringList, reload()
│       │
│       ├── discord/
│       │   ├── IDiscordClient.java      # send(String), setWebhookUrl(String)
│       │   └── WebhookDiscordClient.java # Async HTTP POST implementation
│       │
│       ├── logging/
│       │   ├── ILogSink.java            # write(LogEntry)
│       │   ├── LogEntry.java            # Immutable: message + formatted timestamp
│       │   ├── CompositeLogSink.java    # Fans one entry out to N sinks
│       │   ├── FileLogSink.java         # Appends to command.log
│       │   └── DiscordLogSink.java      # Delegates to IDiscordClient
│       │
│       ├── events/
│       │   ├── SpyEvent.java            # Marker interface for all domain events
│       │   ├── CommandExecutedEvent.java
│       │   ├── GameModeChangedEvent.java
│       │   ├── PlayerConnectionEvent.java
│       │   └── EventPipeline.java       # THE place where events become log entries
│       │
│       └── updater/
│           ├── VersionChecker.java      # Async GitHub release fetch + semver compare
│           └── VersionNotifier.java     # Exposes checkOnce(), platform schedules it
│
├── bukkit/
│   ├── build.gradle.kts
│   └── src/main/java/com/myst1cs04p/spycord/bukkit/
│       ├── SpyCordBukkit.java           # JavaPlugin entry point for Bukkit/Spigot
│       │
│       ├── config/
│       │   └── BukkitPluginConfig.java  # IPluginConfig backed by FileConfiguration
│       │
│       ├── listeners/
│       │   ├── BukkitCommandListener.java
│       │   ├── BukkitGameModeListener.java
│       │   └── BukkitConnectionListener.java
│       │
│       └── commands/
│           └── BukkitSpyCordCommand.java # Legacy CommandExecutor, all subcommands
│
└── paper/
    ├── build.gradle.kts
    └── src/main/java/com/myst1cs04p/spycord/paper/
        ├── SpyCordPaper.java            # Extends SpyCordBukkit, adds Paper features
        └── commands/
            ├── HelpCommand.java
            ├── ReloadCommand.java
            ├── ReportCommand.java
            ├── StatusCommand.java
            ├── ToggleCommand.java
            └── VersionCommand.java
```

### Where to look for what

| You want to… | Look in… |
|---|---|
| Change what gets logged or when | `common/events/EventPipeline.java` |
| Add a new type of loggable event | `common/events/` + the relevant platform listener |
| Add a new place logs get sent | `common/logging/` + wire in `ServiceRegistry` |
| Change Discord message formatting | `common/logging/DiscordLogSink.java` |
| Change file log formatting | `common/logging/FileLogSink.java` or `LogEntry.java` |
| Add or change a command (Paper) | `paper/commands/` |
| Add or change a command (Bukkit) | `bukkit/commands/BukkitSpyCordCommand.java` |
| Change plugin startup/shutdown behaviour | `bukkit/SpyCordBukkit.java` and/or `paper/SpyCordPaper.java` |
| Change version check behaviour | `common/updater/` |

---

## 3. Build & Run

### Prerequisites

- JDK 21 or later
- Git

### Clone and build

```bash
git clone https://github.com/Myst1cS04p/SpyCord.git
cd SpyCord
./gradlew build
```

Built JARs land in:

```
bukkit/build/libs/SpyCord-bukkit-<version>.jar
paper/build/libs/SpyCord-paper-<version>.jar
```

Use the `bukkit` JAR on Spigot/Bukkit servers and the `paper` JAR on Paper servers. Do not use both on the same server.

### Build a single module

```bash
./gradlew :paper:build
./gradlew :bukkit:build
./gradlew :common:build
```

### Run checks without building JARs

```bash
./gradlew compileJava        # compile all modules
./gradlew :common:compileJava  # compile just common
```

### Dropping the JAR into a test server

Copy the appropriate JAR into your test server's `plugins/` folder and restart. On first run, SpyCord writes a default `config.yml` to `plugins/SpyCord/`. Edit it and run `/spycord reload` — no restart needed for config changes.

---

## 4. Config Reference

SpyCord's `config.yml` lives in `plugins/SpyCord/config.yml` after first run.

```yaml
# Master switch. Set to false to disable all logging without unloading the plugin.
enabled: true

# Discord webhook URL. Required for Discord logging to function.
# Leave blank to disable Discord output (file logging still works).
webhook-url: "https://discord.com/api/webhooks/YOUR/WEBHOOK"

# Per-module toggles. Each module respects the master 'enabled' flag above.
modules:
  command-logger: true   # Log sensitive commands to file + Discord
  gamemode-logger: true  # Log OP gamemode changes
  join-logger: true      # Log OP player joins and quits

# Commands to watch. Only commands whose root matches an entry here are logged.
# Case-insensitive. Do not include the leading slash.
command-logging:
  sensitive-commands:
    - op
    - deop
    - ban
    - pardon
    - gamemode
    - give
    - stop
```

### How config is read at runtime

Config reads go through `IPluginConfig`, which is implemented by `BukkitPluginConfig`. That class wraps Bukkit's `FileConfiguration`. When `/spycord reload` is called, `IPluginConfig.reload()` is called — this re-reads the file from disk and the next `EventPipeline.process()` call picks up the new values immediately. There is no caching layer between `IPluginConfig` and the pipeline.

---

## 5. How to Add a New Event Type

This is the most common extension point. The steps are the same whether you're on Bukkit or Paper — only the listener implementation differs.

**Example:** adding an `ItemGivenEvent` that fires when an OP uses `/give`.

### Step 1 — Define the domain event in `common/`

```java
// common/src/main/java/com/myst1cs04p/spycord/common/events/ItemGivenEvent.java
package com.myst1cs04p.spycord.common.events;

public final class ItemGivenEvent implements SpyEvent {

    private final String giverName;
    private final String targetName;
    private final String item;
    private final int amount;

    public ItemGivenEvent(String giverName, String targetName, String item, int amount) {
        this.giverName = giverName;
        this.targetName = targetName;
        this.item = item;
        this.amount = amount;
    }

    public String getGiverName()  { return giverName; }
    public String getTargetName() { return targetName; }
    public String getItem()       { return item; }
    public int getAmount()        { return amount; }
}
```

### Step 2 — Handle it in `EventPipeline`

Open `common/events/EventPipeline.java` and add a branch in `process()`:

```java
public void process(SpyEvent event) {
    if (!config.getBoolean("enabled", true)) return;

    if (event instanceof CommandExecutedEvent e)   { handleCommand(e); }
    else if (event instanceof GameModeChangedEvent e) { handleGameMode(e); }
    else if (event instanceof PlayerConnectionEvent e) { handleConnection(e); }
    else if (event instanceof ItemGivenEvent e)    { handleItemGiven(e); } // add this
}

private void handleItemGiven(ItemGivenEvent event) {
    // respect per-module toggle — add a key to config.yml if needed
    if (!config.getBoolean("modules.give-logger", true)) return;

    sink.write(new LogEntry(String.format(
            "[GIVE] %s gave %dx %s to %s",
            event.getGiverName(),
            event.getAmount(),
            event.getItem(),
            event.getTargetName()
    )));
}
```

### Step 3 — Add the platform listener

**Bukkit** (`bukkit/listeners/`):

```java
public class BukkitItemGiveListener implements Listener {

    private final EventPipeline pipeline;

    public BukkitItemGiveListener(EventPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @EventHandler
    public void onGive(/* appropriate Bukkit event */) {
        pipeline.process(new ItemGivenEvent(
                /* map fields from the Bukkit event */
        ));
    }
}
```

**Paper** — if the Bukkit event covers it, no extra listener is needed. Paper inherits all Bukkit listeners from `SpyCordBukkit`. Only add a Paper-specific listener if you need a Paper-exclusive event.

### Step 4 — Register the listener

In `SpyCordBukkit.registerListeners()`:

```java
protected void registerListeners() {
    getServer().getPluginManager().registerEvents(
            new BukkitCommandListener(services.getEventPipeline()), this);
    getServer().getPluginManager().registerEvents(
            new BukkitGameModeListener(services.getEventPipeline()), this);
    getServer().getPluginManager().registerEvents(
            new BukkitConnectionListener(services.getEventPipeline()), this);
    getServer().getPluginManager().registerEvents(       // add this
            new BukkitItemGiveListener(services.getEventPipeline()), this);
}
```

### Step 5 — Add the config key (optional)

If your new event has a per-module toggle, add it to the default `config.yml` in `bukkit/src/main/resources/`:

```yaml
modules:
  command-logger: true
  gamemode-logger: true
  join-logger: true
  give-logger: true      # add this
```

That's it. Five steps, and common/ contains all the logic. The listener is just a translator.

---

## 6. How to Add a New Command

Commands exist in two places — `paper/commands/` for Brigadier (Paper) and `bukkit/commands/BukkitSpyCordCommand.java` for the legacy CommandExecutor (Bukkit). You need to update both.

**Example:** adding a `/spycord info` subcommand.

### Paper — add a new command class

Create `paper/src/main/java/com/myst1cs04p/spycord/paper/commands/InfoCommand.java`:

```java
package com.myst1cs04p.spycord.paper.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.spycord.common.ServiceRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class InfoCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create(ServiceRegistry services) {
        return Commands.literal("info").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();

            boolean enabled = services.getConfig().getBoolean("enabled", true);
            String webhook = services.getConfig().getString("webhook-url", "(not set)");

            sender.sendMessage(Component.text("[SpyCord] Status: ", NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text(enabled ? "enabled" : "disabled",
                            enabled ? NamedTextColor.GREEN : NamedTextColor.RED))
                    .append(Component.newline())
                    .append(Component.text("[SpyCord] Webhook: " + webhook, NamedTextColor.GRAY)));
            return 1;
        });
    }
}
```

Then register it in `SpyCordPaper.registerBrigadierCommands()`:

```java
LiteralCommandNode<CommandSourceStack> root = Commands.literal("spycord")
        .then(ReloadCommand.create(this, services))
        .then(ReportCommand.create(this))
        .then(StatusCommand.create(services))
        .then(ToggleCommand.create(this, services))
        .then(VersionCommand.create(this))
        .then(HelpCommand.create(this))
        .then(InfoCommand.create(services))   // add this
        .build();
```

### Bukkit — add a case to `BukkitSpyCordCommand`

Open `bukkit/commands/BukkitSpyCordCommand.java` and add a case in the switch:

```java
case "info" -> {
    boolean enabled = config.getBoolean("enabled", true);
    String webhook = config.getString("webhook-url", "(not set)");
    sender.sendMessage("[SpyCord] Status: " + (enabled ? "enabled" : "disabled"));
    sender.sendMessage("[SpyCord] Webhook: " + webhook);
}
```

Also add it to the tab completer:

```java
if (args.length == 1) {
    return List.of("help", "info", "reload", "status", "toggle", "version", "report");
}
```

### Permission (optional)

If the command needs a permission node, add it to both `plugin.yml` (bukkit) and `paper-plugin.yml` (paper), then check `sender.hasPermission("spycord.info")` in both implementations.

### Update the help text

Edit `HelpCommand.java` (Paper) and the `help` case in `BukkitSpyCordCommand.java` to include the new subcommand.

---

## 7. How to Add a New Log Sink

A log sink is anything that receives a `LogEntry` and does something with it — write to a file, POST to a webhook, insert into a database, etc.

**Example:** adding a `ConsoleLogSink` that writes formatted entries to the server console.

### Step 1 — Implement `ILogSink` in `common/`

```java
// common/src/main/java/com/myst1cs04p/spycord/common/logging/ConsoleLogSink.java
package com.myst1cs04p.spycord.common.logging;

import java.util.logging.Logger;

public class ConsoleLogSink implements ILogSink {

    private final Logger logger;

    public ConsoleLogSink(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void write(LogEntry entry) {
        logger.info("[SpyCord Log] " + entry.getMessage());
    }
}
```

That's the entire implementation. `ILogSink` has one method.

### Step 2 — Register it in `ServiceRegistry`

Open `common/ServiceRegistry.java` and add it to the `CompositeLogSink` constructor:

```java
public ServiceRegistry(IPluginConfig config, IDiscordClient discordClient, Logger logger) {
    this.config = config;
    this.discordClient = discordClient;

    FileLogSink fileLogSink     = new FileLogSink("command.log", logger);
    DiscordLogSink discordLogSink = new DiscordLogSink(discordClient);
    ConsoleLogSink consoleLogSink = new ConsoleLogSink(logger);   // add this

    this.logSink = new CompositeLogSink(fileLogSink, discordLogSink, consoleLogSink);

    this.eventPipeline = new EventPipeline(config, logSink);
}
```

`CompositeLogSink` takes a varargs of `ILogSink`, so no other changes are needed. Every event that passes through `EventPipeline` will now also go to the console.

### Making a sink optional via config

If you want the sink to be toggleable, wrap the write call in a config check inside the sink itself:

```java
@Override
public void write(LogEntry entry) {
    if (!config.getBoolean("modules.console-logger", false)) return;
    logger.info("[SpyCord Log] " + entry.getMessage());
}
```

Pass `IPluginConfig` into the constructor alongside whatever else the sink needs. This keeps the toggle logic inside the sink rather than scattering it across `ServiceRegistry` or `EventPipeline`.
