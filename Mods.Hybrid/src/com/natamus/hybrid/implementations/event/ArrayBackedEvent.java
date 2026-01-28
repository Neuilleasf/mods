/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.implementations.event;

import com.natamus.hybrid.implementations.event.Event;
import com.natamus.hybrid.implementations.event.EventIdentifier;
import com.natamus.hybrid.implementations.event.EventPhaseData;
import com.natamus.hybrid.implementations.event.PhaseSorting;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

class ArrayBackedEvent<T>
extends Event<T> {
    private final Function<T[], T> invokerFactory;
    private final Object lock = new Object();
    private T[] handlers;
    private final Map<EventIdentifier, EventPhaseData<T>> phases = new LinkedHashMap<EventIdentifier, EventPhaseData<T>>();
    private final List<EventPhaseData<T>> sortedPhases = new ArrayList<EventPhaseData<T>>();

    ArrayBackedEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.handlers = (Object[])Array.newInstance(type, 0);
        this.update();
    }

    void update() {
        this.invoker = this.invokerFactory.apply((T[][])this.handlers);
    }

    @Override
    public void register(T listener) {
        this.register(DEFAULT_PHASE, listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void register(EventIdentifier phaseEventIdentifier, T listener) {
        Objects.requireNonNull(phaseEventIdentifier, "Tried to register a listener for a null phase!");
        Objects.requireNonNull(listener, "Tried to register a null listener!");
        Object object = this.lock;
        synchronized (object) {
            this.getOrCreatePhase(phaseEventIdentifier, true).addListener(listener);
            this.rebuildInvoker(this.handlers.length + 1);
        }
    }

    private EventPhaseData<T> getOrCreatePhase(EventIdentifier id, boolean sortIfCreate) {
        EventPhaseData<T> phase = this.phases.get(id);
        if (phase == null) {
            phase = new EventPhaseData(id, this.handlers.getClass().getComponentType());
            this.phases.put(id, phase);
            this.sortedPhases.add(phase);
            if (sortIfCreate) {
                PhaseSorting.sortPhases(this.sortedPhases);
            }
        }
        return phase;
    }

    private void rebuildInvoker(int newLength) {
        if (this.sortedPhases.size() == 1) {
            this.handlers = this.sortedPhases.getFirst().listeners;
        } else {
            Object[] newHandlers = (Object[])Array.newInstance(this.handlers.getClass().getComponentType(), newLength);
            int newHandlersIndex = 0;
            for (EventPhaseData<T> existingPhase : this.sortedPhases) {
                int length = existingPhase.listeners.length;
                System.arraycopy(existingPhase.listeners, 0, newHandlers, newHandlersIndex, length);
                newHandlersIndex += length;
            }
            this.handlers = newHandlers;
        }
        this.update();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addPhaseOrdering(EventIdentifier firstPhase, EventIdentifier secondPhase) {
        Objects.requireNonNull(firstPhase, "Tried to add an ordering for a null phase.");
        Objects.requireNonNull(secondPhase, "Tried to add an ordering for a null phase.");
        if (firstPhase.equals(secondPhase)) {
            throw new IllegalArgumentException("Tried to add a phase that depends on itself.");
        }
        Object object = this.lock;
        synchronized (object) {
            EventPhaseData<T> first = this.getOrCreatePhase(firstPhase, false);
            EventPhaseData<T> second = this.getOrCreatePhase(secondPhase, false);
            first.subsequentPhases.add(second);
            second.previousPhases.add(first);
            PhaseSorting.sortPhases(this.sortedPhases);
            this.rebuildInvoker(this.handlers.length);
        }
    }
}
