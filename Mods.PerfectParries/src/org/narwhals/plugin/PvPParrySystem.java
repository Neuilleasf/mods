/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.modules.entity.stamina.SprintStaminaRegenDelay
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.stamina.SprintStaminaRegenDelay;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.narwhals.plugin.PvPConfig;

public final class PvPParrySystem {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final int STAMINA_INDEX = 9;

    private PvPParrySystem() {
    }

    public static void applyPvPParryEffects(Ref<EntityStore> attackerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        PvPConfig config = PvPConfig.get();
        if (!config.enablePvPParryEffects) {
            return;
        }
        if (config.parriedStaminaDamage <= 0.0f) {
            return;
        }
        Player attackerPlayer = (Player)store.getComponent(attackerRef, Player.getComponentType());
        if (attackerPlayer == null) {
            return;
        }
        PvPParrySystem.applyStaminaDamage(attackerRef, store, commandBuffer, config);
    }

    private static void applyStaminaDamage(Ref<EntityStore> attackerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, PvPConfig config) {
        SprintStaminaRegenDelay delayConfig;
        EntityStatMap statMap = (EntityStatMap)commandBuffer.getComponent(attackerRef, EntityStatMap.getComponentType());
        if (statMap == null) {
            return;
        }
        EntityStatValue staminaStat = statMap.get(9);
        if (staminaStat == null) {
            return;
        }
        float currentStamina = staminaStat.get();
        float newStamina = Math.max(0.0f, currentStamina - config.parriedStaminaDamage);
        statMap.setStatValue(9, newStamina);
        if (config.parriedStaminaRegenCooldownSeconds > 0.0f && (delayConfig = (SprintStaminaRegenDelay)store.getResource(SprintStaminaRegenDelay.getResourceType())).hasDelay()) {
            statMap.setStatValue(delayConfig.getIndex(), -config.parriedStaminaRegenCooldownSeconds);
        }
    }
}
