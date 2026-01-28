/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.protocol.packets.interface_.NotificationStyle
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.services;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.formulas.Formulas;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.DebugLogger;
import org.zuxaw.plugin.utils.NotificationHelper;

public class LevelingService {
    private static final DebugLogger DEBUG = DebugLogger.forEnclosingClass();
    private final ComponentType<EntityStore, PlayerLevelData> playerLevelDataType;
    @Nullable
    private StatsService statsService;
    private final MessageService messageService;

    public LevelingService(@Nonnull ComponentType<EntityStore, PlayerLevelData> playerLevelDataType, @Nonnull MessageService messageService) {
        this.playerLevelDataType = playerLevelDataType;
        this.messageService = messageService;
    }

    public void setStatsService(@Nonnull StatsService statsService) {
        this.statsService = statsService;
    }

    public double calculateXPFromMaxHealth(double maxHealth, @Nonnull LevelingConfig config) {
        return Formulas.xpFromKill(maxHealth, config);
    }

    public double getXPRequiredForLevel(int level, @Nonnull LevelingConfig config) {
        return Formulas.xpRequiredForLevel(level, config);
    }

    public double getXPNeededForNextLevel(int currentLevel, @Nonnull LevelingConfig config) {
        return Formulas.xpNeededForNextLevel(currentLevel, config);
    }

    public void addExperience(@Nonnull PlayerRef playerRef, double xp, @Nonnull LevelingConfig config, @Nullable CommandBuffer<EntityStore> commandBuffer) {
        if (xp <= 0.0) {
            DEBUG.warning(config, () -> "Cannot add XP: XP is " + xp + " for player " + playerRef.getUsername());
            return;
        }
        Ref entityRef = playerRef.getReference();
        boolean componentNeedsToBeAdded = false;
        PlayerLevelData data = null;
        if (entityRef != null && entityRef.isValid()) {
            Store store = entityRef.getStore();
            data = (PlayerLevelData)store.getComponent(entityRef, this.playerLevelDataType);
            if (data == null) {
                Holder holder = playerRef.getHolder();
                if (holder != null) {
                    data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
                    if (commandBuffer != null) {
                        PlayerLevelData clonedData = (PlayerLevelData)data.clone();
                        commandBuffer.addComponent(entityRef, this.playerLevelDataType, (Component)clonedData);
                    }
                } else {
                    data = new PlayerLevelData();
                    componentNeedsToBeAdded = true;
                }
            }
        } else {
            Holder holder = playerRef.getHolder();
            if (holder != null) {
                data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
            }
        }
        if (data == null) {
            DEBUG.warning(config, () -> "Cannot add XP: Unable to get or create player data for " + playerRef.getUsername());
            return;
        }
        if (data.getLevel() >= config.getMaxLevel()) {
            return;
        }
        int currentLevel = data.getLevel();
        data.addExperience(xp);
        if (componentNeedsToBeAdded && entityRef != null && entityRef.isValid() && commandBuffer != null) {
            commandBuffer.addComponent(entityRef, this.playerLevelDataType, (Component)data);
        }
        this.sendXPGainMessage(playerRef, data, xp, currentLevel, config);
        this.checkLevelUp(playerRef, data, config);
    }

    private void checkLevelUp(@Nonnull PlayerRef playerRef, @Nonnull PlayerLevelData data, @Nonnull LevelingConfig config) {
        int levelsGained = 0;
        while (data.getLevel() < config.getMaxLevel()) {
            double xpNeeded = this.getXPNeededForNextLevel(data.getLevel(), config);
            if (!(data.getExperience() >= xpNeeded)) break;
            data.setExperience(data.getExperience() - xpNeeded);
            data.setLevel(data.getLevel() + 1);
            ++levelsGained;
            int statPointsPerLevel = config.getStatPointsPerLevel();
            data.setAvailableStatPoints(data.getAvailableStatPoints() + statPointsPerLevel);
            NotificationHelper.showLevelUpTitle(playerRef, data.getLevel(), this.messageService);
            if (statPointsPerLevel > 0) {
                String plural = statPointsPerLevel > 1 ? "s" : "";
                String message = this.messageService.getNotification("stat_points_earned", statPointsPerLevel, plural);
                NotificationHelper.sendNotification(playerRef, "<color:aqua>" + message + "</color>", NotificationStyle.Default);
            }
            if (data.getLevel() < config.getMaxLevel()) continue;
            NotificationHelper.showMaxLevelTitle(playerRef, config.getMaxLevel(), this.messageService);
            break;
        }
    }

    private void sendXPGainMessage(@Nonnull PlayerRef playerRef, @Nonnull PlayerLevelData data, double xpEarned, int levelBefore, @Nonnull LevelingConfig config) {
        int i;
        int currentLevel = data.getLevel();
        double currentXP = data.getExperience();
        if (currentLevel >= config.getMaxLevel()) {
            String maxLevelMessage = String.format("[LEVEL %d] +%.1f XP (MAX LEVEL)", currentLevel, xpEarned);
            NotificationHelper.sendNotification(playerRef, maxLevelMessage, NotificationStyle.Default);
            return;
        }
        double xpNeeded = this.getXPNeededForNextLevel(currentLevel, config);
        double xpProgress = currentXP / xpNeeded;
        int progressPercent = (int)(xpProgress * 100.0);
        int filled = (int)(xpProgress * 20.0);
        StringBuilder progressBar = new StringBuilder();
        if (filled > 0) {
            if (filled >= 10) {
                progressBar.append("<gradient:green:lime>");
                for (i = 0; i < filled; ++i) {
                    progressBar.append("|");
                }
                progressBar.append("</gradient>");
            } else {
                progressBar.append("<color:green>");
                for (i = 0; i < filled; ++i) {
                    progressBar.append("|");
                }
                progressBar.append("</color>");
            }
        }
        if (filled < 20) {
            progressBar.append("<color:gray>");
            for (i = filled; i < 20; ++i) {
                progressBar.append("-");
            }
            progressBar.append("</color>");
        }
        String message = String.format("<color:gold><b>[LEVEL %d]</b></color> <color:green>+%.1f XP</color> <color:gray>|</color> %s <color:gray>|</color> <color:yellow>%.1f/%.1f</color> <color:gray>(</color><color:aqua>%d%%</color><color:gray>)</color>", currentLevel, xpEarned, progressBar.toString(), currentXP, xpNeeded, progressPercent);
        NotificationHelper.sendSuccessNotification(playerRef, message);
    }

    @Nonnull
    public PlayerLevelData getPlayerData(@Nonnull PlayerRef playerRef) {
        Holder holder = playerRef.getHolder();
        if (holder != null) {
            PlayerLevelData data = (PlayerLevelData)holder.getComponent(this.playerLevelDataType);
            if (data != null) {
                return data;
            }
            data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
            if (data != null) {
                return data;
            }
        }
        return new PlayerLevelData();
    }

    @Nonnull
    public ComponentType<EntityStore, PlayerLevelData> getPlayerLevelDataType() {
        return this.playerLevelDataType;
    }

    private void updateLevelProgressHud(@Nonnull PlayerRef playerRef) {
    }

    public void resetPlayerLevelOnDeath(@Nonnull PlayerRef playerRef, @Nonnull LevelingConfig config, @Nullable CommandBuffer<EntityStore> commandBuffer) {
        Holder holder;
        Store store;
        Ref entityRef = playerRef.getReference();
        PlayerLevelData data = null;
        if (entityRef != null && entityRef.isValid()) {
            Store store2 = entityRef.getStore();
            data = (PlayerLevelData)store2.getComponent(entityRef, this.playerLevelDataType);
            if (data == null) {
                Holder holder2 = playerRef.getHolder();
                data = holder2 != null ? (PlayerLevelData)holder2.ensureAndGetComponent(this.playerLevelDataType) : new PlayerLevelData();
            }
        } else {
            Holder holder3 = playerRef.getHolder();
            data = holder3 != null ? (PlayerLevelData)holder3.ensureAndGetComponent(this.playerLevelDataType) : new PlayerLevelData();
        }
        if (data == null) {
            DEBUG.warning(config, () -> ">>> RESET ON DEATH: FAILED - Cannot get player data for " + playerRef.getUsername());
            return;
        }
        PlayerLevelData playerData = data;
        DEBUG.info(config, () -> ">>> RESET ON DEATH: Current player data - Level: " + playerData.getLevel() + ", XP: " + playerData.getExperience() + ", StatPoints: " + playerData.getAvailableStatPoints());
        if (playerData.getLevel() <= 1) {
            DEBUG.info(config, () -> ">>> RESET ON DEATH: Player is already level 1 or below, skipping reset");
            return;
        }
        DEBUG.info(config, () -> ">>> RESET ON DEATH: Proceeding with reset - player is level " + playerData.getLevel());
        playerData.setLevel(1);
        playerData.setExperience(0.0);
        DEBUG.info(config, () -> ">>> RESET ON DEATH: Set level to 1 and XP to 0");
        Map<String, Integer> allocatedStats = playerData.getAllocatedStats();
        int totalAllocatedBefore = allocatedStats.values().stream().mapToInt(Integer::intValue).sum();
        allocatedStats.clear();
        playerData.setAvailableStatPoints(0);
        if (entityRef != null && entityRef.isValid() && this.statsService != null) {
            store = entityRef.getStore();
            this.statsService.removeAllStatModifiers((Ref<EntityStore>)entityRef, (Store<EntityStore>)store);
        }
        if (entityRef != null && entityRef.isValid()) {
            if (commandBuffer != null) {
                commandBuffer.putComponent(entityRef, this.playerLevelDataType, (Component)((PlayerLevelData)playerData.clone()));
            } else {
                store = entityRef.getStore();
                store.putComponent(entityRef, this.playerLevelDataType, (Component)playerData);
            }
        }
        if ((holder = playerRef.getHolder()) != null) {
            holder.putComponent(this.playerLevelDataType, (Component)((PlayerLevelData)playerData.clone()));
        }
        try {
            NotificationHelper.showDeathPenaltyTitle(playerRef, this.messageService);
        }
        catch (Exception e) {
            DEBUG.warning(config, () -> ">>> RESET ON DEATH: Failed to send notification - " + e.getMessage());
        }
        try {
            this.updateLevelProgressHud(playerRef);
        }
        catch (Exception e) {
            DEBUG.warning(config, () -> ">>> RESET ON DEATH: Failed to update HUD - " + e.getMessage());
        }
        DEBUG.info(config, () -> "Player " + playerRef.getUsername() + " died - level reset to 1");
    }

    public boolean setPlayerLevel(@Nonnull PlayerRef playerRef, int newLevel, @Nonnull LevelingConfig config) {
        World world;
        UUID worldUuid;
        if (newLevel < 1 || newLevel > config.getMaxLevel()) {
            DEBUG.warning(config, () -> "Invalid level " + newLevel + " for player " + playerRef.getUsername() + " (must be between 1 and " + config.getMaxLevel() + ")");
            return false;
        }
        Ref entityRef = playerRef.getReference();
        if (entityRef != null && entityRef.isValid() && (worldUuid = playerRef.getWorldUuid()) != null && (world = Universe.get().getWorld(worldUuid)) != null && world.isAlive()) {
            CompletableFuture resultFuture = new CompletableFuture();
            world.execute(() -> {
                Store store = entityRef.getStore();
                PlayerLevelData data = (PlayerLevelData)store.getComponent(entityRef, this.playerLevelDataType);
                if (data == null) {
                    Holder holder = playerRef.getHolder();
                    data = holder != null ? (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType) : new PlayerLevelData();
                }
                data.setLevel(newLevel);
                data.setExperience(0.0);
                Map<String, Integer> allocatedStats = data.getAllocatedStats();
                int totalAllocated = 0;
                for (Integer points : allocatedStats.values()) {
                    totalAllocated += points.intValue();
                }
                allocatedStats.clear();
                data.getAllocatedStats().clear();
                if (this.statsService != null) {
                    this.statsService.removeAllStatModifiers((Ref<EntityStore>)entityRef, (Store<EntityStore>)store);
                }
                int newAvailablePoints = (newLevel - 1) * config.getStatPointsPerLevel() + totalAllocated;
                data.setAvailableStatPoints(newAvailablePoints);
                store.putComponent(entityRef, this.playerLevelDataType, (Component)data);
                Holder holder = playerRef.getHolder();
                if (holder != null) {
                    holder.putComponent(this.playerLevelDataType, (Component)((PlayerLevelData)data.clone()));
                }
                this.updateLevelProgressHud(playerRef);
                resultFuture.complete(true);
            });
            try {
                return (Boolean)resultFuture.get();
            }
            catch (Exception e) {
                DEBUG.warning(config, () -> "Error setting player level on world thread: " + e.getMessage());
                return false;
            }
        }
        Holder holder = playerRef.getHolder();
        if (holder == null) {
            DEBUG.warning(config, () -> "Cannot set level: Holder is null for " + playerRef.getUsername());
            return false;
        }
        PlayerLevelData data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
        if (data == null) {
            data = new PlayerLevelData();
        }
        data.setLevel(newLevel);
        data.setExperience(0.0);
        Map<String, Integer> allocatedStats = data.getAllocatedStats();
        int totalAllocated = 0;
        for (Integer points : allocatedStats.values()) {
            totalAllocated += points.intValue();
        }
        allocatedStats.clear();
        data.getAllocatedStats().clear();
        int newAvailablePoints = (newLevel - 1) * config.getStatPointsPerLevel() + totalAllocated;
        data.setAvailableStatPoints(newAvailablePoints);
        holder.putComponent(this.playerLevelDataType, (Component)data);
        this.updateLevelProgressHud(playerRef);
        return true;
    }
}
