/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.functions;

import java.awt.Color;

public class ColourFunctions {
    public static Color getSafeColour(int r, int g, int b) {
        return new Color(ColourFunctions.clampRgb(r), ColourFunctions.clampRgb(g), ColourFunctions.clampRgb(b));
    }

    private static int clampRgb(int n) {
        return Math.max(0, Math.min(255, n));
    }
}
