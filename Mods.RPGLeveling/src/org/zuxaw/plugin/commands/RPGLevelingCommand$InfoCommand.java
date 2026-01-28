/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.command.system.CommandContext
 *  com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
 *  com.hypixel.hytale.server.core.permissions.PermissionsModule
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.TinyMsg;

private static class RPGLevelingCommand.InfoCommand
extends AbstractAsyncCommand {
    private final LevelingService levelingService;
    private final StatsService statsService;
    private final LevelingConfig config;
    private final MessageService messageService;

    public RPGLevelingCommand.InfoCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService) {
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
