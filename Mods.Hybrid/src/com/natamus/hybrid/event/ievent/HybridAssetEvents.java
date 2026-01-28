/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.assetstore.event.LoadedAssetsEvent
 *  com.hypixel.hytale.assetstore.map.DefaultAssetMap
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.asset.type.item.config.Item
 */
package com.natamus.hybrid.event.ievent;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.natamus.hybrid.data.HybridConstants;

public class HybridAssetEvents {
    public static void onItemAssetLoad(LoadedAssetsEvent<String, Item, DefaultAssetMap<String, Item>> event) {
        HybridConstants.ITEMS = ((DefaultAssetMap)event.getAssetMap()).getAssetMap();
    }

    public static void onBlockTypeAssetLoad(LoadedAssetsEvent<String, BlockType, DefaultAssetMap<String, BlockType>> event) {
        HybridConstants.BLOCKS = ((DefaultAssetMap)event.getAssetMap()).getAssetMap();
    }
}
