/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.entity.entities.Player
 */
package com.natamus.hybrid.event.callback;

import com.hypixel.hytale.server.core.entity.entities.Player;

@FunctionalInterface
public static interface PlayerTickCallback.On_Player_Tick {
    public void onPlayerTick(Player var1);
}
