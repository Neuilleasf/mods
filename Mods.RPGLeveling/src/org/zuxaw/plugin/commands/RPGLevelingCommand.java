/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.server.core.NameMatching
 *  com.hypixel.hytale.server.core.command.system.AbstractCommand
 *  com.hypixel.hytale.server.core.command.system.CommandContext
 *  com.hypixel.hytale.server.core.command.system.CommandSender
 *  com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
 *  com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
 *  com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType
 *  com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
 *  com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage
 *  com.hypixel.hytale.server.core.permissions.PermissionsModule
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.commands;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.gui.StatsGUIPage;
import org.zuxaw.plugin.services.LeaderboardService;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.TinyMsg;

public class RPGLevelingCommand
extends AbstractCommandCollection {
    private final LevelingService levelingService;
    private final StatsService statsService;
    private final LevelingConfig config;
    private final MessageService messageService;
    private final LeaderboardService leaderboardService;

    public RPGLevelingCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService, LeaderboardService leaderboardService) {
        super("lvl", "RPG Leveling plugin commands.");
        this.levelingService = levelingService;
        this.statsService = statsService;
        this.config = config;
        this.messageService = messageService;
        this.leaderboardService = leaderboardService;
        this.addSubCommand((AbstractCommand)new GUICommand(levelingService, statsService, config, messageService));
        this.addSubCommand((AbstractCommand)new InfoCommand(levelingService, statsService, config, messageService));
        this.addSubCommand((AbstractCommand)new SetLevelCommand(levelingService, statsService, config, messageService, leaderboardService));
        this.addSubCommand((AbstractCommand)new SetPointsCommand(levelingService, statsService, config, messageService));
        this.addSubCommand((AbstractCommand)new ResetStatsCommand(levelingService, statsService, config, messageService));
        this.addSubCommand((AbstractCommand)new AddXPCommand(levelingService, statsService, config, messageService));
    }

    protected boolean canGeneratePermission() {
        return false;
    }

    private static class GUICommand
    extends AbstractAsyncCommand {
        private final LevelingService levelingService;
        private final StatsService statsService;
        private final LevelingConfig config;
        private final MessageService messageService;

        public GUICommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService) {
            super("gui", "Open the stats management GUI.");
            this.levelingService = levelingService;
            this.statsService = statsService;
            this.config = config;
            this.messageService = messageService;
        }

        protected boolean canGeneratePermission() {
            return false;
        }

        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
            CommandSender sender = ctx.sender();
            if (sender instanceof Player) {
                Player player = (Player)sender;
                Ref ref = player.getReference();
                if (ref != null && ref.isValid()) {
                    Store store = ref.getStore();
                    World world = ((EntityStore)store.getExternalData()).getWorld();
                    return CompletableFuture.runAsync(() -> {
                        try {
                            PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
                            if (playerRef != null) {
                                player.getPageManager().openCustomPage(ref, store, (CustomUIPage)new StatsGUIPage(playerRef, this.levelingService, this.statsService, this.config, this.messageService));
                            }
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }, (Executor)world);
                }
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("gui", "error_entity_data", new Object[0]) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("gui", "error_players_only", new Object[0]) + "</color>"));
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class InfoCommand
    extends AbstractAsyncCommand {
        private final LevelingService levelingService;
        private final StatsService statsService;
        private final LevelingConfig config;
        private final MessageService messageService;

        public InfoCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService) {
            super("info", "Show all stats information and available commands.");
            this.levelingService = levelingService;
            this.statsService = statsService;
            this.config = config;
            this.messageService = messageService;
        }

        protected boolean canGeneratePermission() {
            return false;
        }

        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
            if (!ctx.isPlayer()) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("info", "error_players_only", new Object[0]) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            UUID playerUuid = ctx.sender().getUuid();
            PlayerRef playerRef = Universe.get().getPlayer(playerUuid);
            if (playerRef == null) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("info", "error_no_player_data", new Object[0]) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            return this.showInfo(ctx, playerRef);
        }

        private CompletableFuture<Void> showInfo(@Nonnull CommandContext ctx, @Nonnull PlayerRef playerRef) {
            this.sendInfo(ctx);
            return CompletableFuture.completedFuture(null);
        }

        private void sendInfo(@Nonnull CommandContext ctx) {
            boolean isAdmin;
            ctx.sendMessage(TinyMsg.parse("<color:gold><b>" + this.messageService.getCommand("info", "header", new Object[0]) + "</b></color>"));
            ctx.sendMessage(TinyMsg.parse("<color:yellow><b>" + this.messageService.getCommand("info", "available_stats", new Object[0]) + "</b></color>"));
            ArrayList<String> localizedStats = new ArrayList<String>();
            for (String stat : StatsService.VALID_STATS) {
                String statKey = stat.replaceAll("([A-Z])", "_$1").toLowerCase().substring(1);
                localizedStats.add(this.messageService.getStatName(statKey));
            }
            String statList = String.join((CharSequence)", ", localizedStats);
            ctx.sendMessage(TinyMsg.parse("<color:aqua>" + this.messageService.getCommand("info", "stats_list", statList, this.config.getStatValuePerPoint()) + "</color>"));
            ctx.sendMessage(TinyMsg.parse("<color:yellow><b>" + this.messageService.getCommand("info", "available_commands", new Object[0]) + "</b></color>"));
            ctx.sendMessage(TinyMsg.parse("  <color:green>/lvl gui - " + this.messageService.getCommand("info", "cmd_gui", new Object[0]) + "</color>"));
            ctx.sendMessage(TinyMsg.parse("  <color:green>/lvl info - " + this.messageService.getCommand("info", "cmd_info", new Object[0]) + "</color>"));
            UUID senderUuid = ctx.sender().getUuid();
            boolean bl = isAdmin = ctx.sender().hasPermission("hytale.command.admin") || PermissionsModule.get().getGroupsForUser(senderUuid).contains("OP");
            if (isAdmin) {
                ctx.sendMessage(TinyMsg.parse("<color:yellow><b>" + this.messageService.getCommand("info", "admin_commands", new Object[0]) + "</b></color>"));
                ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl setlevel <player> <level> - " + this.messageService.getCommand("info", "cmd_setlevel", new Object[0]) + "</color>"));
                ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl setpoints <player> <points> - " + this.messageService.getCommand("info", "cmd_setpoints", new Object[0]) + "</color>"));
                ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl addxp <player> <xp> - " + this.messageService.getCommand("info", "cmd_addxp", new Object[0]) + "</color>"));
                ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl resetstats <player> - " + this.messageService.getCommand("info", "cmd_resetstats", new Object[0]) + "</color>"));
            } else {
                ctx.sendMessage(TinyMsg.parse("<color:gray>" + this.messageService.getCommand("info", "admin_available", new Object[0]) + "</color>"));
            }
        }
    }

    private static class SetLevelCommand
    extends AbstractAsyncCommand {
        private final LevelingService levelingService;
        private final StatsService statsService;
        private final LevelingConfig config;
        private final MessageService messageService;
        private final LeaderboardService leaderboardService;
        private final RequiredArg<String> playerArg;
        private final RequiredArg<Integer> levelArg;

        public SetLevelCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService, LeaderboardService leaderboardService) {
            super("setlevel", "Set a player's level (admin only).");
            this.setPermissionGroups(new String[]{"OP"});
            this.levelingService = levelingService;
            this.statsService = statsService;
            this.config = config;
            this.messageService = messageService;
            this.leaderboardService = leaderboardService;
            this.playerArg = this.withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
            this.levelArg = this.withRequiredArg("level", "Level to set (1-" + config.getMaxLevel() + ")", (ArgumentType)ArgTypes.INTEGER);
        }

        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
            String playerName = (String)this.playerArg.get(ctx);
            Integer level = (Integer)this.levelArg.get(ctx);
            PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
            if (targetPlayer == null) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("setlevel", "player_not_found", playerName) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            if (level < 1 || level > this.config.getMaxLevel()) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("setlevel", "invalid_level", this.config.getMaxLevel()) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            boolean success = this.levelingService.setPlayerLevel(targetPlayer, level, this.config);
            if (success) {
                if (this.leaderboardService != null) {
                    this.leaderboardService.overridePlayer(targetPlayer.getUuid(), targetPlayer.getUsername(), level, 0.0);
                }
                ctx.sendMessage(TinyMsg.parse("<color:green>" + this.messageService.getCommand("setlevel", "success", targetPlayer.getUsername(), level) + "</color>"));
            } else {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("setlevel", "error", new Object[0]) + "</color>"));
            }
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class SetPointsCommand
    extends AbstractAsyncCommand {
        private final LevelingService levelingService;
        private final StatsService statsService;
        private final LevelingConfig config;
        private final MessageService messageService;
        private final RequiredArg<String> playerArg;
        private final RequiredArg<Integer> pointsArg;

        public SetPointsCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService) {
            super("setpoints", "Set available stat points for a player (admin only).");
            this.setPermissionGroups(new String[]{"OP"});
            this.levelingService = levelingService;
            this.statsService = statsService;
            this.config = config;
            this.messageService = messageService;
            this.playerArg = this.withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
            this.pointsArg = this.withRequiredArg("points", "Number of points to set", (ArgumentType)ArgTypes.INTEGER);
        }

        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
            String playerName = (String)this.playerArg.get(ctx);
            Integer points = (Integer)this.pointsArg.get(ctx);
            PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
            if (targetPlayer == null) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("setpoints", "player_not_found", playerName) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            if (points < 0) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("setpoints", "invalid_points", new Object[0]) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            return this.setPlayerPoints(ctx, targetPlayer, points);
        }

        private CompletableFuture<Void> setPlayerPoints(@Nonnull CommandContext ctx, @Nonnull PlayerRef targetPlayer, int points) {
            World world;
            UUID worldUuid;
            Ref entityRef = targetPlayer.getReference();
            if (entityRef != null && entityRef.isValid() && (worldUuid = targetPlayer.getWorldUuid()) != null && (world = Universe.get().getWorld(worldUuid)) != null && world.isAlive()) {
                return CompletableFuture.runAsync(() -> world.execute(() -> {
                    Holder holder;
                    Store store = entityRef.getStore();
                    PlayerLevelData data = (PlayerLevelData)store.getComponent(entityRef, this.levelingService.getPlayerLevelDataType());
                    if (data == null) {
                        holder = targetPlayer.getHolder();
                        data = holder != null ? (PlayerLevelData)holder.ensureAndGetComponent(this.levelingService.getPlayerLevelDataType()) : new PlayerLevelData();
                    }
                    data.setAvailableStatPoints(points);
                    store.putComponent(entityRef, this.levelingService.getPlayerLevelDataType(), (Component)data);
                    holder = targetPlayer.getHolder();
                    if (holder != null) {
                        holder.putComponent(this.levelingService.getPlayerLevelDataType(), (Component)((PlayerLevelData)data.clone()));
                    }
                    ctx.sendMessage(TinyMsg.parse("<color:green>" + this.messageService.getCommand("setpoints", "success", targetPlayer.getUsername(), points) + "</color>"));
                }));
            }
            Holder holder = targetPlayer.getHolder();
            if (holder == null) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("setpoints", "error_no_world", targetPlayer.getUsername()) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            PlayerLevelData data = (PlayerLevelData)holder.ensureAndGetComponent(this.levelingService.getPlayerLevelDataType());
            if (data == null) {
                data = new PlayerLevelData();
            }
            data.setAvailableStatPoints(points);
            holder.putComponent(this.levelingService.getPlayerLevelDataType(), (Component)data);
            ctx.sendMessage(TinyMsg.parse("<color:green>" + this.messageService.getCommand("setpoints", "success", targetPlayer.getUsername(), points) + "</color>"));
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class ResetStatsCommand
    extends AbstractAsyncCommand {
        private final LevelingService levelingService;
        private final StatsService statsService;
        private final LevelingConfig config;
        private final MessageService messageService;
        private final RequiredArg<String> playerArg;

        public ResetStatsCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService) {
            super("resetstats", "Reset a player's allocated stats (admin only).");
            this.setPermissionGroups(new String[]{"OP"});
            this.levelingService = levelingService;
            this.statsService = statsService;
            this.config = config;
            this.messageService = messageService;
            this.playerArg = this.withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
        }

        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
            String playerName = (String)this.playerArg.get(ctx);
            PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
            if (targetPlayer == null) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("resetstats", "player_not_found", playerName) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            boolean success = this.statsService.resetAllocatedStats(targetPlayer, this.config);
            if (success) {
                ctx.sendMessage(TinyMsg.parse("<color:green>" + this.messageService.getCommand("resetstats", "success", targetPlayer.getUsername()) + "</color>"));
            } else {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("resetstats", "error", new Object[0]) + "</color>"));
            }
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class AddXPCommand
    extends AbstractAsyncCommand {
        private final LevelingService levelingService;
        private final StatsService statsService;
        private final LevelingConfig config;
        private final MessageService messageService;
        private final RequiredArg<String> playerArg;
        private final RequiredArg<Double> xpArg;

        public AddXPCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService) {
            super("addxp", "Add experience points to a player (admin only).");
            this.setPermissionGroups(new String[]{"OP"});
            this.levelingService = levelingService;
            this.statsService = statsService;
            this.config = config;
            this.messageService = messageService;
            this.playerArg = this.withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
            this.xpArg = this.withRequiredArg("xp", "Amount of XP to add", (ArgumentType)ArgTypes.DOUBLE);
        }

        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
            String playerName = (String)this.playerArg.get(ctx);
            Double xp = (Double)this.xpArg.get(ctx);
            PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
            if (targetPlayer == null) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("addxp", "player_not_found", playerName) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            if (xp <= 0.0) {
                ctx.sendMessage(TinyMsg.parse("<color:red>" + this.messageService.getCommand("addxp", "invalid_xp", new Object[0]) + "</color>"));
                return CompletableFuture.completedFuture(null);
            }
            Ref entityRef = targetPlayer.getReference();
            if (entityRef != null && entityRef.isValid()) {
                World world;
                UUID worldUuid = targetPlayer.getWorldUuid();
                if (worldUuid != null && (world = Universe.get().getWorld(worldUuid)) != null && world.isAlive()) {
                    world.execute(() -> {
                        this.levelingService.addExperience(targetPlayer, xp, this.config, null);
                        ctx.sendMessage(TinyMsg.parse("<color:green>" + this.messageService.getCommand("addxp", "success", xp, targetPlayer.getUsername()) + "</color>"));
                    });
                    return CompletableFuture.completedFuture(null);
                }
                ctx.sendMessage(TinyMsg.parse("<color:red>Unable to add XP: player's world is not available.</color>"));
                return CompletableFuture.completedFuture(null);
            }
            this.levelingService.addExperience(targetPlayer, xp, this.config, null);
            ctx.sendMessage(TinyMsg.parse("<color:green>" + this.messageService.getCommand("addxp", "success", xp, targetPlayer.getUsername()) + "</color>"));
            return CompletableFuture.completedFuture(null);
        }
    }
}
