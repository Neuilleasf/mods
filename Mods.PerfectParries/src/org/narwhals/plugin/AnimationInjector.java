/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ComponentAccessor
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.protocol.Rangef
 *  com.hypixel.hytale.server.core.asset.type.model.config.Model
 *  com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset$Animation
 *  com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset$AnimationSet
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnimationInjector {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String WAKE_NAME = "ParriedWake";
    private static final String WAKE_PATH = "Characters/Animations/Damage/Default/PP_Parry_Wake.blockyanim";
    private static final String STUN_NAME = "ParriedStun";
    private static final String STUN_PATH = "Characters/Animations/Damage/Default/PP_Parry_Stunned.blockyanim";
    private static final Set<Integer> injectedModelHashes = new HashSet<Integer>();
    private static Field animationSetMapField;
    private static Field cachedPacketField;
    private static Field isNetworkOutdatedField;

    public static void injectIfNeeded(Ref<EntityStore> entityRef, ComponentAccessor<EntityStore> accessor) {
        if (animationSetMapField == null || cachedPacketField == null || isNetworkOutdatedField == null) {
            return;
        }
        Player playerComponent = (Player)accessor.getComponent(entityRef, Player.getComponentType());
        if (playerComponent != null) {
            ((HytaleLogger.Api)LOGGER.atInfo()).log("AnimationInjector: Entity is a player, canceling stun animation injection.");
            return;
        }
        ModelComponent modelComponent = (ModelComponent)accessor.getComponent(entityRef, ModelComponent.getComponentType());
        if (modelComponent == null) {
            return;
        }
        Model model = modelComponent.getModel();
        if (model == null) {
            return;
        }
        int modelHash = System.identityHashCode(model);
        if (injectedModelHashes.contains(modelHash)) {
            return;
        }
        AnimationInjector.injectIntoModel(model, modelHash, modelComponent);
    }

    private static void injectIntoModel(Model model, int modelHash, ModelComponent modelComponent) {
        try {
            Map currentMap = (Map)animationSetMapField.get(model);
            if (currentMap != null && currentMap.containsKey(WAKE_NAME) && currentMap.containsKey(STUN_NAME)) {
                ((HytaleLogger.Api)LOGGER.atFine()).log("AnimationInjector: Model already has parry animations");
                injectedModelHashes.add(modelHash);
                return;
            }
            ModelAsset.AnimationSet wakeAnimSet = AnimationInjector.createAnimationSet(WAKE_PATH, false);
            ModelAsset.AnimationSet stunAnimSet = AnimationInjector.createAnimationSet(STUN_PATH, true);
            HashMap<String, ModelAsset.AnimationSet> newMap = new HashMap<String, ModelAsset.AnimationSet>();
            if (currentMap != null) {
                newMap.putAll(currentMap);
            }
            newMap.put(WAKE_NAME, wakeAnimSet);
            newMap.put(STUN_NAME, stunAnimSet);
            animationSetMapField.set(model, newMap);
            cachedPacketField.set(model, null);
            isNetworkOutdatedField.set(modelComponent, true);
            injectedModelHashes.add(modelHash);
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("AnimationInjector: Failed to inject animation");
            e.printStackTrace();
        }
    }

    private static ModelAsset.AnimationSet createAnimationSet(String path, boolean looping) {
        ModelAsset.Animation animation = new ModelAsset.Animation(null, path, 1.0f, 0.2f, looping, 1.0f, new int[0], null);
        return new ModelAsset.AnimationSet(new ModelAsset.Animation[]{animation}, new Rangef(2.0f, 10.0f));
    }

    public static void clearCache() {
        injectedModelHashes.clear();
        ((HytaleLogger.Api)LOGGER.atInfo()).log("AnimationInjector: Cache cleared");
    }

    public static int getInjectedCount() {
        return injectedModelHashes.size();
    }

    static {
        try {
            animationSetMapField = Model.class.getDeclaredField("animationSetMap");
            animationSetMapField.setAccessible(true);
            cachedPacketField = Model.class.getDeclaredField("cachedPacket");
            cachedPacketField.setAccessible(true);
            isNetworkOutdatedField = ModelComponent.class.getDeclaredField("isNetworkOutdated");
            isNetworkOutdatedField.setAccessible(true);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("AnimationInjector: Reflection fields initialized");
        }
        catch (NoSuchFieldException e) {
            ((HytaleLogger.Api)LOGGER.atSevere()).log("AnimationInjector: Failed to initialize reflection fields");
            e.printStackTrace();
        }
    }
}
