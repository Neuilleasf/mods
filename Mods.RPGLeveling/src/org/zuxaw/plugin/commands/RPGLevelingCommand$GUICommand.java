/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.server.core.command.system.CommandContext
 *  com.hypixel.hytale.server.core.command.system.CommandSender
 *  com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.gui.StatsGUIPage;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.TinyMsg;

private static class RPGLevelingCommand.GUICommand
extends AbstractAsyncCommand {
    private final LevelingService levelingService;
    private final StatsService statsService;
    private final LevelingConfig config;
    private final MessageService messageService;

    public RPGLevelingCommand.GUICommand(LevelingService levelingService, StatsService statsService, LevelingConfig config, MessageService messageService) {
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
