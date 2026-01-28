/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.components;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nullable;

public class DeathProcessedMarker
implements Component<EntityStore> {
    public static final BuilderCodec<DeathProcessedMarker> CODEC = BuilderCodec.builder(DeathProcessedMarker.class, DeathProcessedMarker::new).build();

    @Nullable
    public Component<EntityStore> clone() {
        return new DeathProcessedMarker();
    }
}
