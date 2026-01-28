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
 *  com.hypixel.hytale.server.npc.entities.NPCEntity
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
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.formulas.Formulas;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.DebugLogger;

public class DamageModificationSystem
extends DamageEventSystem {
    private static final DebugLogger DEBUG = DebugLogger.forEnclosingClass();
    private final StatsService statsService;
    private final LevelingConfig config;
    private static final ConcurrentHashMap<Class<?>, DamageDisplayAccessors> DISPLAY_CACHE = new ConcurrentHashMap();

    @Nonnull
    private static DamageDisplayAccessors resolveDisplayAccessors(@Nonnull Class<?> damageClass) {
        Method putMeta = null;
        try {
            putMeta = damageClass.getMethod("putMeta", Object.class, Object.class);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        Method setInitialAmount = null;
        try {
            setInitialAmount = damageClass.getMethod("setInitialAmount", Float.TYPE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        Field initialAmountField = null;
        for (Class<?> c = damageClass; c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField("initialAmount");
                f.setAccessible(true);
                initialAmountField = f;
                break;
            }
            catch (NoSuchFieldException f) {
                continue;
            }
            catch (Throwable t) {
                break;
            }
        }
        return new DamageDisplayAccessors(putMeta, setInitialAmount, initialAmountField);
    }

    public DamageModificationSystem(@Nonnull StatsService statsService, @Nonnull LevelingConfig config) {
        this.statsService = statsService;
        this.config = config;
    }

    public void handle(int index, ArchetypeChunk<EntityStore> archetypeChunk, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, Damage damage) {
        if (damage.isCancelled()) {
            return;
        }
        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource)) {
            return;
        }
        Damage.EntitySource entitySource = (Damage.EntitySource)source;
        Ref attackerRef = entitySource.getRef();
        if (attackerRef == null || !attackerRef.isValid()) {
            return;
        }
        Player attackerPlayer = (Player)store.getComponent(attackerRef, Player.getComponentType());
        if (attackerPlayer == null) {
            return;
        }
        Ref targetRef = archetypeChunk.getReferenceTo(index);
        if (targetRef == null || !targetRef.isValid()) {
            return;
        }
        NPCEntity npcEntity = (NPCEntity)store.getComponent(targetRef, NPCEntity.getComponentType());
        if (npcEntity == null) {
            return;
        }
        PlayerLevelData playerData = (PlayerLevelData)store.getComponent(attackerRef, this.statsService.getPlayerLevelDataType());
        if (playerData == null) {
            return;
        }
        int damageStatLevel = this.statsService.getStatLevel("Damage", playerData);
        if (damageStatLevel <= 0) {
            return;
        }
        float mult = Formulas.damageMultiplier(damageStatLevel, this.config);
        float currentAmount = damage.getAmount();
        float totalAmount = (currentAmount + Formulas.damageFlatBonus(damageStatLevel, this.config)) * mult;
        damage.setAmount(totalAmount);
        this.applyDisplayAmount(damage, currentAmount, totalAmount);
    }

    private void applyDisplayAmount(Damage damage, float currentAmount, float totalAmount) {
        Field cached;
        DamageDisplayAccessors acc = DISPLAY_CACHE.computeIfAbsent(damage.getClass(), DamageModificationSystem::resolveDisplayAccessors);
        if (acc.putMeta != null) {
            try {
                acc.putMeta.invoke((Object)damage, "DisplayAmount", Float.valueOf(totalAmount));
                acc.putMeta.invoke((Object)damage, "Amount", Float.valueOf(totalAmount));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (acc.setInitialAmount != null) {
            try {
                acc.setInitialAmount.invoke((Object)damage, Float.valueOf(totalAmount));
                return;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (acc.initialAmountField != null) {
            try {
                acc.initialAmountField.setFloat(damage, totalAmount);
                return;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if ((cached = acc.matchedAmountField.get()) != null) {
            try {
                if (cached.getType() == Float.TYPE) {
                    cached.setFloat(damage, totalAmount);
                } else {
                    cached.set(damage, Float.valueOf(totalAmount));
                }
                return;
            }
            catch (Throwable ignored) {
                acc.matchedAmountField.compareAndSet(cached, null);
            }
        }
        for (Class<?> c = damage.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (Float.TYPE != f.getType() && Float.class != f.getType()) continue;
                try {
                    float v;
                    f.setAccessible(true);
                    float f2 = v = f.getType() == Float.TYPE ? f.getFloat(damage) : ((Number)f.get(damage)).floatValue();
                    if (!(Math.abs(v - currentAmount) < 0.01f)) continue;
                    if (f.getType() == Float.TYPE) {
                        f.setFloat(damage, totalAmount);
                    } else {
                        f.set(damage, Float.valueOf(totalAmount));
                    }
                    acc.matchedAmountField.compareAndSet(null, f);
                    return;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
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
            return null;
        }
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    private static final class DamageDisplayAccessors {
        @Nullable
        final Method putMeta;
        @Nullable
        final Method setInitialAmount;
        @Nullable
        final Field initialAmountField;
        final AtomicReference<Field> matchedAmountField = new AtomicReference<Object>(null);

        DamageDisplayAccessors(@Nullable Method putMeta, @Nullable Method setInitialAmount, @Nullable Field initialAmountField) {
            this.putMeta = putMeta;
            this.setInitialAmount = setInitialAmount;
            this.initialAmountField = initialAmountField;
        }
    }
}
