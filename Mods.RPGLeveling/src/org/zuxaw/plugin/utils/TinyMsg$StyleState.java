/*
 * Decompiled with CFR 0.152.
 */
package org.zuxaw.plugin.utils;

import java.awt.Color;

private record TinyMsg.StyleState(Color color, boolean bold, boolean italic, boolean underlined, boolean mono) {
    TinyMsg.StyleState() {
        this(null, false, false, false, false);
    }

    TinyMsg.StyleState withColor(Color color) {
        return new TinyMsg.StyleState(color, this.bold, this.italic, this.underlined, this.mono);
    }

    TinyMsg.StyleState withBold(boolean bold) {
        return new TinyMsg.StyleState(this.color, bold, this.italic, this.underlined, this.mono);
    }

    TinyMsg.StyleState withItalic(boolean italic) {
        return new TinyMsg.StyleState(this.color, this.bold, italic, this.underlined, this.mono);
    }

    TinyMsg.StyleState withUnderlined(boolean underlined) {
        return new TinyMsg.StyleState(this.color, this.bold, this.italic, underlined, this.mono);
    }

    TinyMsg.StyleState withMono(boolean mono) {
        return new TinyMsg.StyleState(this.color, this.bold, this.italic, this.underlined, mono);
    }
}
