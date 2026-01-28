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
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.component.system.tick.EntityTickingSystem
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  com.hypixel.hytale.server.core.util.Config
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.components.StatsAppliedMarker;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.services.StatsService;

public class StatsApplicationSystem
extends EntityTickingSystem<EntityStore> {
    private final StatsService statsService;
    private final Config<LevelingConfig> config;
    private final ComponentType<EntityStore, StatsAppliedMarker> statsAppliedMarkerType;
    @Nonnull
    private final Query<EntityStore> query;

    public StatsApplicationSystem(@Nonnull StatsService statsService, @Nonnull Config<LevelingConfig> config, @Nonnull ComponentType<EntityStore, StatsAppliedMarker> statsAppliedMarkerType) {
        this.statsService = statsService;
        this.config = config;
        this.statsAppliedMarkerType = statsAppliedMarkerType;
        this.query = Query.and((Query[])new Query[]{Player.getComponentType(), statsService.getPlayerLevelDataType(), Query.not(statsAppliedMarkerType)});
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Ref entityRef = archetypeChunk.getReferenceTo(index);
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }
        PlayerLevelData levelData = (PlayerLevelData)archetypeChunk.getComponent(index, this.statsService.getPlayerLevelDataType());
        if (levelData == null) {
            commandBuffer.addComponent(entityRef, this.statsAppliedMarkerType, (Component)new StatsAppliedMarker());
            return;
        }
        if (levelData.getAllocatedStats().isEmpty()) {
            commandBuffer.addComponent(entityRef, this.statsAppliedMarkerType, (Component)new StatsAppliedMarker());
            return;
        }
        this.statsService.applyStatModifiers((Ref<EntityStore>)entityRef, store, levelData, (LevelingConfig)this.config.get());
        commandBuffer.addComponent(entityRef, this.statsAppliedMarkerType, (Component)new StatsAppliedMarker());
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}
