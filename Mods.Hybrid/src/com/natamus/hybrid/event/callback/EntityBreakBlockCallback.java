/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 */
package com.natamus.hybrid.event.callback;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.natamus.hybrid.implementations.event.Event;
import com.natamus.hybrid.implementations.event.EventFactory;

public class EntityBreakBlockCallback {
    public static final Event<On_Entity_Break_Block> ENTITY_BREAK_BLOCK = EventFactory.createArrayBacked(On_Entity_Break_Block.class, callbacks -> (player, targetBlockType, targetBlockVec3i, handStack) -> {
        for (On_Entity_Break_Block callback : callbacks) {
            if (callback.onEntityBreakBlock(player, targetBlockType, targetBlockVec3i, handStack)) continue;
            return false;
        }
        return true;
    });

    private EntityBreakBlockCallback() {
    }

    @FunctionalInterface
    public static interface On_Entity_Break_Block {
        public boolean onEntityBreakBlock(Player var1, BlockType var2, Vector3i var3, ItemStack var4);
    }
}
