/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.assetstore.AssetPack
 *  com.hypixel.hytale.assetstore.map.BlockTypeAssetMap
 *  com.hypixel.hytale.common.plugin.PluginIdentifier
 *  com.hypixel.hytale.component.system.ISystem
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.server.core.asset.AssetModule
 *  com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
 *  com.hypixel.hytale.server.core.plugin.JavaPlugin
 *  com.hypixel.hytale.server.core.plugin.JavaPluginInit
 *  com.hypixel.hytale.server.core.plugin.PluginBase
 *  com.hypixel.hytale.server.core.plugin.PluginManager
 *  javax.annotation.Nonnull
 */
package com.jarhax.eyespy;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.jarhax.eyespy.api.hud.HudProvider;
import com.jarhax.eyespy.api.hud.MultiHudProvider;
import com.jarhax.eyespy.api.hud.VanillaHudProvider;
import com.jarhax.eyespy.impl.hud.PlayerTickSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class EyeSpy
extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final Map<String, String> OWNERSHIP = new HashMap<String, String>();
    public static HudProvider provider = new VanillaHudProvider();

    public EyeSpy(@Nonnull JavaPluginInit init) {
        super(init);
    }

    protected void setup() {
        super.setup();
        this.getEntityStoreRegistry().registerSystem((ISystem)new PlayerTickSystem());
    }

    protected void start() {
        PluginBase plugin = PluginManager.get().getPlugin(PluginIdentifier.fromString((String)"Buuz135:MultipleHUD"));
        if (plugin != null) {
            provider = new MultiHudProvider();
        }
        long start = System.nanoTime();
        OWNERSHIP.clear();
        BlockTypeAssetMap blockTypes = BlockType.getAssetMap();
        for (AssetPack pack : AssetModule.get().getAssetPacks()) {
            String ownerName = pack.getManifest().getGroup() + ":" + pack.getManifest().getName();
            Set blockTypeKeys = blockTypes.getKeysForPack(pack.getName());
            if (blockTypeKeys == null) continue;
            for (String entry : blockTypes.getKeysForPack(pack.getName())) {
                OWNERSHIP.put(entry, ownerName);
            }
        }
        long end = System.nanoTime();
        ((HytaleLogger.Api)LOGGER.atInfo()).log("Determined owners for %d blocks. Took %fms", OWNERSHIP.size(), (float)(end - start) / 1000000.0f);
    }
}
