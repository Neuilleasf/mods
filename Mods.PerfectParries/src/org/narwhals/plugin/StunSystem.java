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
 *  com.hypixel.hytale.protocol.AnimationSlot
 *  com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect
 *  com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior
 *  com.hypixel.hytale.server.core.entity.AnimationUtils
 *  com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent
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
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.narwhals.plugin.EntityStaminaComponent;
import org.narwhals.plugin.StunComponent;
import org.narwhals.plugin.StunUtil;

public class StunSystem
extends EntityTickingSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final ComponentType<EntityStore, StunComponent> stunComponentType;
    private final ComponentType<EntityStore, EntityStaminaComponent> staminaComponentType;

    public StunSystem(ComponentType<EntityStore, StunComponent> stunComponentType, ComponentType<EntityStore, EntityStaminaComponent> staminaComponentType) {
        this.stunComponentType = stunComponentType;
        this.staminaComponentType = staminaComponentType;
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.stunComponentType;
    }

    public void tick(float delta, int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        StunComponent stunComp = (StunComponent)chunk.getComponent(index, this.stunComponentType);
        if (stunComp == null) {
            return;
        }
        if (stunComp.isWakingUp()) {
            return;
        }
        Ref entityRef = chunk.getReferenceTo(index);
        float remaining = stunComp.getTimeRemaining() - delta;
        stunComp.setTimeRemaining(remaining);
        stunComp.addTimeSinceStunStart(delta);
        if (remaining <= 0.025f) {
            this.endStun((Ref<EntityStore>)entityRef, commandBuffer, stunComp.isFullStun());
            return;
        }
        EffectControllerComponent effectController = (EffectControllerComponent)store.getComponent(entityRef, EffectControllerComponent.getComponentType());
        if (effectController != null) {
            try {
                EntityEffect critEffect;
                String effectName = stunComp.isFullStun() ? "PP_Entity_Stunned" : "PP_Entity_Staggered";
                EntityEffect stunEffect = (EntityEffect)EntityEffect.getAssetMap().getAsset((Object)effectName);
                if (stunEffect != null) {
                    effectController.addEffect(entityRef, stunEffect, 0.5f, OverlapBehavior.OVERWRITE, commandBuffer);
                }
                if (stunComp.isFullStun() && stunComp.isBonusDamageWindowActive() && (critEffect = (EntityEffect)EntityEffect.getAssetMap().getAsset((Object)"PP_Entity_Can_Crit")) != null) {
                    effectController.addEffect(entityRef, critEffect, 0.25f, OverlapBehavior.OVERWRITE, commandBuffer);
                }
            }
            catch (Exception e) {
                ((HytaleLogger.Api)LOGGER.atWarning()).log("StunSystem: Exception while adding crit effect: " + e.getMessage());
            }
        }
        StunUtil.enforceStun((Ref<EntityStore>)entityRef, commandBuffer, remaining);
    }

    private void endStun(Ref<EntityStore> entityRef, CommandBuffer<EntityStore> commandBuffer, boolean isStunned) {
        StunUtil.enforceStun(entityRef, commandBuffer, 0.0f);
        if (isStunned) {
            EntityStaminaComponent staminaComp = (EntityStaminaComponent)commandBuffer.getComponent(entityRef, this.staminaComponentType);
            if (staminaComp != null) {
                staminaComp.setCurrentStamina(staminaComp.getMaxStamina());
                staminaComp.setLastAttacker(null);
            }
            AnimationUtils.stopAnimation(entityRef, (AnimationSlot)AnimationSlot.Movement, (boolean)true, commandBuffer);
            AnimationUtils.stopAnimation(entityRef, (AnimationSlot)AnimationSlot.Action, (boolean)true, commandBuffer);
            AnimationUtils.playAnimation(entityRef, (AnimationSlot)AnimationSlot.Action, (String)"ParriedWake", (boolean)true, commandBuffer);
        }
        ((HytaleLogger.Api)LOGGER.atInfo()).log("StunSystem: Woke up naturally");
        commandBuffer.removeComponent(entityRef, this.stunComponentType);
    }
}
