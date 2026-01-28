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

public interface HudBackend {
    public void show(@Nonnull Player var1, @Nonnull PlayerRef var2, @Nonnull String var3, @Nonnull CustomUIHud var4);

    public void hide(@Nonnull Player var1, @Nonnull PlayerRef var2, @Nonnull String var3);

    public boolean supportsIncrementalUpdates();
}
