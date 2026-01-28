/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.protocol.InteractionType
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.entity.InteractionContext
 *  com.hypixel.hytale.server.core.entity.entities.Player
 */
package com.natamus.hybrid.event.callback;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;

@FunctionalInterface
public static interface PlayerBlockCallbacks.On_Player_Use_Block {
    public void onPlayerUseBlock(Player var1, BlockType var2, Vector3i var3, InteractionContext var4, InteractionType var5);
}
