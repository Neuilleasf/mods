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
import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.hud.backend.HudBackend;

public class MultipleHudBackend
implements HudBackend {
    @Nonnull
    private final Object mhudInstance;
    @Nonnull
    private final Method setCustomHudMethod;
    @Nonnull
    private final Method hideCustomHudMethod;

    public MultipleHudBackend() throws Exception {
        Class<?> mhudClass = Class.forName("com.buuz135.mhud.MultipleHUD");
        Method getInstance = mhudClass.getMethod("getInstance", new Class[0]);
        this.mhudInstance = getInstance.invoke(null, new Object[0]);
        this.setCustomHudMethod = mhudClass.getMethod("setCustomHud", Player.class, PlayerRef.class, String.class, CustomUIHud.class);
        this.hideCustomHudMethod = mhudClass.getMethod("hideCustomHud", Player.class, PlayerRef.class, String.class);
    }

    @Override
    public void show(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull String hudId, @Nonnull CustomUIHud hud) {
        try {
            this.setCustomHudMethod.invoke(this.mhudInstance, player, playerRef, hudId, hud);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void hide(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull String hudId) {
        try {
            this.hideCustomHudMethod.invoke(this.mhudInstance, player, playerRef, hudId);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public boolean supportsIncrementalUpdates() {
        return true;
    }
}
