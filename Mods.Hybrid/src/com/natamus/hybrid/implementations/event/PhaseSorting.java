/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 */
package com.natamus.hybrid.implementations.event;

import com.hypixel.hytale.logger.HytaleLogger;
import com.natamus.hybrid.data.HybridConstants;
import com.natamus.hybrid.implementations.event.EventPhaseData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class PhaseSorting {
    public static boolean ENABLE_CYCLE_WARNING = true;

    static <T> void sortPhases(List<EventPhaseData<T>> sortedPhases) {
        ArrayList<EventPhaseData<T>> toposort = new ArrayList<EventPhaseData<T>>(sortedPhases.size());
        for (EventPhaseData<T> eventPhaseData : sortedPhases) {
            PhaseSorting.forwardVisit(eventPhaseData, null, toposort);
        }
        PhaseSorting.clearStatus(toposort);
        Collections.reverse(toposort);
        IdentityHashMap phaseToScc = new IdentityHashMap();
        for (EventPhaseData eventPhaseData : toposort) {
            if (eventPhaseData.visitStatus != 0) continue;
            ArrayList<EventPhaseData<T>> sccPhases = new ArrayList<EventPhaseData<T>>();
            PhaseSorting.backwardVisit(eventPhaseData, sccPhases);
            sccPhases.sort(Comparator.comparing(p -> p.id));
            PhaseScc phaseScc = new PhaseScc(sccPhases);
            Iterator<Object> iterator = sccPhases.iterator();
            while (iterator.hasNext()) {
                EventPhaseData eventPhaseData2 = (EventPhaseData)iterator.next();
                phaseToScc.put(eventPhaseData2, phaseScc);
            }
        }
        PhaseSorting.clearStatus(toposort);
        for (PhaseScc phaseScc : phaseToScc.values()) {
            for (EventPhaseData eventPhaseData : phaseScc.phases) {
                for (EventPhaseData eventPhaseData3 : eventPhaseData.subsequentPhases) {
                    PhaseScc subsequentScc = (PhaseScc)phaseToScc.get(eventPhaseData3);
                    if (subsequentScc == phaseScc) continue;
                    phaseScc.subsequentSccs.add(subsequentScc);
                    ++subsequentScc.inDegree;
                }
            }
        }
        PriorityQueue<PhaseScc> priorityQueue = new PriorityQueue<PhaseScc>(Comparator.comparing(scc -> scc.phases.getFirst().id));
        sortedPhases.clear();
        for (PhaseScc scc4 : phaseToScc.values()) {
            if (scc4.inDegree != 0) continue;
            priorityQueue.add(scc4);
            scc4.inDegree = -1;
        }
        while (!priorityQueue.isEmpty()) {
            PhaseScc phaseScc = priorityQueue.poll();
            sortedPhases.addAll(phaseScc.phases);
            for (PhaseScc phaseScc2 : phaseScc.subsequentSccs) {
                --phaseScc2.inDegree;
                if (phaseScc2.inDegree != 0) continue;
                priorityQueue.add(phaseScc2);
            }
        }
    }

    private static <T> void forwardVisit(EventPhaseData<T> phase, EventPhaseData<T> parent, List<EventPhaseData<T>> toposort) {
        if (phase.visitStatus == 0) {
            phase.visitStatus = 1;
            for (EventPhaseData data : phase.subsequentPhases) {
                PhaseSorting.forwardVisit(data, phase, toposort);
            }
            toposort.add(phase);
            phase.visitStatus = 2;
        } else if (phase.visitStatus == 1 && ENABLE_CYCLE_WARNING) {
            ((HytaleLogger.Api)HybridConstants.LOGGER.atWarning()).log("Event phase ordering conflict detected.\nEvent phase {} is ordered both before and after event phase {}.", (Object)phase.id, (Object)parent.id);
        }
    }

    private static <T> void clearStatus(List<EventPhaseData<T>> phases) {
        for (EventPhaseData<T> phase : phases) {
            phase.visitStatus = 0;
        }
    }

    private static <T> void backwardVisit(EventPhaseData<T> phase, List<EventPhaseData<T>> sccPhases) {
        if (phase.visitStatus == 0) {
            phase.visitStatus = 1;
            sccPhases.add(phase);
            for (EventPhaseData data : phase.previousPhases) {
                PhaseSorting.backwardVisit(data, sccPhases);
            }
        }
    }

    private static class PhaseScc<T> {
        final List<EventPhaseData<T>> phases;
        final List<PhaseScc<T>> subsequentSccs = new ArrayList<PhaseScc<T>>();
        int inDegree = 0;

        private PhaseScc(List<EventPhaseData<T>> phases) {
            this.phases = phases;
        }
    }
}
