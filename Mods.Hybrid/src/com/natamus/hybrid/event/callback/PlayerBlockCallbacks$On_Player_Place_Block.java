/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 */
package com.natamus.hybrid.event.callback;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;

@FunctionalInterface
public static interface PlayerBlockCallbacks.On_Player_Place_Block {
    public boolean onPlayerPlaceBlock(Player var1, ItemStack var2, Vector3i var3, RotationTuple var4);
}
