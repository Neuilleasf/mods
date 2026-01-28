/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud
 *  com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.hud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.utils.DebugLogger;

public class LevelProgressHud
extends CustomUIHud {
    private static final DebugLogger DEBUG = DebugLogger.forEnclosingClass();
    @Nullable
    private LevelInfo levelInfo;
    @Nonnull
    private final LevelingService levelingService;
    @Nonnull
    private final LevelingConfig config;
    @Nonnull
    private final MessageService messageService;

    public LevelProgressHud(@Nonnull PlayerRef playerRef, @Nonnull LevelingService levelingService, @Nonnull LevelingConfig config, @Nonnull MessageService messageService) {
        super(playerRef);
        this.levelingService = levelingService;
        this.config = config;
        this.messageService = messageService;
    }

    protected void build(@Nonnull UICommandBuilder ui) {
        try {
            ui.append("HUD/LevelProgress.ui");
            if (this.levelInfo != null) {
                ui.set("#LevelLabel.Text", this.levelInfo.levelText);
                ui.set("#ProgressBar.Value", this.levelInfo.progressValue);
            } else {
                ui.set("#LevelLabel.Text", "LEVEL: 1 XP: 0 / 0");
                ui.set("#ProgressBar.Value", 0.0f);
            }
        }
        catch (Exception e) {
            DEBUG.severe(this.config, () -> "[HUD] build() failed: " + e.getMessage());
            throw e;
        }
    }

    public void requestUpdate() {
        try {
            if (this.levelInfo == null) {
                return;
            }
            UICommandBuilder commandBuilder = new UICommandBuilder();
            commandBuilder.set("#LevelLabel.Text", this.levelInfo.levelText);
            commandBuilder.set("#ProgressBar.Value", this.levelInfo.progressValue);
            this.update(false, commandBuilder);
        }
        catch (Exception e) {
            DEBUG.severe(this.config, () -> "[HUD] requestUpdate() failed: " + e.getMessage());
        }
    }

    public void updateLevelInfo(int level, double currentXP) {
        try {
            String levelText;
            float progressValue;
            int maxLevel = this.config.getMaxLevel();
            if (level >= maxLevel) {
                progressValue = 1.0f;
                levelText = this.messageService.getHudMessage("level_xp_max", level, String.format("%.0f", currentXP), String.format("%.0f", currentXP));
            } else {
                double xpNeeded = this.levelingService.getXPNeededForNextLevel(level, this.config);
                if (xpNeeded > 0.0) {
                    progressValue = (float)Math.min(1.0, Math.max(0.0, currentXP / xpNeeded));
                    levelText = this.messageService.getHudMessage("level_xp_progress", level, String.format("%.0f", currentXP), String.format("%.0f", xpNeeded));
                } else {
                    progressValue = 0.0f;
                    levelText = this.messageService.getHudMessage("level_xp_progress", level, String.format("%.0f", currentXP), "0");
                }
            }
            this.levelInfo = new LevelInfo(levelText, progressValue);
        }
        catch (Exception e) {
            DEBUG.severe(this.config, () -> "[HUD] updateLevelInfo() failed: " + e.getMessage());
            throw e;
        }
    }

    @Nullable
    public LevelInfo getLevelInfo() {
        return this.levelInfo;
    }

    public record LevelInfo(@Nonnull String levelText, float progressValue) {
    }
}
