/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.server.core.entity.UUIDComponent
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$EntitySource
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$Source
 *  com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  com.hypixel.hytale.server.npc.entities.NPCEntity
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class DamageTrackingSystem
extends DamageEventSystem {
    private final Map<UUID, UUID> lastAttackers;
    private final Map<UUID, String> entityNames;

    public DamageTrackingSystem(Map<UUID, UUID> lastAttackers, Map<UUID, String> entityNames) {
        this.lastAttackers = lastAttackers;
        this.entityNames = entityNames;
    }

    public void handle(int index, ArchetypeChunk<EntityStore> archetypeChunk, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, Damage damage) {
        Ref targetRef = archetypeChunk.getReferenceTo(index);
        if (targetRef == null || !targetRef.isValid()) {
            return;
        }
        Damage.Source source = damage.getSource();
        if (source instanceof Damage.EntitySource) {
            Damage.EntitySource entitySource = (Damage.EntitySource)source;
            Ref attackerRef = entitySource.getRef();
            if (!attackerRef.isValid()) {
                return;
            }
            Player attackerPlayer = (Player)store.getComponent(attackerRef, Player.getComponentType());
            if (attackerPlayer == null) {
                return;
            }
            UUIDComponent attackerUuidComponent = (UUIDComponent)store.getComponent(attackerRef, UUIDComponent.getComponentType());
            if (attackerUuidComponent == null) {
                return;
            }
            UUID attackerUuid = attackerUuidComponent.getUuid();
            UUIDComponent targetUuidComponent = (UUIDComponent)store.getComponent(targetRef, UUIDComponent.getComponentType());
            if (targetUuidComponent == null) {
                return;
            }
            UUID targetUuid = targetUuidComponent.getUuid();
            String targetNPCId = "Unknown Entity";
            NPCEntity npcEntity = (NPCEntity)store.getComponent(targetRef, NPCEntity.getComponentType());
            if (npcEntity != null) {
                targetNPCId = npcEntity.getNPCTypeId();
                this.entityNames.put(targetUuid, targetNPCId);
            }
            this.lastAttackers.put(targetUuid, attackerUuid);
        }
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
