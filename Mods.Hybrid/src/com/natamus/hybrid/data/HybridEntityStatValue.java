/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.data;

public class HybridEntityStatValue {
    private final String id;
    private int index;
    private float min;
    private float max;

    public HybridEntityStatValue(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getMin() {
        return this.min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return this.max;
    }

    public void setMax(float max) {
        this.max = max;
    }
}
