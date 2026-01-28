/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.protocol.packets.interface_.NotificationStyle
 *  com.hypixel.hytale.server.core.Message
 *  com.hypixel.hytale.server.core.io.PacketHandler
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.util.EventTitleUtil
 *  com.hypixel.hytale.server.core.util.NotificationUtil
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.utils;

import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.utils.LocalizationValidator;
import org.zuxaw.plugin.utils.TinyMsg;

public class NotificationHelper {
    public static void sendNotification(@Nonnull PlayerRef playerRef, @Nonnull String message, @Nonnull NotificationStyle style) {
        LocalizationValidator.validateNotification(message, NotificationHelper.class);
        NotificationUtil.sendNotification((PacketHandler)playerRef.getPacketHandler(), (Message)TinyMsg.parse(message), (NotificationStyle)style);
    }

    public static void sendNotification(@Nonnull PlayerRef playerRef, @Nonnull String message) {
        NotificationHelper.sendNotification(playerRef, message, NotificationStyle.Default);
    }

    public static void sendSuccessNotification(@Nonnull PlayerRef playerRef, @Nonnull String message) {
        NotificationHelper.sendNotification(playerRef, message, NotificationStyle.Success);
    }

    public static void sendWarningNotification(@Nonnull PlayerRef playerRef, @Nonnull String message) {
        NotificationHelper.sendNotification(playerRef, message, NotificationStyle.Warning);
    }

    public static void sendDangerNotification(@Nonnull PlayerRef playerRef, @Nonnull String message) {
        NotificationHelper.sendNotification(playerRef, message, NotificationStyle.Danger);
    }

    public static void showEventTitle(@Nonnull PlayerRef playerRef, @Nonnull String primaryTitle, @Nonnull String secondaryTitle, boolean isMajor) {
        World world;
        UUID worldUuid = playerRef.getWorldUuid();
        if (worldUuid != null && (world = Universe.get().getWorld(worldUuid)) != null && world.isAlive()) {
            world.execute(() -> EventTitleUtil.showEventTitleToPlayer((PlayerRef)playerRef, (Message)Message.raw((String)primaryTitle), (Message)Message.raw((String)secondaryTitle), (boolean)isMajor));
            return;
        }
        try {
            EventTitleUtil.showEventTitleToPlayer((PlayerRef)playerRef, (Message)Message.raw((String)primaryTitle), (Message)Message.raw((String)secondaryTitle), (boolean)isMajor);
        }
        catch (Exception e) {
            NotificationHelper.sendNotification(playerRef, primaryTitle + " - " + secondaryTitle, NotificationStyle.Success);
        }
    }

    public static void showLevelUpTitle(@Nonnull PlayerRef playerRef, int newLevel, @Nonnull MessageService messageService) {
        NotificationHelper.showEventTitle(playerRef, messageService.getNotification("level_up_title", new Object[0]), messageService.getNotification("level_up_subtitle", newLevel), true);
    }

    public static void showMaxLevelTitle(@Nonnull PlayerRef playerRef, int maxLevel, @Nonnull MessageService messageService) {
        NotificationHelper.showEventTitle(playerRef, messageService.getNotification("max_level_title", new Object[0]), messageService.getNotification("max_level_subtitle", maxLevel), true);
    }

    public static void showDeathPenaltyTitle(@Nonnull PlayerRef playerRef, @Nonnull MessageService messageService) {
        NotificationHelper.showEventTitle(playerRef, messageService.getNotification("level_reset_death_title", new Object[0]), messageService.getNotification("level_reset_death_subtitle", new Object[0]), true);
    }
}
