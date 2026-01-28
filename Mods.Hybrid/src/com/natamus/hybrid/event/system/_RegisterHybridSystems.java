/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ComponentRegistryProxy
 *  com.hypixel.hytale.component.system.ISystem
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 */
package com.natamus.hybrid.event.system;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.natamus.hybrid.event.system.HybridBreakBlockEventSystem;
import com.natamus.hybrid.event.system.HybridPlaceBlockEventSystem;
import com.natamus.hybrid.event.system.HybridPlayerTickSystem;
import com.natamus.hybrid.event.system.HybridUseBlockEventSystem;

public class _RegisterHybridSystems {
    public static void init(ComponentRegistryProxy<EntityStore> entityStoreRegistry) {
        entityStoreRegistry.registerSystem((ISystem)new HybridBreakBlockEventSystem());
        entityStoreRegistry.registerSystem((ISystem)new HybridPlayerTickSystem());
        entityStoreRegistry.registerSystem((ISystem)new HybridPlaceBlockEventSystem());
        entityStoreRegistry.registerSystem((ISystem)new HybridUseBlockEventSystem());
    }
}
