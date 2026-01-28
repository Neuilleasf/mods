/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue
 *  com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType
 *  com.hypixel.hytale.server.core.universe.world.World
 *  javax.annotation.Nullable
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.natamus.hybrid.data.HybridConstants;
import com.natamus.hybrid.data.HybridEntityStatType;
import com.natamus.hybrid.data.HybridEntityStatValue;
import java.util.List;
import javax.annotation.Nullable;

public class EntityStatFunctions {
    public static boolean shouldGenerateEntityStatData() {
        return HybridConstants.HYBRID_ENTITY_STATS.isEmpty();
    }

    public static void generateEntityStatData(Player player) {
        EntityStatMap statMap = EntityStatFunctions.getPlayerEntityStatMap(player);
        if (statMap != null) {
            EntityStatFunctions.generateEntityStatData(statMap);
        }
    }

    public static void generateEntityStatData(EntityStatMap entityStatMap) {
        if (!EntityStatFunctions.shouldGenerateEntityStatData()) {
            return;
        }
        for (int n = 0; n < entityStatMap.size(); ++n) {
            EntityStatValue entityStatValue = entityStatMap.get(n);
            if (entityStatValue == null) continue;
            String id = entityStatValue.getId();
            HybridEntityStatType hybridType = HybridEntityStatType.fromId(id);
            if (hybridType == null) {
                ((HytaleLogger.Api)HybridConstants.LOGGER.atWarning()).log("Unable to parse '" + id + "' in HybridEntityStatType.");
                continue;
            }
            HybridEntityStatValue hybridValue = new HybridEntityStatValue(id);
            hybridValue.setIndex(n);
            hybridValue.setMin(entityStatValue.getMin());
            hybridValue.setMax(entityStatValue.getMax());
            HybridConstants.HYBRID_ENTITY_STATS.put(hybridType, hybridValue);
        }
    }

    @Nullable
    public static EntityStatMap getPlayerEntityStatMap(Player player) {
        World world = player.getWorld();
        if (world == null) {
            return null;
        }
        Ref playerRef = player.getReference();
        if (playerRef == null) {
            return null;
        }
        return (EntityStatMap)world.getEntityStore().getStore().getComponent(playerRef, EntityStatMap.getComponentType());
    }

    public static List<String> getListOfEntityStatIds() {
        return HybridConstants.HYBRID_ENTITY_STATS.keySet().stream().map(HybridEntityStatType::getId).toList();
    }

    @Nullable
    public static HybridEntityStatValue getHybridEntityStatValue(EntityStatType type) {
        return EntityStatFunctions.getHybridEntityStatValue(type.getId());
    }

    @Nullable
    public static HybridEntityStatValue getHybridEntityStatValue(String id) {
        return EntityStatFunctions.getHybridEntityStatValue(HybridEntityStatType.fromId(id));
    }

    @Nullable
    public static HybridEntityStatValue getHybridEntityStatValue(HybridEntityStatType hybridType) {
        return hybridType != null ? HybridConstants.HYBRID_ENTITY_STATS.get((Object)hybridType) : null;
    }

    public static int getEntityStatIndex(EntityStatType type) {
        return EntityStatFunctions.getEntityStatIndex(type.getId());
    }

    public static int getEntityStatIndex(String id) {
        return EntityStatFunctions.getEntityStatIndex(HybridEntityStatType.fromId(id));
    }

    public static int getEntityStatIndex(HybridEntityStatType hybridType) {
        HybridEntityStatValue value = EntityStatFunctions.getHybridEntityStatValue(hybridType);
        return value != null ? value.getIndex() : -1;
    }

    @Nullable
    public static EntityStatValue getEntityStatValue(EntityStatMap statMap, HybridEntityStatType hybridType) {
        int index = EntityStatFunctions.getEntityStatIndex(hybridType);
        return index >= 0 ? statMap.get(index) : null;
    }

    public static void setStatValue(EntityStatMap statMap, HybridEntityStatType hybridType, float value) {
        int index = EntityStatFunctions.getEntityStatIndex(hybridType);
        if (index >= 0) {
            statMap.setStatValue(index, value);
        }
    }

    public static void setMinStatValue(EntityStatMap statMap, HybridEntityStatType hybridType) {
        float minValue = EntityStatFunctions.getMinStatValue(statMap, hybridType);
        if (minValue >= 0.0f) {
            EntityStatFunctions.setStatValue(statMap, hybridType, minValue);
        }
    }

    public static void setMaxStatValue(EntityStatMap statMap, HybridEntityStatType hybridType) {
        float maxValue = EntityStatFunctions.getMaxStatValue(statMap, hybridType);
        if (maxValue >= 0.0f) {
            EntityStatFunctions.setStatValue(statMap, hybridType, maxValue);
        }
    }

    public static void setMaxStatValuePlusOne(EntityStatMap statMap, HybridEntityStatType hybridType) {
        float maxValue = EntityStatFunctions.getMaxStatValue(statMap, hybridType);
        if (maxValue >= 0.0f) {
            EntityStatFunctions.setStatValue(statMap, hybridType, maxValue + 1.0f);
        }
    }

    public static float getStatValue(EntityStatMap statMap, HybridEntityStatType hybridType) {
        EntityStatValue value = EntityStatFunctions.getEntityStatValue(statMap, hybridType);
        return value != null ? value.get() : -1.0f;
    }

    public static float getMinStatValue(EntityStatMap statMap, HybridEntityStatType hybridType) {
        EntityStatValue value = EntityStatFunctions.getEntityStatValue(statMap, hybridType);
        return value != null ? value.getMin() : -1.0f;
    }

    public static float getMaxStatValue(EntityStatMap statMap, HybridEntityStatType hybridType) {
        EntityStatValue value = EntityStatFunctions.getEntityStatValue(statMap, hybridType);
        return value != null ? value.getMax() : -1.0f;
    }
}
