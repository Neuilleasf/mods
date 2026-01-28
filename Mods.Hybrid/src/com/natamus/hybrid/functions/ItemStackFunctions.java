/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.hypixel.hytale.component.AddReason
 *  com.hypixel.hytale.component.ComponentAccessor
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.math.vector.Vector3f
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.modules.entity.item.ItemComponent
 *  com.hypixel.hytale.server.core.universe.world.World
 *  org.bson.BsonDocument
 */
package com.natamus.hybrid.functions;

import com.google.gson.Gson;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.LinkedHashMap;
import org.bson.BsonDocument;

public class ItemStackFunctions {
    public static void dropItemStack(World world, String itemId, Vector3i dropPos) {
        ItemStackFunctions.dropItemStack(world, new ItemStack(itemId, 1), dropPos);
    }

    public static void dropItemStack(World world, ItemStack itemStack, Vector3i dropPos) {
        Store entityStore = world.getEntityStore().getStore();
        Holder drop = ItemComponent.generateItemDrop((ComponentAccessor)entityStore, (ItemStack)itemStack, (Vector3d)dropPos.toVector3d(), (Vector3f)Vector3f.NaN, (float)0.0f, (float)0.0f, (float)0.0f);
        if (drop == null) {
            return;
        }
        world.execute(() -> {
            try {
                entityStore.addEntity(drop, AddReason.SPAWN);
            }
            catch (IllegalStateException ex) {
                world.execute(() -> entityStore.addEntity(drop, AddReason.SPAWN));
            }
        });
    }

    public static String getItemStackJsonData(ItemStack itemStack) {
        Gson gson = new Gson();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        if (itemStack != null) {
            String itemId = itemStack.getItemId();
            int quantity = itemStack.getQuantity();
            String metadata = "none";
            BsonDocument rawMetadata = itemStack.getMetadata();
            if (rawMetadata != null) {
                metadata = rawMetadata.toJson();
            }
            data.put("itemId", itemId);
            data.put("quantity", quantity);
            data.put("metadata", metadata);
        }
        return gson.toJson(data);
    }
}
