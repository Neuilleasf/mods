/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.NameMatching
 *  com.hypixel.hytale.server.core.command.system.CommandContext
 *  com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
 *  com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
 *  com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType
 *  com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.commands;

import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.services.LeaderboardService;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.TinyMsg;

private static class RPGLevelingCommand.SetLevelCommand
extends AbstractAsyncCommand {
    private final LevelingService levelingService;
    private final StatsService statsService;
    private final LevelingConfig config;
    private final MessageService messageService;
    private final LeaderboardService leaderboardService;
    private final RequiredArg<String> playerArg;
    private final RequiredArg<Integer> levelArg;

    public RPGLevelingCommand.SetLevelCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService, LeaderboardService leaderboardService) {
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
