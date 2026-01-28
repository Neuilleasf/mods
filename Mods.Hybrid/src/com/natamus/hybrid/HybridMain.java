/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ComponentRegistryProxy
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.server.core.plugin.JavaPlugin
 *  com.hypixel.hytale.server.core.plugin.JavaPluginInit
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package com.natamus.hybrid;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.natamus.hybrid.cmd._RegisterHybridCommands;
import com.natamus.hybrid.config.HybridConfigHandler;
import com.natamus.hybrid.data.HybridConstants;
import com.natamus.hybrid.event.ievent._RegisterHybridEvents;
import com.natamus.hybrid.event.system._RegisterHybridSystems;
import com.natamus.hybrid.functions.ConfigFunctions;
import javax.annotation.Nonnull;

public class HybridMain
extends JavaPlugin {
    private static HybridMain INSTANCE;

    public HybridMain(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        HybridConfigHandler.config = this.withConfig(ConfigFunctions.getConfigName("Hybrid"), new HybridConfigHandler().getCodec());
    }

    protected void setup() {
        ((HytaleLogger.Api)HybridConstants.LOGGER.atInfo()).log("Loading Hybrid version 1.7.");
        ConfigFunctions.processConfigSetup(this, HybridConfigHandler.config);
        _RegisterHybridCommands.init(this.getCommandRegistry());
        _RegisterHybridEvents.init(this.getEventRegistry());
        _RegisterHybridSystems.init((ComponentRegistryProxy<EntityStore>)this.getEntityStoreRegistry());
    }

    public static HybridMain getInstance() {
        return INSTANCE;
    }
}
