/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.protocol.InteractionType
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple
 *  com.hypixel.hytale.server.core.entity.InteractionContext
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 */
package com.natamus.hybrid.event.callback;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.natamus.hybrid.implementations.event.Event;
import com.natamus.hybrid.implementations.event.EventFactory;

public class PlayerBlockCallbacks {
    public static final Event<On_Player_Use_Block> USE_BLOCK = EventFactory.createArrayBacked(On_Player_Use_Block.class, callbacks -> (player, blockType, targetBlockVec3i, interactionContext, interactionType) -> {
        for (On_Player_Use_Block callback : callbacks) {
            callback.onPlayerUseBlock(player, blockType, targetBlockVec3i, interactionContext, interactionType);
        }
    });
    public static final Event<On_Player_Place_Block> PLACE_BLOCK = EventFactory.createArrayBacked(On_Player_Place_Block.class, callbacks -> (player, itemStack, targetBlockVec3i, rotationTuple) -> {
        for (On_Player_Place_Block callback : callbacks) {
            if (callback.onPlayerPlaceBlock(player, itemStack, targetBlockVec3i, rotationTuple)) continue;
            return false;
        }
        return true;
    });

    private PlayerBlockCallbacks() {
    }

    @FunctionalInterface
    public static interface On_Player_Place_Block {
        public boolean onPlayerPlaceBlock(Player var1, ItemStack var2, Vector3i var3, RotationTuple var4);
    }

    @FunctionalInterface
    public static interface On_Player_Use_Block {
        public void onPlayerUseBlock(Player var1, BlockType var2, Vector3i var3, InteractionContext var4, InteractionType var5);
    }
}
