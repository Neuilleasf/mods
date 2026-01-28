/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Ref
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

import com.hypixel.hytale.component.Ref;
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
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.TinyMsg;

private static class RPGLevelingCommand.AddXPCommand
extends AbstractAsyncCommand {
    private final LevelingService levelingService;
    private final StatsService statsService;
    private final LevelingConfig config;
    private final MessageService messageService;
    private final RequiredArg<String> playerArg;
    private final RequiredArg<Double> xpArg;

    public RPGLevelingCommand.AddXPCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService) {
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
