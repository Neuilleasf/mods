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
 *  com.hypixel.hytale.component.system.tick.EntityTickingSystem
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect
 *  com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior
 *  com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent
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
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.narwhals.plugin.EntityStaminaComponent;
import org.narwhals.plugin.EntityStaminaConfig;
import org.narwhals.plugin.StunComponent;

public class EntityStaminaRegenSystem
extends EntityTickingSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final ComponentType<EntityStore, EntityStaminaComponent> staminaComponentType;
    private final ComponentType<EntityStore, StunComponent> stunComponentType;

    public EntityStaminaRegenSystem(ComponentType<EntityStore, EntityStaminaComponent> staminaType, ComponentType<EntityStore, StunComponent> stunType) {
        this.staminaComponentType = staminaType;
        this.stunComponentType = stunType;
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.staminaComponentType;
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        TimeResource time;
        long now;
        long timeSinceAction;
        EntityStaminaConfig.StaminaStats stats;
        EntityStaminaComponent staminaComp = (EntityStaminaComponent)chunk.getComponent(index, this.staminaComponentType);
        if (staminaComp == null) {
            return;
        }
        Ref entityRef = chunk.getReferenceTo(index);
        StunComponent stunComp = (StunComponent)store.getComponent(entityRef, this.stunComponentType);
        if (stunComp != null) {
            return;
        }
        if (staminaComp.getCurrentStamina() < staminaComp.getMaxStamina() && (stats = EntityStaminaConfig.getStats(staminaComp.getModelAssetId())) != null && (timeSinceAction = (now = (time = (TimeResource)store.getResource(TimeResource.getResourceType())).getNow().toEpochMilli()) - staminaComp.getLastActionTimeMs()) > stats.stamina_regen_timer) {
            EffectControllerComponent effectController = (EffectControllerComponent)store.getComponent(entityRef, EffectControllerComponent.getComponentType());
            if (effectController == null) {
                return;
            }
            EntityEffect effect = (EntityEffect)EntityEffect.getAssetMap().getAsset((Object)"PP_Entity_Stamina_Regenerating");
            if (effect == null) {
                return;
            }
            try {
                effectController.addEffect(entityRef, effect, 0.5f, OverlapBehavior.OVERWRITE, commandBuffer);
            }
            catch (Exception e) {
                ((HytaleLogger.Api)LOGGER.atWarning()).log("EntityStaminaRegenSystem: Exception while adding regen effect: " + e.getMessage());
            }
            float regenAmount = stats.stamina_regen_rate * dt;
            float newStamina = Math.min(staminaComp.getMaxStamina(), staminaComp.getCurrentStamina() + regenAmount);
            staminaComp.setCurrentStamina(newStamina);
        }
    }
}
