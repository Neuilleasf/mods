/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.implementations.event;

import com.natamus.hybrid.implementations.event.EventIdentifier;

public abstract class Event<T> {
    protected volatile T invoker;
    public static final EventIdentifier DEFAULT_PHASE = EventIdentifier.fromNamespaceAndPath("hybrid", "default");

    public final T invoker() {
        return this.invoker;
    }

    public abstract void register(T var1);

    public void register(EventIdentifier phase, T listener) {
        this.register(listener);
    }

    public void addPhaseOrdering(EventIdentifier firstPhase, EventIdentifier secondPhase) {
    }
}
