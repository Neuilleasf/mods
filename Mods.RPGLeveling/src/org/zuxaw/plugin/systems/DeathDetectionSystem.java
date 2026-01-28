/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.SystemGroup
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.component.system.tick.EntityTickingSystem
 *  com.hypixel.hytale.server.core.entity.UUIDComponent
 *  com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule
 *  com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  com.hypixel.hytale.server.core.util.Config
 *  com.hypixel.hytale.server.npc.entities.NPCEntity
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.components.DeathProcessedMarker;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.utils.NotificationHelper;

public class DeathDetectionSystem
extends EntityTickingSystem<EntityStore> {
    private final Map<UUID, UUID> lastAttackers;
    private final Map<UUID, String> entityNames;
    private final LevelingService levelingService;
    private final Config<LevelingConfig> config;
    private final MessageService messageService;
    private final ComponentType<EntityStore, DeathProcessedMarker> deathProcessedMarkerType;

    public DeathDetectionSystem(Map<UUID, UUID> lastAttackers, Map<UUID, String> entityNames, LevelingService levelingService, Config<LevelingConfig> config, MessageService messageService, ComponentType<EntityStore, DeathProcessedMarker> deathProcessedMarkerType) {
        this.lastAttackers = lastAttackers;
        this.entityNames = entityNames;
        this.levelingService = levelingService;
        this.config = config;
        this.messageService = messageService;
        this.deathProcessedMarkerType = deathProcessedMarkerType;
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef deadPlayerRef;
        PlayerRef killer;
        UUID attackerUuid;
        LevelingConfig cfg = (LevelingConfig)this.config.get();
        Ref entityRef = archetypeChunk.getReferenceTo(index);
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }
        if (store.getComponent(entityRef, DeathComponent.getComponentType()) == null) {
            return;
        }
        if (store.getComponent(entityRef, this.deathProcessedMarkerType) != null) {
            return;
        }
        UUIDComponent uuidComponent = (UUIDComponent)store.getComponent(entityRef, UUIDComponent.getComponentType());
        if (uuidComponent == null) {
            return;
        }
        UUID entityUuid = uuidComponent.getUuid();
        String entityName = this.entityNames.remove(entityUuid);
        if (entityName == null) {
            NPCEntity npcEntity = (NPCEntity)store.getComponent(entityRef, NPCEntity.getComponentType());
            String string = entityName = npcEntity != null ? npcEntity.getNPCTypeId() : "Unknown Entity";
        }
        if ((attackerUuid = this.lastAttackers.remove(entityUuid)) != null && (killer = Universe.get().getPlayer(attackerUuid)) != null) {
            double xp;
            int healthIndex;
            EntityStatValue healthStat;
            double maxHealth = 0.0;
            ComponentType statMapType = EntityStatsModule.get().getEntityStatMapComponentType();
            EntityStatMap statMap = (EntityStatMap)store.getComponent(entityRef, statMapType);
            if (statMap != null && (healthStat = statMap.get(healthIndex = DefaultEntityStatTypes.getHealth())) != null) {
                maxHealth = healthStat.getMax();
            }
            if (maxHealth > 0.0 && (xp = this.levelingService.calculateXPFromMaxHealth(maxHealth, cfg)) > 0.0) {
                this.levelingService.addExperience(killer, xp, cfg, commandBuffer);
            }
            String displayName = entityName.replace("_", " ");
            String message = "<color:yellow>" + this.messageService.getNotification("entity_killed", "<color:gold>" + displayName + "</color>") + "</color>";
            NotificationHelper.sendNotification(killer, message);
        }
        if (cfg.isResetLevelOnDeath() && (deadPlayerRef = Universe.get().getPlayer(entityUuid)) != null) {
            this.levelingService.resetPlayerLevelOnDeath(deadPlayerRef, cfg, commandBuffer);
        }
        commandBuffer.addComponent(entityRef, this.deathProcessedMarkerType, (Component)new DeathProcessedMarker());
    }

    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return null;
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return Query.and((Query[])new Query[]{DeathComponent.getComponentType(), Query.not(this.deathProcessedMarkerType)});
    }
}
