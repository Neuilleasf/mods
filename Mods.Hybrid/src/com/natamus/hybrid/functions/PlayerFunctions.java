/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ComponentAccessor
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.protocol.GameMode
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent
 *  com.hypixel.hytale.server.core.inventory.Inventory
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nullable
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.natamus.hybrid.functions.InventoryFunctions;
import javax.annotation.Nullable;

public class PlayerFunctions {
    @Nullable
    public static Vector3d getPlayerPosition(Player player) {
        World world = player.getWorld();
        if (world == null) {
            return null;
        }
        return PlayerFunctions.getPlayerPosition((Ref<EntityStore>)player.getReference());
    }

    @Nullable
    public static Vector3d getPlayerPosition(Ref<EntityStore> playerReference) {
        if (playerReference == null) {
            return null;
        }
        TransformComponent transformComponent = (TransformComponent)playerReference.getStore().getComponent(playerReference, TransformComponent.getComponentType());
        if (transformComponent == null) {
            return null;
        }
        return transformComponent.getPosition();
    }

    public static boolean isCrouching(Player player) {
        World world = player.getWorld();
        if (world == null) {
            return false;
        }
        return PlayerFunctions.isCrouching((Ref<EntityStore>)player.getReference());
    }

    public static boolean isCrouching(Ref<EntityStore> playerReference) {
        if (playerReference == null) {
            return false;
        }
        MovementStatesComponent movementStatesComponent = (MovementStatesComponent)playerReference.getStore().getComponent(playerReference, MovementStatesComponent.getComponentType());
        if (movementStatesComponent == null) {
            return false;
        }
        return movementStatesComponent.getMovementStates().crouching;
    }

    public static boolean isJoiningTheWorldForTheFirstTime(Player player) {
        return player.isFirstSpawn() && InventoryFunctions.isInventoryEmpty(player);
    }

    public static void decreaseHeldItemDurability(Player player, double amount, boolean ignoreGamemode) {
        World world = player.getWorld();
        if (world == null) {
            return;
        }
        PlayerFunctions.decreaseHeldItemDurability(player, world, amount, ignoreGamemode);
    }

    public static void decreaseHeldItemDurability(Player player, World world, double amount, boolean ignoreGamemode) {
        Ref playerRef = player.getReference();
        if (playerRef == null) {
            return;
        }
        if (!ignoreGamemode && player.getGameMode().equals((Object)GameMode.Creative)) {
            return;
        }
        Inventory playerInventory = player.getInventory();
        ItemStack handStack = playerInventory.getItemInHand();
        if (handStack == null) {
            return;
        }
        player.updateItemStackDurability(playerRef, handStack, playerInventory.getHotbar(), (int)playerInventory.getActiveHotbarSlot(), -amount, (ComponentAccessor)world.getEntityStore().getStore());
    }
}
