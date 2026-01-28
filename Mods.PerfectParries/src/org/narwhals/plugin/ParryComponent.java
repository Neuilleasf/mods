/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParryComponent
implements Component<EntityStore> {
    private long blockStartTimeMs = Long.MIN_VALUE;
    private boolean wasBlocking = false;
    private long perfectParryTimeMs = Long.MIN_VALUE;
    private boolean counterattackReady = false;
    private long lastSuccessfulParryTimeMs = 0L;
    @Nullable
    private Ref<EntityStore> parriedEntityRef = null;

    public long getBlockStartTimeMs() {
        return this.blockStartTimeMs;
    }

    public void setBlockStartTimeMs(long timeMs) {
        this.blockStartTimeMs = timeMs;
    }

    public boolean wasBlocking() {
        return this.wasBlocking;
    }

    public void setWasBlocking(boolean blocking) {
        this.wasBlocking = blocking;
    }

    public long getPerfectParryTimeMs() {
        return this.perfectParryTimeMs;
    }

    public void setPerfectParryTimeMs(long timeMs) {
        this.perfectParryTimeMs = timeMs;
    }

    public boolean isCounterattackReady() {
        return this.counterattackReady;
    }

    public void setCounterattackReady(boolean ready) {
        this.counterattackReady = ready;
    }

    public long getLastSuccessfulParryTimeMs() {
        return this.lastSuccessfulParryTimeMs;
    }

    public void setLastSuccessfulParryTimeMs(long timeMs) {
        this.lastSuccessfulParryTimeMs = timeMs;
    }

    @Nullable
    public Ref<EntityStore> getParriedEntityRef() {
        return this.parriedEntityRef;
    }

    public void setParriedEntityRef(@Nullable Ref<EntityStore> ref) {
        this.parriedEntityRef = ref;
    }

    @Nonnull
    public ParryComponent clone() {
        ParryComponent c = new ParryComponent();
        c.blockStartTimeMs = this.blockStartTimeMs;
        c.wasBlocking = this.wasBlocking;
        c.perfectParryTimeMs = this.perfectParryTimeMs;
        c.counterattackReady = this.counterattackReady;
        c.parriedEntityRef = this.parriedEntityRef;
        c.lastSuccessfulParryTimeMs = this.lastSuccessfulParryTimeMs;
        return c;
    }
}
