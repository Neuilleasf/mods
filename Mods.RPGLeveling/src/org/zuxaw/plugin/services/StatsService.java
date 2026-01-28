/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule
 *  com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes
 *  com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType
 *  com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier
 *  com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier$ModifierTarget
 *  com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier
 *  com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier$CalculationType
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.services;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.formulas.Formulas;
import org.zuxaw.plugin.utils.DebugLogger;

public class StatsService {
    private static final DebugLogger DEBUG = DebugLogger.forEnclosingClass();
    private final ComponentType<EntityStore, PlayerLevelData> playerLevelDataType;
    private final ComponentType<EntityStore, EntityStatMap> statMapType;
    public static final String[] VALID_STATS = new String[]{"Health", "Stamina", "Mana", "Ammo", "Oxygen", "StaminaRegenDelay", "StaminaConsumption", "Damage", "Mining", "Woodcutting", "Defense"};

    public StatsService(@Nonnull ComponentType<EntityStore, PlayerLevelData> playerLevelDataType) {
        this.playerLevelDataType = playerLevelDataType;
        this.statMapType = EntityStatsModule.get().getEntityStatMapComponentType();
    }

    public int getStatIndex(@Nonnull String statName) {
        switch (statName.toLowerCase()) {
            case "health": {
                return DefaultEntityStatTypes.getHealth();
            }
            case "stamina": {
                return DefaultEntityStatTypes.getStamina();
            }
            case "mana": {
                return DefaultEntityStatTypes.getMana();
            }
            case "ammo": {
                return DefaultEntityStatTypes.getAmmo();
            }
            case "oxygen": {
                return DefaultEntityStatTypes.getOxygen();
            }
            case "staminaregendelay": {
                try {
                    int index = EntityStatType.getAssetMap().getIndex((Object)"StaminaRegenDelay");
                    if (index >= 0) {
                        return index;
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                return -1;
            }
            case "damage": 
            case "mining": 
            case "woodcutting": 
            case "defense": {
                return -1;
            }
        }
        return -1;
    }

    public boolean isValidStat(@Nonnull String statName) {
        for (String validStat : VALID_STATS) {
            if (!validStat.equalsIgnoreCase(statName)) continue;
            return true;
        }
        return false;
    }

    public boolean allocateStatPoints(@Nonnull PlayerRef playerRef, @Nonnull String statName, int points, @Nonnull LevelingConfig config) {
        int maxStatPoints;
        World world;
        UUID worldUuid;
        if (points <= 0) {
            DEBUG.warning(config, () -> "Cannot allocate non-positive points: " + points);
            return false;
        }
        if (!this.isValidStat(statName)) {
            DEBUG.warning(config, () -> "Invalid stat name: " + statName);
            return false;
        }
        Ref entityRef = playerRef.getReference();
        PlayerLevelData data = null;
        if (entityRef != null && entityRef.isValid() && (worldUuid = playerRef.getWorldUuid()) != null && (world = Universe.get().getWorld(worldUuid)) != null && world.isAlive()) {
            CompletableFuture resultFuture = new CompletableFuture();
            world.execute(() -> {
                int maxStatPoints;
                Store store = entityRef.getStore();
                PlayerLevelData storeData = (PlayerLevelData)store.getComponent(entityRef, this.playerLevelDataType);
                if (storeData == null) {
                    Holder holder = playerRef.getHolder();
                    storeData = holder != null ? (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType) : new PlayerLevelData();
                }
                if (storeData.getAvailableStatPoints() < points) {
                    resultFuture.complete(false);
                    return;
                }
                int currentAllocation = storeData.getAllocatedPoints(statName);
                int newAllocation = currentAllocation + points;
                if (newAllocation > (maxStatPoints = config.getMaxStatPointsForStat(statName))) {
                    DEBUG.warning(config, () -> String.format("Cannot exceed maximum of %d points for %s! (Current: %d, Trying to add: %d)", maxStatPoints, statName, currentAllocation, points));
                    resultFuture.complete(false);
                    return;
                }
                storeData.setAvailableStatPoints(storeData.getAvailableStatPoints() - points);
                storeData.allocatePoints(statName, newAllocation);
                store.putComponent(entityRef, this.playerLevelDataType, (Component)storeData);
                Holder holder = playerRef.getHolder();
                if (holder != null) {
                    holder.putComponent(this.playerLevelDataType, (Component)((PlayerLevelData)storeData.clone()));
                }
                this.applyStatModifiers((Ref<EntityStore>)entityRef, (Store<EntityStore>)store, storeData, config);
                resultFuture.complete(true);
            });
            try {
                return (Boolean)resultFuture.get();
            }
            catch (Exception e) {
                DEBUG.warning(config, () -> "Error allocating stat points on world thread: " + e.getMessage());
                return false;
            }
        }
        Holder holder = playerRef.getHolder();
        if (holder == null) {
            DEBUG.warning(config, () -> "Cannot allocate stats: Holder is null for " + playerRef.getUsername());
            return false;
        }
        data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
        if (data == null) {
            DEBUG.warning(config, () -> "Cannot allocate stats: Failed to get player data for " + playerRef.getUsername());
            return false;
        }
        if (data.getAvailableStatPoints() < points) {
            return false;
        }
        int currentAllocation = data.getAllocatedPoints(statName);
        int newAllocation = currentAllocation + points;
        if (newAllocation > (maxStatPoints = config.getMaxStatPointsForStat(statName))) {
            DEBUG.warning(config, () -> String.format("Cannot exceed maximum of %d points for %s! (Current: %d, Trying to add: %d)", maxStatPoints, statName, currentAllocation, points));
            return false;
        }
        data.setAvailableStatPoints(data.getAvailableStatPoints() - points);
        data.allocatePoints(statName, newAllocation);
        return true;
    }

    public void applyStatModifiers(@Nonnull Ref<EntityStore> entityRef, @Nonnull Store<EntityStore> store, @Nonnull PlayerLevelData data, @Nonnull LevelingConfig config) {
        EntityStatMap statMap = (EntityStatMap)store.getComponent(entityRef, this.statMapType);
        if (statMap == null) {
            DEBUG.warning(config, () -> "Cannot apply stat modifiers: EntityStatMap not found");
            return;
        }
        Map<String, Integer> allocatedStats = data.getAllocatedStats();
        double statValuePerPoint = config.getStatValuePerPoint();
        for (Map.Entry<String, Integer> entry : allocatedStats.entrySet()) {
            int statIndex;
            String statName = entry.getKey();
            int allocatedPoints = entry.getValue();
            if (allocatedPoints <= 0 || (statIndex = this.getStatIndex(statName)) < 0 || statName.equalsIgnoreCase("StaminaRegenDelay")) continue;
            float bonusValue = Formulas.statModifierValue(allocatedPoints, statValuePerPoint);
            StaticModifier.CalculationType calculationType = StaticModifier.CalculationType.ADDITIVE;
            String modifierKey = "RPGLeveling_" + statName + "_Bonus";
            statMap.removeModifier(statIndex, modifierKey);
            statMap.removeModifier(statIndex, "RPGLeveling_" + statName.toLowerCase() + "_Bonus");
            EntityStatValue statValue = statMap.get(statIndex);
            float currentMax = statValue != null ? statValue.getMax() : 0.0f;
            float currentValue = statValue != null ? statValue.get() : 0.0f;
            StaticModifier modifier = new StaticModifier(Modifier.ModifierTarget.MAX, calculationType, bonusValue);
            statMap.putModifier(statIndex, modifierKey, (Modifier)modifier);
        }
    }

    public void applyStatModifiers(@Nonnull PlayerRef playerRef, @Nonnull LevelingConfig config) {
        World world;
        UUID worldUuid;
        Holder holder = playerRef.getHolder();
        if (holder == null) {
            DEBUG.warning(config, () -> "Cannot apply stat modifiers: Holder is null for " + playerRef.getUsername());
            return;
        }
        PlayerLevelData data = (PlayerLevelData)holder.getComponent(this.playerLevelDataType);
        if (data == null) {
            return;
        }
        Ref entityRef = playerRef.getReference();
        if (entityRef != null && entityRef.isValid() && (worldUuid = playerRef.getWorldUuid()) != null && (world = Universe.get().getWorld(worldUuid)) != null && world.isAlive()) {
            Store store = entityRef.getStore();
            world.execute(() -> this.applyStatModifiers((Ref<EntityStore>)entityRef, (Store<EntityStore>)store, data, config));
        }
    }

    public void recalculateStats(@Nonnull PlayerRef playerRef, @Nonnull LevelingConfig config) {
        this.applyStatModifiers(playerRef, config);
    }

    @Nonnull
    public ComponentType<EntityStore, PlayerLevelData> getPlayerLevelDataType() {
        return this.playerLevelDataType;
    }

    @Nonnull
    public ComponentType<EntityStore, EntityStatMap> getEntityStatMapType() {
        return this.statMapType;
    }

    public void removeAllStatModifiers(@Nonnull Ref<EntityStore> entityRef, @Nonnull Store<EntityStore> store) {
        EntityStatMap statMap = (EntityStatMap)store.getComponent(entityRef, this.statMapType);
        if (statMap == null) {
            return;
        }
        for (String statName : VALID_STATS) {
            int statIndex = this.getStatIndex(statName);
            if (statIndex < 0) continue;
            String modifierKey = "RPGLeveling_" + statName + "_Bonus";
            statMap.removeModifier(statIndex, modifierKey);
        }
    }

    public int getStatLevel(@Nonnull String statName, @Nonnull PlayerLevelData data) {
        return data.getAllocatedPoints(statName);
    }

    public boolean resetAllocatedStats(@Nonnull PlayerRef playerRef, @Nonnull LevelingConfig config) {
        World world;
        UUID worldUuid;
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
                Map<String, Integer> allocatedStats = data.getAllocatedStats();
                int totalAllocated = 0;
                for (Integer points : allocatedStats.values()) {
                    totalAllocated += points.intValue();
                }
                data.setAvailableStatPoints(data.getAvailableStatPoints() + totalAllocated);
                allocatedStats.clear();
                data.getAllocatedStats().clear();
                EntityStatMap statMap = (EntityStatMap)store.getComponent(entityRef, this.statMapType);
                if (statMap != null) {
                    for (String statName : VALID_STATS) {
                        int statIndex = this.getStatIndex(statName);
                        if (statIndex < 0) continue;
                        String modifierKey = "RPGLeveling_" + statName + "_Bonus";
                        statMap.removeModifier(statIndex, modifierKey);
                    }
                }
                store.putComponent(entityRef, this.playerLevelDataType, (Component)data);
                Holder holder = playerRef.getHolder();
                if (holder != null) {
                    holder.putComponent(this.playerLevelDataType, (Component)((PlayerLevelData)data.clone()));
                }
                resultFuture.complete(true);
            });
            try {
                return (Boolean)resultFuture.get();
            }
            catch (Exception e) {
                DEBUG.warning(config, () -> "Error resetting stats on world thread: " + e.getMessage());
                return false;
            }
        }
        Holder holder = playerRef.getHolder();
        if (holder == null) {
            DEBUG.warning(config, () -> "Cannot reset stats: Holder is null for " + playerRef.getUsername());
            return false;
        }
        PlayerLevelData data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
        if (data == null) {
            data = new PlayerLevelData();
        }
        Map<String, Integer> allocatedStats = data.getAllocatedStats();
        int totalAllocated = 0;
        for (Integer points : allocatedStats.values()) {
            totalAllocated += points.intValue();
        }
        data.setAvailableStatPoints(data.getAvailableStatPoints() + totalAllocated);
        allocatedStats.clear();
        data.getAllocatedStats().clear();
        holder.putComponent(this.playerLevelDataType, (Component)data);
        return true;
    }
}
