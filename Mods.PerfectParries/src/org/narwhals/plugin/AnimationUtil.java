/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.server.core.asset.type.item.config.Item
 *  com.hypixel.hytale.server.core.asset.type.model.config.Model
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.inventory.Inventory
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nullable
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nullable;

public class AnimationUtil {
    @Nullable
    public static String getMobAnimId(Ref<EntityStore> entityRef, CommandBuffer<EntityStore> commandBuffer) {
        ModelComponent modelComponent = (ModelComponent)commandBuffer.getComponent(entityRef, ModelComponent.getComponentType());
        if (modelComponent == null) {
            return null;
        }
        Model model = modelComponent.getModel();
        if (model == null) {
            return null;
        }
        String modelPath = model.getModel();
        if (modelPath == null) {
            return null;
        }
        int modelsIndex = modelPath.indexOf("/Models");
        if (modelsIndex == -1) {
            return null;
        }
        String beforeModels = modelPath.substring(0, modelsIndex);
        int lastSlashIndex = beforeModels.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            return null;
        }
        return beforeModels.substring(lastSlashIndex + 1);
    }

    @Nullable
    public static String getItemAnimationId(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        Player playerComponent = (Player)store.getComponent(entityRef, Player.getComponentType());
        if (playerComponent == null) {
            return null;
        }
        Inventory inventory = playerComponent.getInventory();
        ItemStack itemInHand = inventory.getItemInHand();
        if (itemInHand == null) {
            return null;
        }
        Item item = itemInHand.getItem();
        return item.getPlayerAnimationsId();
    }
}
