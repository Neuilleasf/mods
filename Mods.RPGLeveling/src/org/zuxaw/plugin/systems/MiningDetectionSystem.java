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
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.protocol.BlockPosition
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.entity.EntityUtils
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
 *  com.hypixel.hytale.server.core.universe.world.meta.BlockState
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  com.hypixel.hytale.server.core.util.TargetUtil
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
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.formulas.Formulas;
import org.zuxaw.plugin.services.StatsService;

public class MiningDetectionSystem
extends EntityTickingSystem<EntityStore> {
    private final StatsService statsService;
    private final LevelingConfig config;
    private final Map<UUID, BlockPosition> lastTargetedBlock = new HashMap<UUID, BlockPosition>();
    @Nonnull
    private final Query<EntityStore> query;

    public MiningDetectionSystem(@Nonnull StatsService statsService, @Nonnull LevelingConfig config) {
        this.statsService = statsService;
        this.config = config;
        this.query = Query.and((Query[])new Query[]{Player.getComponentType()});
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        float speedBonus;
        boolean isWoodcuttingBlock;
        BlockType blockType;
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
        TransformComponent transform = (TransformComponent)archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        if (transform == null) {
            return;
        }
        WorldChunk chunk = transform.getChunk();
        if (chunk == null) {
            return;
        }
        Vector3i targetBlockVec = TargetUtil.getTargetBlock((Ref)entityRef, (double)5.0, commandBuffer);
        if (targetBlockVec == null) {
            this.lastTargetedBlock.remove(playerRef.getUuid());
            return;
        }
        try {
            blockType = chunk.getBlockType(targetBlockVec.x, targetBlockVec.y, targetBlockVec.z);
        }
        catch (Exception e) {
            return;
        }
        if (blockType == null) {
            return;
        }
        BlockPosition currentTargetBlock = new BlockPosition(targetBlockVec.x, targetBlockVec.y, targetBlockVec.z);
        UUID playerUuid = playerRef.getUuid();
        String blockTypeId = blockType.getId().toLowerCase();
        boolean isMiningBlock = blockTypeId.contains("ore") || blockTypeId.contains("stone") || blockTypeId.contains("rock") || blockTypeId.contains("mineral");
        boolean bl = isWoodcuttingBlock = !isMiningBlock && (blockTypeId.contains("wood") || blockTypeId.contains("log") || blockTypeId.contains("plank") || blockTypeId.contains("tree"));
        if (!isMiningBlock && !isWoodcuttingBlock) {
            this.lastTargetedBlock.remove(playerUuid);
            return;
        }
        String statName = isMiningBlock ? "Mining" : "Woodcutting";
        double statValuePerPoint = isMiningBlock ? this.config.getMiningStatValuePerPoint() : this.config.getWoodcuttingStatValuePerPoint();
        int statLevel = this.statsService.getStatLevel(statName, playerData);
        if (statLevel <= 0) {
            this.lastTargetedBlock.remove(playerUuid);
            return;
        }
        BlockPosition lastBlock = this.lastTargetedBlock.get(playerUuid);
        if (lastBlock == null || !lastBlock.equals((Object)currentTargetBlock)) {
            this.lastTargetedBlock.put(playerUuid, currentTargetBlock);
        }
        if ((speedBonus = Formulas.blockDamagePerTickBonus(statLevel, statValuePerPoint, this.config)) > 0.001f) {
            try {
                BlockState blockState = chunk.getState(targetBlockVec.x, targetBlockVec.y, targetBlockVec.z);
                if (blockState != null) {
                    Method damageMethod = blockState.getClass().getMethod("damage", Float.TYPE);
                    damageMethod.invoke((Object)blockState, Float.valueOf(speedBonus));
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}
