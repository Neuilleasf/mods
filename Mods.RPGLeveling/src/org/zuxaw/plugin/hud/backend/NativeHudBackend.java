/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.hud.backend;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.hud.backend.HudBackend;

public class NativeHudBackend
implements HudBackend {
    @Override
    public void show(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull String hudId, @Nonnull CustomUIHud hud) {
        player.getHudManager().setCustomHud(playerRef, hud);
    }

    @Override
    public void hide(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull String hudId) {
        player.getHudManager().setCustomHud(playerRef, null);
    }

    @Override
    public boolean supportsIncrementalUpdates() {
        return false;
    }
}
