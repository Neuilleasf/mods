/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.entity.entities.Player
 */
package com.natamus.hybrid.event.callback;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.natamus.hybrid.implementations.event.Event;
import com.natamus.hybrid.implementations.event.EventFactory;

public class PlayerTickCallback {
    public static final Event<On_Player_Tick> ON_PLAYER_TICK = EventFactory.createArrayBacked(On_Player_Tick.class, callbacks -> player -> {
        for (On_Player_Tick callback : callbacks) {
            callback.onPlayerTick(player);
        }
    });

    private PlayerTickCallback() {
    }

    @FunctionalInterface
    public static interface On_Player_Tick {
        public void onPlayerTick(Player var1);
    }
}
