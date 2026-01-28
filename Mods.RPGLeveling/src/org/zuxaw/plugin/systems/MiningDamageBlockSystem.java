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
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.formulas.Formulas;
import org.zuxaw.plugin.services.StatsService;

public class MiningDamageBlockSystem
extends EntityEventSystem<EntityStore, DamageBlockEvent> {
    private static final ConcurrentHashMap<Class<?>, DamageAccessors> ACCESSORS_CACHE = new ConcurrentHashMap();
    private final StatsService statsService;
    private final LevelingConfig config;

    @Nonnull
    private static DamageAccessors resolveAccessors(@Nonnull Class<?> eventClass) {
        try {
            Method get = eventClass.getMethod("getAmount", new Class[0]);
            Method set = eventClass.getMethod("setAmount", Float.TYPE);
            return new DamageAccessors(get, set, "setAmount");
        }
        catch (NoSuchMethodException get) {
            String[] getNames = new String[]{"getDamage", "getDamageAmount", "getBlockDamage"};
            String[] setNames = new String[]{"setDamage", "setDamageAmount", "setBlockDamage"};
            Method get2 = null;
            for (String name : getNames) {
                try {
                    Method m;
                    get2 = m = eventClass.getMethod(name, new Class[0]);
                    break;
                }
                catch (NoSuchMethodException noSuchMethodException) {
                }
            }
            Method set = null;
            String via = "unknown";
            for (String name : setNames) {
                try {
                    Method m;
                    set = m = eventClass.getMethod(name, Float.TYPE);
                    via = name;
                    break;
                }
                catch (NoSuchMethodException noSuchMethodException) {
                }
            }
            return new DamageAccessors(get2, set, via);
        }
    }

    public MiningDamageBlockSystem(@Nonnull StatsService statsService, @Nonnull LevelingConfig config) {
        super(DamageBlockEvent.class);
        this.statsService = statsService;
        this.config = config;
    }

    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull DamageBlockEvent event) {
        double statValuePerPoint;
        String statName;
        boolean isWoodcuttingBlock;
        Ref entityRef = archetypeChunk.getReferenceTo(index);
        Player player = (Player)store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            return;
        }
        PlayerLevelData playerData = (PlayerLevelData)store.getComponent(entityRef, this.statsService.getPlayerLevelDataType());
        if (playerData == null) {
            return;
        }
        BlockType blockType = event.getBlockType();
        if (blockType == null) {
            return;
        }
        String blockTypeId = blockType.getId();
        String lowerId = blockTypeId.toLowerCase();
        boolean isMiningBlock = lowerId.contains("ore") || lowerId.contains("stone") || lowerId.contains("rock") || lowerId.contains("mineral");
        boolean bl = isWoodcuttingBlock = lowerId.contains("wood") || lowerId.contains("log") || lowerId.contains("plank") || lowerId.contains("tree");
        if (isMiningBlock && isWoodcuttingBlock) {
            isWoodcuttingBlock = false;
        }
        if (!isMiningBlock && !isWoodcuttingBlock) {
            return;
        }
        if (isMiningBlock) {
            statName = "Mining";
            statValuePerPoint = this.config.getMiningStatValuePerPoint();
        } else {
            statName = "Woodcutting";
            statValuePerPoint = this.config.getWoodcuttingStatValuePerPoint();
        }
        int statLevel = this.statsService.getStatLevel(statName, playerData);
        if (statLevel <= 0) {
            return;
        }
        double damageMultiplier = Formulas.blockDamageMultiplier(statLevel, statValuePerPoint, this.config);
        DamageAccessors acc = ACCESSORS_CACHE.computeIfAbsent(event.getClass(), MiningDamageBlockSystem::resolveAccessors);
        float currentAmount = 0.0f;
        boolean hasAmount = false;
        if (acc.getAmount != null) {
            try {
                Object result = acc.getAmount.invoke((Object)event, new Object[0]);
                if (result instanceof Number) {
                    currentAmount = ((Number)result).floatValue();
                    hasAmount = true;
                }
            }
            catch (ReflectiveOperationException e) {
                ACCESSORS_CACHE.remove(event.getClass());
                hasAmount = false;
            }
        }
        if (hasAmount && acc.setAmount != null) {
            float newAmount = (float)((double)currentAmount * damageMultiplier);
            try {
                acc.setAmount.invoke((Object)event, Float.valueOf(newAmount));
            }
            catch (ReflectiveOperationException e) {
                ACCESSORS_CACHE.remove(event.getClass());
            }
        }
    }

    @Nullable
    public Query<EntityStore> getQuery() {
        return Query.and((Query[])new Query[]{Player.getComponentType()});
    }

    private static final class DamageAccessors {
        @Nullable
        final Method getAmount;
        @Nullable
        final Method setAmount;
        @Nonnull
        final String via;

        DamageAccessors(@Nullable Method getAmount, @Nullable Method setAmount, @Nonnull String via) {
            this.getAmount = getAmount;
            this.setAmount = setAmount;
            this.via = via;
        }
    }
}
