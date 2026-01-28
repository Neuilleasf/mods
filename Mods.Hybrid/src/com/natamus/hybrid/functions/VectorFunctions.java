/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.math.vector.Vector3i
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.universe.world.World
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.natamus.hybrid.functions.BlockFunctions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class VectorFunctions {
    public static List<Vector3i> getAllConnectedBlockPositions(World world, Vector3i startPos, boolean includeStartPos) {
        BlockType blockType = world.getBlockType(startPos);
        if (blockType == null) {
            return List.of();
        }
        return VectorFunctions.getAllConnectedBlockPositions(world, startPos, blockType.getId(), includeStartPos, false);
    }

    public static List<Vector3i> getAllConnectedBlockPositions(World world, Vector3i startPos, String startBlockId, boolean includeStartPos, boolean containSearch) {
        return VectorFunctions.getConnectedDirectionalBlockPositions(world, startPos, startBlockId, includeStartPos, containSearch, false);
    }

    public static List<Vector3i> getBelowConnectedBlockPositions(World world, Vector3i startPos, String startBlockId, boolean includeStartPos, boolean containSearch) {
        return VectorFunctions.getConnectedDirectionalBlockPositions(world, startPos, startBlockId, includeStartPos, containSearch, true);
    }

    public static List<Vector3i> getConnectedDirectionalBlockPositions(World world, Vector3i startPos, String startBlockId, boolean includeStartPos, boolean containSearch, boolean onlyBelow) {
        HashSet<Vector3i> blockPositions = new HashSet<Vector3i>();
        ArrayDeque<Vector3i> blockQueue = new ArrayDeque<Vector3i>();
        blockQueue.add(startPos);
        blockPositions.add(startPos);
        while (!blockQueue.isEmpty()) {
            Vector3i current = (Vector3i)blockQueue.poll();
            Vector3i[] surroundingBlockPositions = !onlyBelow ? VectorFunctions.getAllSurroundingBlocks(current) : VectorFunctions.getBlocksOnSameLevelAndBelow(current);
            for (Vector3i neighbor : surroundingBlockPositions) {
                BlockType type;
                if (blockPositions.contains(neighbor) || (type = world.getBlockType(neighbor)) == null) continue;
                String neighbourBlockId = type.getId();
                if (containSearch ? !neighbourBlockId.contains(startBlockId) : !neighbourBlockId.equals(startBlockId)) continue;
                blockPositions.add(neighbor);
                blockQueue.add(neighbor);
            }
        }
        if (!includeStartPos) {
            blockPositions.remove(startPos);
        }
        return new ArrayList<Vector3i>(blockPositions);
    }

    public static List<Vector3i> getTreeBlockPositions(World world, Vector3i startPos, String startBlockId, boolean includeStartPos, boolean containSearch) {
        HashSet<Vector3i> blockPositions = new HashSet<Vector3i>();
        ArrayDeque<Vector3i> blockQueue = new ArrayDeque<Vector3i>();
        blockQueue.add(startPos);
        blockPositions.add(startPos);
        while (!blockQueue.isEmpty()) {
            Vector3i current = (Vector3i)blockQueue.poll();
            for (Vector3i neighbor : VectorFunctions.getAllSurroundingBlocks(current)) {
                BlockType type;
                if (blockPositions.contains(neighbor) || Math.abs(neighbor.x - startPos.x) > 10 || Math.abs(neighbor.z - startPos.z) > 10 || (type = world.getBlockType(neighbor)) == null) continue;
                String neighbourBlockId = type.getId();
                if (containSearch ? !neighbourBlockId.contains(startBlockId) : !neighbourBlockId.equals(startBlockId)) continue;
                blockPositions.add(neighbor);
                if (BlockFunctions.isLeavesBlock(neighbourBlockId)) continue;
                blockQueue.add(neighbor);
            }
        }
        if (!includeStartPos) {
            blockPositions.remove(startPos);
        }
        return new ArrayList<Vector3i>(blockPositions);
    }

    public static Vector3i[] getHorizontalNeighbours(Vector3i center) {
        Vector3i[] neighbours = new Vector3i[Vector3i.CARDINAL_DIRECTIONS.length];
        for (int i = 0; i < Vector3i.CARDINAL_DIRECTIONS.length; ++i) {
            neighbours[i] = center.clone().add(Vector3i.CARDINAL_DIRECTIONS[i]);
        }
        return neighbours;
    }

    public static Vector3i[] getAdjacentBlocks(Vector3i center) {
        Vector3i[] neighbours = new Vector3i[Vector3i.BLOCK_SIDES.length];
        for (int i = 0; i < Vector3i.BLOCK_SIDES.length; ++i) {
            neighbours[i] = center.clone().add(Vector3i.BLOCK_SIDES[i]);
        }
        return neighbours;
    }

    public static Vector3i[] getAllSurroundingBlocks(Vector3i center) {
        Vector3i[] all = new Vector3i[Vector3i.BLOCK_PARTS[0].length + Vector3i.BLOCK_PARTS[1].length + Vector3i.BLOCK_PARTS[2].length];
        int index = 0;
        for (Vector3i part : Vector3i.BLOCK_PARTS[0]) {
            all[index++] = center.clone().add(part);
        }
        for (Vector3i part : Vector3i.BLOCK_PARTS[1]) {
            all[index++] = center.clone().add(part);
        }
        for (Vector3i part : Vector3i.BLOCK_PARTS[2]) {
            all[index++] = center.clone().add(part);
        }
        return all;
    }

    public static Vector3i[] getBlocksOnSameLevelAndBelow(Vector3i center) {
        Vector3i[] all = new Vector3i[Vector3i.BLOCK_PARTS[0].length + Vector3i.BLOCK_PARTS[2].length];
        int index = 0;
        for (Vector3i part : Vector3i.BLOCK_PARTS[0]) {
            all[index++] = center.clone().add(part);
        }
        for (Vector3i part : Vector3i.BLOCK_PARTS[2]) {
            all[index++] = center.clone().add(part);
        }
        return all;
    }

    public static Vector3i[] getSameLevel3x3(Vector3i center) {
        Vector3i[] result = new Vector3i[8];
        int index = 0;
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                if (x == 0 && z == 0) continue;
                result[index++] = center.clone().add(x, 0, z);
            }
        }
        return result;
    }
}
