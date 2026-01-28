/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.implementations.event;

import com.natamus.hybrid.implementations.event.EventPhaseData;
import java.util.ArrayList;
import java.util.List;

private static class PhaseSorting.PhaseScc<T> {
    final List<EventPhaseData<T>> phases;
    final List<PhaseSorting.PhaseScc<T>> subsequentSccs = new ArrayList<PhaseSorting.PhaseScc<T>>();
    int inDegree = 0;

    private PhaseSorting.PhaseScc(List<EventPhaseData<T>> phases) {
        this.phases = phases;
    }
}
