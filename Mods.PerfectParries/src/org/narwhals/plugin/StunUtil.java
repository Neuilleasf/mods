/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.protocol.AnimationSlot
 *  com.hypixel.hytale.server.core.HytaleServer
 *  com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect
 *  com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior
 *  com.hypixel.hytale.server.core.entity.AnimationUtils
 *  com.hypixel.hytale.server.core.entity.InteractionChain
 *  com.hypixel.hytale.server.core.entity.InteractionManager
 *  com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent
 *  com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
 *  com.hypixel.hytale.server.core.modules.interaction.InteractionModule
 *  com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  javax.annotation.Nullable
 */
package org.narwhals.plugin;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.narwhals.plugin.StunComponent;

public class StunUtil {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String STUN_EFFECT = "PP_Entity_Stunned";
    private static final String STAGGER_EFFECT = "PP_Entity_Staggered";
    private static ComponentType<EntityStore, StunComponent> stunComponentType;
    private static Field cooldownHandlerField;
    private static Field cooldownsMapField;
    private static Constructor<?> cooldownConstructor;
    private static MethodHandle remainingCooldownSetter;
    private static MethodHandle remainingCooldownGetter;
    private static boolean initialized;
    private static final Map<String, List<String>> modelInteractionCache;

    public static void init(ComponentType<EntityStore, StunComponent> componentType) {
        stunComponentType = componentType;
        StunUtil.initReflection();
        ((HytaleLogger.Api)LOGGER.atInfo()).log("StunUtil: Initialized.");
    }

    private static synchronized void initReflection() {
        if (initialized) {
            return;
        }
        try {
            cooldownHandlerField = InteractionManager.class.getDeclaredField("cooldownHandler");
            cooldownHandlerField.setAccessible(true);
            Class<?> cooldownHandlerClass = cooldownHandlerField.getType();
            cooldownsMapField = cooldownHandlerClass.getDeclaredField("cooldowns");
            cooldownsMapField.setAccessible(true);
            Class<?> cooldownClassLocal = null;
            for (Class<?> clazz : cooldownHandlerClass.getDeclaredClasses()) {
                if (!clazz.getSimpleName().equals("Cooldown")) continue;
                cooldownClassLocal = clazz;
                break;
            }
            if (cooldownClassLocal != null) {
                Field remainingCooldownField = cooldownClassLocal.getDeclaredField("remainingCooldown");
                remainingCooldownField.setAccessible(true);
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                remainingCooldownSetter = lookup.unreflectSetter(remainingCooldownField);
                remainingCooldownGetter = lookup.unreflectGetter(remainingCooldownField);
                Constructor<?>[] constructorArray = cooldownClassLocal.getDeclaredConstructors();
                int n = constructorArray.length;
                int n2 = 0;
                if (n2 < n) {
                    Constructor<?> c = constructorArray[n2];
                    c.setAccessible(true);
                    cooldownConstructor = c;
                }
            }
            initialized = true;
            ((HytaleLogger.Api)LOGGER.atInfo()).log("StunUtil: Reflection Initialized Successfully.");
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.atSevere()).log("StunUtil: Failed to initialize reflection!");
            e.printStackTrace();
        }
    }

    public static void applyStun(Ref<EntityStore> entityRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, float durationSeconds, @Nullable Ref<EntityStore> stunCauser) {
        if (stunComponentType == null) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("StunUtil: Component type not initialized!");
            return;
        }
        if (entityRef == null || !entityRef.isValid()) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("StunUtil: Cannot apply stun to null or invalid entity ref");
            return;
        }
        StunComponent existing = (StunComponent)store.getComponent(entityRef, stunComponentType);
        if (existing != null) {
            existing.setTimeRemaining(durationSeconds);
            existing.setStunCauser(stunCauser);
            StunUtil.applyEffect(entityRef, store, commandBuffer, durationSeconds, true);
            StunUtil.setInteractionLock(entityRef, commandBuffer, durationSeconds, true, true);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("StunUtil: Extended existing stun to " + durationSeconds + "s");
            return;
        }
        StunUtil.applyEffect(entityRef, store, commandBuffer, durationSeconds, true);
        StunUtil.setInteractionLock(entityRef, commandBuffer, durationSeconds, true, true);
        StunComponent stunComp = new StunComponent(durationSeconds, StunComponent.StunType.STUN);
        stunComp.setStunCauser(stunCauser);
        commandBuffer.addComponent(entityRef, stunComponentType, (Component)stunComp);
        ((HytaleLogger.Api)LOGGER.atInfo()).log("StunUtil: Applied STUN for " + durationSeconds + "s");
        AnimationUtils.playAnimation(entityRef, (AnimationSlot)AnimationSlot.Action, (String)"ParriedStun", (boolean)true, commandBuffer);
    }

    public static void applyStagger(Ref<EntityStore> entityRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, float durationSeconds) {
        if (stunComponentType == null) {
            return;
        }
        if (entityRef == null || !entityRef.isValid()) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("StunUtil: Cannot apply stagger to null or invalid entity ref");
            return;
        }
        StunComponent existing = (StunComponent)store.getComponent(entityRef, stunComponentType);
        if (existing != null) {
            if (existing.isStagger() && durationSeconds > existing.getTimeRemaining()) {
                existing.setTimeRemaining(durationSeconds);
                StunUtil.applyEffect(entityRef, store, commandBuffer, durationSeconds, false);
                StunUtil.setInteractionLock(entityRef, commandBuffer, durationSeconds, true, true);
            }
            return;
        }
        StunUtil.applyEffect(entityRef, store, commandBuffer, durationSeconds, false);
        StunUtil.setInteractionLock(entityRef, commandBuffer, durationSeconds, true, true);
        StunComponent staggerComp = new StunComponent(durationSeconds, StunComponent.StunType.STAGGER);
        commandBuffer.addComponent(entityRef, stunComponentType, (Component)staggerComp);
        ((HytaleLogger.Api)LOGGER.atInfo()).log("StunUtil: Applied STAGGER for " + durationSeconds + "s");
    }

    public static void wakeUp(Ref<EntityStore> entityRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, boolean isStunned) {
        if (stunComponentType == null) {
            return;
        }
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }
        StunComponent stunComp = (StunComponent)store.getComponent(entityRef, stunComponentType);
        if (stunComp == null) {
            ((HytaleLogger.Api)LOGGER.atInfo()).log("StunUtil: wakeUp called but entity has no StunComponent, skipping.");
            return;
        }
        if (stunComp.isWakingUp()) {
            ((HytaleLogger.Api)LOGGER.atInfo()).log("StunUtil: Entity is already being woke up, returning...");
            return;
        }
        stunComp.setWakingUp(true);
        commandBuffer.removeComponent(entityRef, stunComponentType);
        AnimationUtils.stopAnimation(entityRef, (AnimationSlot)AnimationSlot.Movement, (boolean)true, commandBuffer);
        AnimationUtils.stopAnimation(entityRef, (AnimationSlot)AnimationSlot.Action, (boolean)true, commandBuffer);
        if (isStunned) {
            AnimationUtils.playAnimation(entityRef, (AnimationSlot)AnimationSlot.Action, (String)"ParriedWake", (boolean)true, commandBuffer);
            HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> StunUtil.setInteractionLock(entityRef, commandBuffer, 0.0f, false, false), 250L, TimeUnit.MILLISECONDS);
        } else {
            HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> StunUtil.setInteractionLock(entityRef, commandBuffer, 0.0f, false, false), 100L, TimeUnit.MILLISECONDS);
        }
        ((HytaleLogger.Api)LOGGER.atInfo()).log("StunUtil: Waking up entity");
    }

    public static void enforceStun(Ref<EntityStore> entityRef, CommandBuffer<EntityStore> commandBuffer, float remainingDuration) {
        if (!initialized) {
            return;
        }
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }
        StunUtil.setInteractionLock(entityRef, commandBuffer, remainingDuration, false, true);
    }

    public static boolean isStunned(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        if (stunComponentType == null) {
            return false;
        }
        if (entityRef == null || !entityRef.isValid()) {
            return false;
        }
        return store.getComponent(entityRef, stunComponentType) != null;
    }

    public static boolean isFullStun(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        if (stunComponentType == null) {
            return false;
        }
        if (entityRef == null || !entityRef.isValid()) {
            return false;
        }
        StunComponent comp = (StunComponent)store.getComponent(entityRef, stunComponentType);
        return comp != null && comp.isFullStun();
    }

    public static boolean isBonusDamageWindowActive(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        if (stunComponentType == null) {
            return false;
        }
        if (entityRef == null || !entityRef.isValid()) {
            return false;
        }
        StunComponent comp = (StunComponent)store.getComponent(entityRef, stunComponentType);
        return comp != null && comp.isBonusDamageWindowActive();
    }

    @Nullable
    public static StunComponent getStunComponent(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        if (stunComponentType == null) {
            return null;
        }
        if (entityRef == null || !entityRef.isValid()) {
            return null;
        }
        return (StunComponent)store.getComponent(entityRef, stunComponentType);
    }

    private static void applyEffect(Ref<EntityStore> entityRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, float duration, boolean isStunned) {
        EntityEffect effect;
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }
        EffectControllerComponent effectController = (EffectControllerComponent)store.getComponent(entityRef, EffectControllerComponent.getComponentType());
        if (effectController == null) {
            return;
        }
        if (isStunned) {
            effect = (EntityEffect)EntityEffect.getAssetMap().getAsset((Object)STUN_EFFECT);
        } else {
            effect = (EntityEffect)EntityEffect.getAssetMap().getAsset((Object)STAGGER_EFFECT);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("Playing stagger effect");
        }
        if (effect == null) {
            return;
        }
        try {
            effectController.addEffect(entityRef, effect, 0.5f, OverlapBehavior.OVERWRITE, commandBuffer);
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("StunUtil: Exception while adding effect: " + e.getMessage());
        }
    }

    private static void removeEffect(Ref<EntityStore> entityRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }
        EffectControllerComponent effectController = (EffectControllerComponent)store.getComponent(entityRef, EffectControllerComponent.getComponentType());
        if (effectController == null) {
            return;
        }
        try {
            int staggerEffectIndex;
            int stunEffectIndex = EntityEffect.getAssetMap().getIndex((Object)STUN_EFFECT);
            if (stunEffectIndex != Integer.MIN_VALUE) {
                effectController.removeEffect(entityRef, stunEffectIndex, commandBuffer);
            }
            if ((staggerEffectIndex = EntityEffect.getAssetMap().getIndex((Object)STAGGER_EFFECT)) != Integer.MIN_VALUE) {
                effectController.removeEffect(entityRef, staggerEffectIndex, commandBuffer);
            }
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("StunUtil: Exception while removing effects: " + e.getMessage());
        }
    }

    private static void setInteractionLock(Ref<EntityStore> entityRef, CommandBuffer<EntityStore> commandBuffer, float duration, boolean forceCancel, boolean prefillMap) {
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }
        InteractionManager interactionManager = (InteractionManager)commandBuffer.getComponent(entityRef, InteractionModule.get().getInteractionManagerComponent());
        if (interactionManager == null) {
            return;
        }
        try {
            ObjectCollection chains;
            boolean shouldPanicCancel = forceCancel;
            if (cooldownHandlerField != null && remainingCooldownSetter != null) {
                Object handler = cooldownHandlerField.get(interactionManager);
                if (handler == null) {
                    return;
                }
                Map cooldowns = (Map)cooldownsMapField.get(handler);
                if (cooldowns != null) {
                    if (prefillMap) {
                        List<String> allInteractions = StunUtil.getOrCacheInteractions(entityRef, commandBuffer);
                        Iterator iterator = allInteractions.iterator();
                        while (iterator.hasNext()) {
                            String id = (String)iterator.next();
                            if (cooldowns.containsKey(id) || cooldownConstructor == null) continue;
                            try {
                                Object newCooldown = cooldownConstructor.newInstance(Float.valueOf(duration), new float[0], true);
                                cooldowns.put(id, newCooldown);
                            }
                            catch (Exception exception) {}
                        }
                    }
                    for (Object cooldownObj : cooldowns.values()) {
                        float current;
                        if (duration > 0.0f && remainingCooldownGetter != null && (current = remainingCooldownGetter.invoke(cooldownObj)) < 0.2f) {
                            shouldPanicCancel = true;
                        }
                        remainingCooldownSetter.invoke(cooldownObj, duration);
                    }
                }
            }
            if (shouldPanicCancel && duration > 0.0f && !(chains = interactionManager.getChains().values()).isEmpty()) {
                for (InteractionChain chain : new ArrayList(chains)) {
                    interactionManager.cancelChains(chain);
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static List<String> getOrCacheInteractions(Ref<EntityStore> entityRef, CommandBuffer<EntityStore> commandBuffer) {
        ModelComponent modelComp = (ModelComponent)commandBuffer.getComponent(entityRef, ModelComponent.getComponentType());
        if (modelComp == null || modelComp.getModel() == null) {
            return Collections.emptyList();
        }
        String modelAssetId = modelComp.getModel().getModelAssetId();
        if (modelAssetId == null) {
            return Collections.emptyList();
        }
        return modelInteractionCache.computeIfAbsent(modelAssetId, k -> {
            ArrayList<String> found = new ArrayList<String>();
            try {
                IndexedLookupTableAssetMap assetMap = RootInteraction.getAssetMap();
                int assetCount = assetMap.getNextIndex();
                for (int i = 0; i < assetCount; ++i) {
                    String id;
                    RootInteraction root = (RootInteraction)assetMap.getAsset(i);
                    if (root == null || !(id = root.getId()).contains((CharSequence)k) || id.contains("*")) continue;
                    found.add(id);
                }
                ((HytaleLogger.Api)LOGGER.atInfo()).log("StunUtil: Cached " + found.size() + " interactions for " + k);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return found;
        });
    }

    static {
        initialized = false;
        modelInteractionCache = new ConcurrentHashMap<String, List<String>>();
    }
}
