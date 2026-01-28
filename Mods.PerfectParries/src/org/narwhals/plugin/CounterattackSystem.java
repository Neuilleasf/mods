/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Archetype
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.SystemGroup
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.protocol.SoundCategory
 *  com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle
 *  com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent
 *  com.hypixel.hytale.server.core.entity.damage.DamageDataComponent
 *  com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$EntitySource
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$Source
 *  com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem
 *  com.hypixel.hytale.server.core.modules.entity.damage.DamageModule
 *  com.hypixel.hytale.server.core.modules.time.TimeResource
 *  com.hypixel.hytale.server.core.universe.world.SoundUtil
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.narwhals.plugin.EffectUtil;
import org.narwhals.plugin.ParryComponent;
import org.narwhals.plugin.ParryConfig;

public class CounterattackSystem
extends DamageEventSystem {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final WorldParticle COUNTER_PARTICLE = new WorldParticle("Counter", null, 1.0f, null, null);
    private final Vector3d tempTargetPos = new Vector3d();
    private final ComponentType<EntityStore, ParryComponent> parryComponentType;

    public CounterattackSystem(ComponentType<EntityStore, ParryComponent> parryComponentType) {
        this.parryComponentType = parryComponentType;
    }

    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }

    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        TransformComponent transformComponent;
        long timeSinceAction;
        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource)) {
            return;
        }
        Damage.EntitySource entitySource = (Damage.EntitySource)source;
        Ref attackerRef = entitySource.getRef();
        if (!attackerRef.isValid()) {
            return;
        }
        ParryComponent attackerParry = (ParryComponent)commandBuffer.getComponent(attackerRef, this.parryComponentType);
        if (attackerParry == null || !attackerParry.isCounterattackReady()) {
            return;
        }
        Ref targetRef = chunk.getReferenceTo(index);
        Ref<EntityStore> parriedRef = attackerParry.getParriedEntityRef();
        if (parriedRef == null || !parriedRef.equals(targetRef)) {
            return;
        }
        ParryConfig config = ParryConfig.get();
        TimeResource timeResource = (TimeResource)store.getResource(TimeResource.getResourceType());
        long nowMs = timeResource.getNow().toEpochMilli();
        long perfectParryTimeMs = attackerParry.getPerfectParryTimeMs();
        long checkTimeMs = nowMs;
        DamageDataComponent attackerDamageData = (DamageDataComponent)commandBuffer.getComponent(attackerRef, DamageDataComponent.getComponentType());
        if (attackerDamageData != null) {
            long lastInputMs;
            checkTimeMs = lastInputMs = attackerDamageData.getLastCombatAction().toEpochMilli();
        }
        if ((timeSinceAction = checkTimeMs - perfectParryTimeMs) < 0L || timeSinceAction > config.counterattackWindowMs) {
            attackerParry.setCounterattackReady(false);
            attackerParry.setParriedEntityRef(null);
            return;
        }
        float originalDamage = damage.getAmount();
        float boostedDamage = originalDamage * config.counterattackDamageMultiplier;
        damage.setAmount(boostedDamage);
        if (boostedDamage > 0.0f && (transformComponent = (TransformComponent)chunk.getComponent(index, TransformComponent.getComponentType())) != null) {
            EffectUtil.spawnCombatParticle(COUNTER_PARTICLE, damage, this.tempTargetPos, transformComponent, store, commandBuffer);
            Vector3d pos = transformComponent.getPosition();
            int soundIndex = SoundEvent.getAssetMap().getIndex((Object)"SFX_Light_Melee_T1_Impact");
            SoundUtil.playSoundEvent3d((int)soundIndex, (SoundCategory)SoundCategory.SFX, (double)pos.x, (double)pos.y, (double)pos.z, (float)5.0f, (float)1.0f, store);
        }
        attackerParry.setCounterattackReady(false);
        attackerParry.setParriedEntityRef(null);
    }
}
