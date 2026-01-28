/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 */
package org.zuxaw.plugin.hud;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.zuxaw.plugin.hud.LevelProgressHud;

private static class LevelProgressHudManager.HudState {
    final LevelProgressHud hud;
    int lastLevel;
    double lastXP;
    int pendingLevel;
    double pendingXP;
    boolean dirty;
    boolean isVisible;
    long lastUiUpdateNanos;
    Player player;
    PlayerRef playerRef;

    LevelProgressHudManager.HudState(LevelProgressHud hud, int level, double xp, Player player, PlayerRef playerRef) {
        this.hud = hud;
        this.lastLevel = level;
        this.lastXP = xp;
        this.pendingLevel = level;
        this.pendingXP = xp;
        this.dirty = true;
        this.isVisible = true;
        this.lastUiUpdateNanos = 0L;
        this.player = player;
        this.playerRef = playerRef;
    }
}
