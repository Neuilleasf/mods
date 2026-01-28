/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockBreakingDropType
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockGathering
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.CustomModelTexture
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.modules.item.ItemModule
 *  com.hypixel.hytale.server.core.universe.world.World
 *  javax.annotation.Nullable
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockBreakingDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockGathering;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.CustomModelTexture;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.natamus.hybrid.functions.ItemStackFunctions;
import java.util.ArrayList;
import javax.annotation.Nullable;

public class BlockFunctions {
    public static void dropBlock(World world, Vector3i blockPos, boolean dropBreakingDropType) {
        BlockFunctions.dropBlock(world, blockPos, blockPos, dropBreakingDropType);
    }

    public static void dropBlock(World world, Vector3i blockPos, @Nullable Vector3i dropPos, boolean dropBreakingDropType) {
        BlockFunctions.dropBlock(world, blockPos, dropPos, dropBreakingDropType, 256);
    }

    public static void dropBlock(World world, Vector3i blockPos, @Nullable Vector3i dropPos, boolean dropBreakingDropType, int setBlockSettings) {
        if (dropPos == null) {
            dropPos = blockPos;
        }
        BlockType blockType = world.getBlockType(blockPos);
        world.breakBlock(blockPos.x, blockPos.y, blockPos.z, setBlockSettings);
        if (blockType != null) {
            BlockBreakingDropType blockBreakingDropType;
            BlockGathering blockGathering;
            ArrayList<ItemStack> itemsToDrop = new ArrayList<ItemStack>();
            String dropBlockId = blockType.getId();
            boolean usedItemModule = false;
            if (dropBreakingDropType && (blockGathering = blockType.getGathering()) != null && (blockBreakingDropType = blockGathering.getBreaking()) != null) {
                if (blockBreakingDropType.getItemId() != null) {
                    dropBlockId = blockBreakingDropType.getItemId();
                } else if (blockBreakingDropType.getDropListId() != null) {
                    String dropListId = blockBreakingDropType.getDropListId();
                    ItemModule itemModule = ItemModule.get();
                    if (itemModule.isEnabled()) {
                        for (int i = 0; i < blockBreakingDropType.getQuantity(); ++i) {
                            itemsToDrop.addAll(itemModule.getRandomItemDrops(dropListId));
                        }
                    }
                    usedItemModule = true;
                }
            }
            if (!usedItemModule && dropBlockId != null) {
                itemsToDrop.add(new ItemStack(dropBlockId));
            }
            for (ItemStack itemStack : itemsToDrop) {
                ItemStackFunctions.dropItemStack(world, itemStack, dropPos);
            }
        }
    }

    public static boolean isOre(BlockType blockType) {
        CustomModelTexture[] customModelTextures = blockType.getCustomModelTexture();
        if (customModelTextures == null) {
            return false;
        }
        for (CustomModelTexture customModelTexture : customModelTextures) {
            String texture = customModelTexture.getTexture();
            if (!texture.contains("Ores") && !texture.contains("Ore_")) continue;
            return true;
        }
        return false;
    }

    public static boolean isLog(BlockType blockType) {
        return BlockFunctions.isLog(blockType, true);
    }

    public static boolean isLog(BlockType blockType, boolean includeBranches) {
        String blockId = blockType.getId();
        return blockId.endsWith("_Trunk") || blockId.endsWith("_Log") || includeBranches && BlockFunctions.isBranch(blockId);
    }

    public static boolean isBranch(BlockType blockType) {
        return BlockFunctions.isBranch(blockType.getId());
    }

    public static boolean isBranch(String blockId) {
        return blockId.contains("_Branch_");
    }

    public static boolean isLeavesBlock(BlockType blockType) {
        return BlockFunctions.isLeavesBlock(blockType.getId());
    }

    public static boolean isLeavesBlock(String blockId) {
        return blockId.contains("_Leaves");
    }
}
