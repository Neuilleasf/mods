/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.assetstore.event.LoadedAssetsEvent
 *  com.hypixel.hytale.event.EventRegistry
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.asset.type.item.config.Item
 *  com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent
 */
package com.natamus.hybrid.event.ievent;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.natamus.hybrid.event.ievent.HybridAssetEvents;
import com.natamus.hybrid.event.ievent.HybridPlayerEvents;

public class _RegisterHybridEvents {
    public static void init(EventRegistry eventRegistry) {
        eventRegistry.registerGlobal(PlayerReadyEvent.class, HybridPlayerEvents::onPlayerReadyEvent);
        eventRegistry.register(LoadedAssetsEvent.class, Item.class, HybridAssetEvents::onItemAssetLoad);
        eventRegistry.register(LoadedAssetsEvent.class, BlockType.class, HybridAssetEvents::onBlockTypeAssetLoad);
    }
}
