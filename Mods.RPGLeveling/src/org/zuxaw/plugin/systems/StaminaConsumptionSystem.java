/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.component.system.tick.EntityTickingSystem
 *  com.hypixel.hytale.server.core.entity.EntityUtils
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule
 *  com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.formulas.Formulas;
import org.zuxaw.plugin.services.StatsService;

public class StaminaConsumptionSystem
extends EntityTickingSystem<EntityStore> {
    private final StatsService statsService;
    private final LevelingConfig config;
    private final Map<UUID, Float> previousStaminaValues = new ConcurrentHashMap<UUID, Float>();
    @Nonnull
    private final Query<EntityStore> query;

    public StaminaConsumptionSystem(@Nonnull StatsService statsService, @Nonnull LevelingConfig config) {
        this.statsService = statsService;
        this.config = config;
        this.query = Query.and((Query[])new Query[]{Player.getComponentType()});
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Holder holder = EntityUtils.toHolder((int)index, archetypeChunk);
        Player player = (Player)holder.getComponent(Player.getComponentType());
        PlayerRef playerRef = (PlayerRef)holder.getComponent(PlayerRef.getComponentType());
        if (player == null || playerRef == null) {
            return;
        }
        Ref entityRef = archetypeChunk.getReferenceTo(index);
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }
        PlayerLevelData playerData = (PlayerLevelData)store.getComponent(entityRef, this.statsService.getPlayerLevelDataType());
        if (playerData == null) {
            return;
        }
        int allocatedPoints = playerData.getAllocatedPoints("StaminaConsumption");
        if (allocatedPoints <= 0) {
            return;
        }
        EntityStatMap statMap = (EntityStatMap)store.getComponent(entityRef, EntityStatsModule.get().getEntityStatMapComponentType());
        if (statMap == null) {
            return;
        }
        int staminaIndex = DefaultEntityStatTypes.getStamina();
        EntityStatValue staminaValue = statMap.get(staminaIndex);
        if (staminaValue == null) {
            return;
        }
        float currentStamina = staminaValue.get();
        MovementStatesComponent movementComponent = (MovementStatesComponent)store.getComponent(entityRef, MovementStatesComponent.getComponentType());
        boolean isSprinting = false;
        if (movementComponent != null && movementComponent.getMovementStates() != null) {
            boolean bl = isSprinting = movementComponent.getMovementStates().sprinting || movementComponent.getMovementStates().running;
        }
        if (!isSprinting) {
            this.previousStaminaValues.put(playerRef.getUuid(), Float.valueOf(currentStamina));
            return;
        }
        UUID playerUuid = playerRef.getUuid();
        Float previousStamina = this.previousStaminaValues.get(playerUuid);
        boolean isConsuming = previousStamina != null && currentStamina < previousStamina.floatValue();
        this.previousStaminaValues.put(playerUuid, Float.valueOf(currentStamina));
        if (!isConsuming) {
            return;
        }
        float consumptionMultiplier = Formulas.staminaConsumptionMultiplier(allocatedPoints, this.config);
        float naturalDecrease = previousStamina.floatValue() - currentStamina;
        float reducedDecrease = naturalDecrease * consumptionMultiplier;
        float newStamina = Math.max(0.0f, previousStamina.floatValue() - reducedDecrease);
        statMap.setStatValue(staminaIndex, newStamina);
        this.previousStaminaValues.put(playerUuid, Float.valueOf(newStamina));
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}
