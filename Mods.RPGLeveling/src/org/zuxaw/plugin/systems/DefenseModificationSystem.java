/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.SystemGroup
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$EntitySource
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$Source
 *  com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.formulas.Formulas;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.DebugLogger;

public class DefenseModificationSystem
extends DamageEventSystem {
    private static final DebugLogger DEBUG = DebugLogger.forEnclosingClass();
    private final StatsService statsService;
    private final LevelingConfig config;

    public DefenseModificationSystem(@Nonnull StatsService statsService, @Nonnull LevelingConfig config) {
        this.statsService = statsService;
        this.config = config;
    }

    public void handle(int index, ArchetypeChunk<EntityStore> archetypeChunk, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, Damage damage) {
        if (damage.isCancelled()) {
            return;
        }
        Ref targetRef = archetypeChunk.getReferenceTo(index);
        if (targetRef == null || !targetRef.isValid()) {
            return;
        }
        Player targetPlayer = (Player)store.getComponent(targetRef, Player.getComponentType());
        if (targetPlayer == null) {
            return;
        }
        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource)) {
            return;
        }
        Damage.EntitySource entitySource = (Damage.EntitySource)source;
        Ref srcRef = entitySource.getRef();
        PlayerLevelData playerData = (PlayerLevelData)store.getComponent(targetRef, this.statsService.getPlayerLevelDataType());
        if (playerData == null) {
            return;
        }
        int defenseStatLevel = this.statsService.getStatLevel("Defense", playerData);
        if (defenseStatLevel <= 0) {
            return;
        }
        float ratio = Formulas.defenseReductionRatio(defenseStatLevel, this.config);
        float currentAmount = damage.getAmount();
        float actualReduction = currentAmount * ratio;
        float newAmount = currentAmount - actualReduction;
        damage.setAmount(newAmount);
    }

    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        try {
            Class<?> mod = Class.forName("com.hypixel.hytale.server.core.modules.entity.damage.DamageModule");
            Object inst = mod.getMethod("get", new Class[0]).invoke(null, new Object[0]);
            SystemGroup g = (SystemGroup)mod.getMethod("getFilterDamageGroup", new Class[0]).invoke(inst, new Object[0]);
            return g;
        }
        catch (Throwable t) {
            DEBUG.warning(this.config, () -> "[DefenseStat] getGroup() FAILED: " + t.getMessage() + " (system may not run in Filter, defense reduction will not apply)");
            return null;
        }
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
