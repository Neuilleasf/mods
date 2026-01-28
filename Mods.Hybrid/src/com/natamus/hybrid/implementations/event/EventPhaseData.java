/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.implementations.event;

import com.natamus.hybrid.implementations.event.EventIdentifier;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class EventPhaseData<T> {
    final EventIdentifier id;
    T[] listeners;
    final List<EventPhaseData<T>> subsequentPhases = new ArrayList<EventPhaseData<T>>();
    final List<EventPhaseData<T>> previousPhases = new ArrayList<EventPhaseData<T>>();
    int visitStatus = 0;

    EventPhaseData(EventIdentifier id, Class<?> listenerClass) {
        this.id = id;
        this.listeners = (Object[])Array.newInstance(listenerClass, 0);
    }

    void addListener(T listener) {
        int oldLength = this.listeners.length;
        this.listeners = Arrays.copyOf(this.listeners, oldLength + 1);
        this.listeners[oldLength] = listener;
    }
}
