/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.server.core.util.Config
 */
package com.natamus.hybrid.config;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.util.Config;
import com.natamus.hybrid.functions.ConfigFunctions;

public class HybridConfigHandler {
    public static Config<HybridConfigHandler> config;
    public boolean placeholderBoolean = true;
    private final BuilderCodec<HybridConfigHandler> codec = ConfigFunctions.buildCodec(HybridConfigHandler.class);

    public BuilderCodec<HybridConfigHandler> getCodec() {
        return this.codec;
    }

    public static HybridConfigHandler getConfigHandler() {
        return (HybridConfigHandler)config.get();
    }
}
