/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.common.plugin.PluginIdentifier
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.plugin.PluginBase
 *  com.hypixel.hytale.server.core.plugin.PluginManager
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.hud;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.hud.LevelProgressHud;
import org.zuxaw.plugin.hud.backend.HudBackend;
import org.zuxaw.plugin.hud.backend.NativeHudBackend;
import org.zuxaw.plugin.hud.backend.TextHudBackend;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.utils.DebugLogger;

public class LevelProgressHudManager {
    private static final DebugLogger DEBUG = DebugLogger.forEnclosingClass();
    private static final String HUD_IDENTIFIER = "RPGLeveling_LevelProgress";
    private static final long MIN_UI_UPDATE_INTERVAL_NANOS = 150000000L;
    private final Map<PlayerRef, HudState> hudStates = new HashMap<PlayerRef, HudState>();
    @Nonnull
    private final HudBackend backend;
    private boolean usingTextFallback = false;
    @Nonnull
    private final LevelingService levelingService;
    @Nonnull
    private final LevelingConfig config;
    @Nonnull
    private final MessageService messageService;

    public LevelProgressHudManager(@Nonnull LevelingService levelingService, @Nonnull LevelingConfig config, @Nonnull MessageService messageService) {
        this.levelingService = levelingService;
        this.config = config;
        this.messageService = messageService;
        this.backend = this.detectBackend();
    }

    @Nonnull
    private HudBackend detectBackend() {
        try {
            PluginBase plugin = PluginManager.get().getPlugin(PluginIdentifier.fromString((String)"Buuz135:MultipleHUD"));
            if (plugin != null) {
                Class<?> clazz = Class.forName("org.zuxaw.plugin.hud.backend.MultipleHudBackend");
                HudBackend backend = (HudBackend)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                return backend;
            }
        }
        catch (Throwable t) {
            DEBUG.warning(this.config, () -> "[HUD] MultipleHUD not available: " + t.getMessage());
        }
        try {
            NativeHudBackend nativeBackend = new NativeHudBackend();
            return nativeBackend;
        }
        catch (Throwable t) {
            DEBUG.severe(this.config, () -> "[HUD] Native CustomUIHud backend failed: " + t.getMessage());
            DEBUG.warning(this.config, () -> "[HUD] Using EventTitle text fallback (compatibility mode)");
            this.usingTextFallback = true;
            return new TextHudBackend();
        }
    }

    public void updateHud(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull PlayerLevelData data) {
        boolean created = false;
        boolean updated = false;
        boolean flushed = false;
        boolean visible = false;
        try {
            boolean xpChanged;
            int currentLevel = data.getLevel();
            double currentXP = data.getExperience();
            HudState state = this.hudStates.get(playerRef);
            if (state == null) {
                created = true;
                LevelProgressHud hud = new LevelProgressHud(playerRef, this.levelingService, this.config, this.messageService);
                state = new HudState(hud, currentLevel, currentXP, player, playerRef);
                this.hudStates.put(playerRef, state);
                hud.updateLevelInfo(currentLevel, currentXP);
                this.backend.show(player, playerRef, HUD_IDENTIFIER, hud);
                state.dirty = false;
                return;
            }
            state.player = player;
            state.playerRef = playerRef;
            boolean levelChanged = currentLevel != state.lastLevel;
            boolean bl = xpChanged = Math.abs(currentXP - state.lastXP) > 0.01;
            if (!levelChanged && !xpChanged) {
                return;
            }
            updated = true;
            state.lastLevel = currentLevel;
            state.lastXP = currentXP;
            state.pendingLevel = currentLevel;
            state.pendingXP = currentXP;
            state.dirty = true;
            visible = state.isVisible;
            if (state.isVisible) {
                flushed = true;
                this.flushIfAllowed(state, levelChanged);
            }
        }
        catch (Exception e) {
            DEBUG.severe(this.config, () -> "[HUD] updateHud failed: " + e.getMessage());
        }
    }

    private void flushIfAllowed(@Nonnull HudState state, boolean force) {
        if (!state.dirty) {
            return;
        }
        long now = System.nanoTime();
        if (!force && now - state.lastUiUpdateNanos < 150000000L) {
            return;
        }
        boolean incremental = false;
        boolean text = false;
        state.hud.updateLevelInfo(state.pendingLevel, state.pendingXP);
        if (this.backend.supportsIncrementalUpdates()) {
            incremental = true;
            state.hud.requestUpdate();
        } else if (this.usingTextFallback) {
            text = true;
            this.backend.show(state.player, state.playerRef, HUD_IDENTIFIER, state.hud);
        } else {
            state.hud.show();
        }
        state.lastUiUpdateNanos = now;
        state.dirty = false;
    }

    public void hideHud(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
    }

    public void showHud(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
        try {
            HudState state = this.hudStates.get(playerRef);
            if (state != null && !state.isVisible) {
                this.backend.show(player, playerRef, HUD_IDENTIFIER, state.hud);
                state.isVisible = true;
                if (this.backend.supportsIncrementalUpdates()) {
                    state.dirty = true;
                    this.flushIfAllowed(state, true);
                }
            }
        }
        catch (Exception e) {
            DEBUG.severe(this.config, () -> "[HUD] showHud failed: " + e.getMessage());
        }
    }

    public void onPlayerLeave(@Nonnull PlayerRef playerRef) {
        HudBackend hudBackend;
        this.hudStates.remove(playerRef);
        if (this.usingTextFallback && (hudBackend = this.backend) instanceof TextHudBackend) {
            TextHudBackend textBackend = (TextHudBackend)hudBackend;
            textBackend.cleanup(playerRef);
        }
    }

    public boolean hasHud(@Nonnull PlayerRef playerRef) {
        return this.hudStates.containsKey(playerRef);
    }

    public boolean isHudVisible(@Nonnull PlayerRef playerRef) {
        HudState state = this.hudStates.get(playerRef);
        return state != null && state.isVisible;
    }

    private static class HudState {
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

        HudState(LevelProgressHud hud, int level, double xp, Player player, PlayerRef playerRef) {
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
}
