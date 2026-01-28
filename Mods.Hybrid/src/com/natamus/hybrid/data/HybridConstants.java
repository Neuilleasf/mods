/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.asset.type.item.config.Item
 */
package com.natamus.hybrid.data;

import com.google.gson.Gson;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.natamus.hybrid.data.HybridEntityStatType;
import com.natamus.hybrid.data.HybridEntityStatValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HybridConstants {
    public static final Gson GSON = new Gson();
    public static final HytaleLogger LOGGER = HytaleLogger.get((String)"Hybrid");
    public static final Random RANDOM = new Random();
    public static Map<String, Item> ITEMS = null;
    public static Map<String, BlockType> BLOCKS = null;
    public static final Map<HybridEntityStatType, HybridEntityStatValue> HYBRID_ENTITY_STATS = new HashMap<HybridEntityStatType, HybridEntityStatValue>();
}
