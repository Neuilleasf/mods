/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent
 */
package com.natamus.hybrid.event.ievent;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.natamus.hybrid.functions.EntityStatFunctions;

public class HybridPlayerEvents {
    public static void onPlayerReadyEvent(PlayerReadyEvent e) {
        Player player = e.getPlayer();
        if (EntityStatFunctions.shouldGenerateEntityStatData()) {
            EntityStatFunctions.generateEntityStatData(player);
        }
    }
}
