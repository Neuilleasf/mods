/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle
 *  com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage
 *  com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem
 *  com.hypixel.hytale.server.core.modules.time.TimeResource
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.narwhals.plugin.EffectUtil;
import org.narwhals.plugin.EntityStaminaComponent;
import org.narwhals.plugin.EntityStaminaConfig;
import org.narwhals.plugin.ParryConfig;
import org.narwhals.plugin.StunComponent;
import org.narwhals.plugin.StunUtil;

public class EntityStaminaDamageSystem
extends DamageEventSystem {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final ComponentType<EntityStore, EntityStaminaComponent> staminaComponentType;
    private final ComponentType<EntityStore, StunComponent> stunComponentType;
    private static final WorldParticle CRIT_PARTICLE = new WorldParticle("Critical", null, 1.0f, null, null);
    private final Vector3d tempTargetPos = new Vector3d();

    public EntityStaminaDamageSystem(ComponentType<EntityStore, EntityStaminaComponent> staminaType, ComponentType<EntityStore, StunComponent> stunType) {
        this.staminaComponentType = staminaType;
        this.stunComponentType = stunType;
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.staminaComponentType;
    }

    public void handle(int index, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, Damage damage) {
        ParryConfig config = ParryConfig.get();
        if (!config.enableEntityStamina) {
            return;
        }
        EntityStaminaComponent staminaComp = (EntityStaminaComponent)chunk.getComponent(index, this.staminaComponentType);
        if (staminaComp == null) {
            return;
        }
        EntityStaminaConfig.StaminaStats stats = EntityStaminaConfig.getStats(staminaComp.getModelAssetId());
        if (stats == null) {
            return;
        }
        TimeResource time = (TimeResource)store.getResource(TimeResource.getResourceType());
        long now = time.getNow().toEpochMilli();
        Ref entityRef = chunk.getReferenceTo(index);
        StunComponent stunComp = (StunComponent)store.getComponent(entityRef, this.stunComponentType);
        if (stunComp != null) {
            this.handleStunnedEntityDamage((Ref<EntityStore>)entityRef, stunComp, staminaComp, damage, chunk, index, store, commandBuffer, config);
            return;
        }
        float incomingDamage = damage.getAmount();
        float staminaLoss = incomingDamage * stats.damaged_stamina_multiplier;
        float current = staminaComp.getCurrentStamina();
        float newStamina = current - staminaLoss;
        staminaComp.setCurrentStamina(newStamina);
        staminaComp.setLastActionTimeMs(now);
    }

    private void handleStunnedEntityDamage(Ref<EntityStore> entityRef, StunComponent stunComp, EntityStaminaComponent staminaComp, Damage damage, ArchetypeChunk<EntityStore> chunk, int index, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, ParryConfig config) {
        if (stunComp.getTimeSinceStunStart() < 0.1f) {
            ((HytaleLogger.Api)LOGGER.atInfo()).log("StaminaDamage: Stun/stagger just applied, ignoring damage wake-up.");
            return;
        }
        if (stunComp.isFullStun()) {
            if (stunComp.isBonusDamageWindowActive()) {
                float newDamage = damage.getAmount() * config.stunnedDamageMultiplier;
                damage.setAmount(newDamage);
                ((HytaleLogger.Api)LOGGER.atInfo()).log("StaminaDamage: CRITICAL HIT! Damage boosted by " + config.stunnedDamageMultiplier + "x to " + newDamage);
                TransformComponent transform = (TransformComponent)chunk.getComponent(index, TransformComponent.getComponentType());
                EffectUtil.spawnCombatParticle(CRIT_PARTICLE, damage, this.tempTargetPos, transform, store, commandBuffer);
            } else {
                ((HytaleLogger.Api)LOGGER.atInfo()).log("StaminaDamage: Hit TOO EARLY. Waking enemy without bonus.");
            }
            StunUtil.wakeUp(entityRef, store, commandBuffer, true);
            staminaComp.setCurrentStamina(staminaComp.getMaxStamina());
            staminaComp.setLastAttacker(null);
        } else {
            ((HytaleLogger.Api)LOGGER.atInfo()).log("StaminaDamage: Stagger interrupted by damage.");
            StunUtil.wakeUp(entityRef, store, commandBuffer, false);
        }
    }
}
