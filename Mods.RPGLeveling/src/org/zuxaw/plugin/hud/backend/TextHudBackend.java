/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.Message
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.util.EventTitleUtil
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.hud.backend;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.hud.LevelProgressHud;
import org.zuxaw.plugin.hud.backend.HudBackend;

public class TextHudBackend
implements HudBackend {
    private final Map<PlayerRef, String> lastShownText = new HashMap<PlayerRef, String>();
    private static final long MIN_UPDATE_INTERVAL_NANOS = 2000000000L;
    private final Map<PlayerRef, Long> lastUpdateTime = new HashMap<PlayerRef, Long>();

    @Override
    public void show(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull String hudId, @Nonnull CustomUIHud hud) {
        String lastText;
        if (!(hud instanceof LevelProgressHud)) {
            return;
        }
        LevelProgressHud levelHud = (LevelProgressHud)hud;
        LevelProgressHud.LevelInfo info = levelHud.getLevelInfo();
        if (info == null) {
            return;
        }
        long now = System.nanoTime();
        Long lastUpdate = this.lastUpdateTime.get(playerRef);
        if (lastUpdate != null && now - lastUpdate < 2000000000L) {
            return;
        }
        String newText = info.levelText();
        if (newText.equals(lastText = this.lastShownText.get(playerRef))) {
            return;
        }
        try {
            String progressBar = this.createProgressBar(info.progressValue(), 20);
            EventTitleUtil.showEventTitleToPlayer((PlayerRef)playerRef, (Message)Message.raw((String)newText), (Message)Message.raw((String)progressBar), (boolean)false);
            this.lastShownText.put(playerRef, newText);
            this.lastUpdateTime.put(playerRef, now);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void hide(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull String hudId) {
        try {
            this.lastShownText.remove(playerRef);
            this.lastUpdateTime.remove(playerRef);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public boolean supportsIncrementalUpdates() {
        return false;
    }

    private String createProgressBar(float progress, int barLength) {
        int filled = Math.round(progress * (float)barLength);
        filled = Math.max(0, Math.min(barLength, filled));
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; ++i) {
            if (i < filled) {
                bar.append("\u2588");
                continue;
            }
            bar.append("\u2591");
        }
        bar.append("] ");
        bar.append(String.format("%.0f%%", Float.valueOf(progress * 100.0f)));
        return bar.toString();
    }

    public void cleanup(@Nonnull PlayerRef playerRef) {
        this.lastShownText.remove(playerRef);
        this.lastUpdateTime.remove(playerRef);
    }
}
