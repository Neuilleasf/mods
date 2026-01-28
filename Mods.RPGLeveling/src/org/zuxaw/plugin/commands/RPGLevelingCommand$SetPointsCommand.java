/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.server.core.NameMatching
 *  com.hypixel.hytale.server.core.command.system.CommandContext
 *  com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
 *  com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
 *  com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType
 *  com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  com.hypixel.hytale.server.core.universe.world.World
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.commands;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.TinyMsg;

private static class RPGLevelingCommand.SetPointsCommand
extends AbstractAsyncCommand {
    private final LevelingService levelingService;
    private final StatsService statsService;
    private final LevelingConfig config;
    private final MessageService messageService;
    private final RequiredArg<String> playerArg;
    private final RequiredArg<Integer> pointsArg;

    public RPGLevelingCommand.SetPointsCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService) {
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
