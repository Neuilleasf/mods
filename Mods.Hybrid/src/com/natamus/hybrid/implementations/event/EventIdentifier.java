/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.implementations.event;

public class EventIdentifier
implements Comparable<EventIdentifier> {
    public final String namespace;
    public final String path;

    public EventIdentifier(String namespaceIn, String pathIn) {
        this.namespace = namespaceIn;
        this.path = pathIn;
    }

    public static EventIdentifier fromNamespaceAndPath(String namespaceIn, String pathIn) {
        return new EventIdentifier(namespaceIn, pathIn);
    }

    @Override
    public int compareTo(EventIdentifier other) {
        int ns = this.namespace.compareTo(other.namespace);
        return ns != 0 ? ns : this.path.compareTo(other.path);
    }

    public String toString() {
        return this.namespace + ":" + this.path;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventIdentifier)) {
            return false;
        }
        EventIdentifier id = (EventIdentifier)o;
        return this.namespace.equals(id.namespace) && this.path.equals(id.path);
    }

    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }
}
