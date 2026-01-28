/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.component.system.EntityEventSystem
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.natamus.hybrid.event.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.natamus.hybrid.event.callback.EntityBreakBlockCallback;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HybridBreakBlockEventSystem
extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    public HybridBreakBlockEventSystem() {
        super(BreakBlockEvent.class);
    }

    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull BreakBlockEvent e) {
        Ref ref = archetypeChunk.getReferenceTo(index);
        Player player = (Player)store.getComponent(ref, Player.getComponentType());
        if (!EntityBreakBlockCallback.ENTITY_BREAK_BLOCK.invoker().onEntityBreakBlock(player, e.getBlockType(), e.getTargetBlock(), e.getItemInHand())) {
            e.setCancelled(true);
        }
    }

    @Nullable
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
