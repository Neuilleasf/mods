/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.component.system.tick.EntityTickingSystem
 *  com.hypixel.hytale.server.core.entity.damage.DamageDataComponent
 *  com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.WieldingInteraction
 *  com.hypixel.hytale.server.core.modules.time.TimeResource
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.WieldingInteraction;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.narwhals.plugin.ParryComponent;

public class BlockTrackingSystem
extends EntityTickingSystem<EntityStore> {
    private final ComponentType<EntityStore, ParryComponent> parryComponentType;

    public BlockTrackingSystem(ComponentType<EntityStore, ParryComponent> parryComponentType) {
        this.parryComponentType = parryComponentType;
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.parryComponentType;
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        boolean isBlockingNow;
        DamageDataComponent damageData = (DamageDataComponent)chunk.getComponent(index, DamageDataComponent.getComponentType());
        if (damageData == null) {
            return;
        }
        ParryComponent parryComponent = (ParryComponent)chunk.getComponent(index, this.parryComponentType);
        if (parryComponent == null) {
            return;
        }
        WieldingInteraction currentWielding = damageData.getCurrentWielding();
        boolean bl = isBlockingNow = currentWielding != null;
        if (isBlockingNow != parryComponent.wasBlocking()) {
            if (isBlockingNow) {
                TimeResource timeResource = (TimeResource)store.getResource(TimeResource.getResourceType());
                parryComponent.setBlockStartTimeMs(timeResource.getNow().toEpochMilli());
            }
            parryComponent.setWasBlocking(isBlockingNow);
        }
    }

    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return false;
    }
}
