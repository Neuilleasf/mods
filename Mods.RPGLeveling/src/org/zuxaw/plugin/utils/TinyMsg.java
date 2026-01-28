/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.protocol.MaybeBool
 *  com.hypixel.hytale.server.core.Message
 */
package org.zuxaw.plugin.utils;

import com.hypixel.hytale.protocol.MaybeBool;
import com.hypixel.hytale.server.core.Message;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TinyMsg {
    private static final Pattern TAG_PATTERN = Pattern.compile("<(/?)([a-zA-Z0-9_]+)(?::([^>]+))?>");
    private static final Map<String, Color> NAMED_COLORS = new HashMap<String, Color>();

    public static Message parse(String text) {
        String content;
        Message segmentMsg;
        if (text == null) {
            return Message.raw((String)"");
        }
        if (text.isEmpty()) {
            return Message.raw((String)text);
        }
        Message root = Message.empty();
        ArrayDeque<StyleState> stateStack = new ArrayDeque<StyleState>();
        stateStack.push(new StyleState());
        Matcher matcher = TAG_PATTERN.matcher(text);
        int lastEnd = 0;
        while (matcher.find()) {
            String content2;
            if (matcher.start() > lastEnd && !(content2 = text.substring(lastEnd, matcher.start())).isEmpty()) {
                segmentMsg = TinyMsg.createStyledMessage(content2, (StyleState)stateStack.peek());
                root = Message.join((Message[])new Message[]{root, segmentMsg});
            }
            boolean isClosing = matcher.group(1).equals("/");
            String tagName = matcher.group(2).toLowerCase();
            String tagArg = matcher.group(3);
            if (isClosing) {
                if (stateStack.size() > 1) {
                    stateStack.pop();
                }
            } else {
                StyleState currentState;
                StyleState newState = currentState = (StyleState)stateStack.peek();
                switch (tagName) {
                    case "color": {
                        Color color;
                        if (tagArg == null || (color = TinyMsg.parseColor(tagArg)) == null) break;
                        newState = newState.withColor(color);
                        break;
                    }
                    case "gradient": {
                        String[] colors;
                        if (tagArg == null || (colors = tagArg.split(":")).length < 2) break;
                        Color startColor = TinyMsg.parseColor(colors[0]);
                        Color endColor = TinyMsg.parseColor(colors[1]);
                        if (startColor == null || endColor == null) break;
                        newState = newState.withColor(startColor);
                        break;
                    }
                    case "b": {
                        newState = newState.withBold(true);
                        break;
                    }
                    case "i": {
                        newState = newState.withItalic(true);
                        break;
                    }
                    case "u": {
                        newState = newState.withUnderlined(true);
                        break;
                    }
                    case "mono": {
                        newState = newState.withMono(true);
                        break;
                    }
                    case "reset": {
                        newState = new StyleState();
                    }
                }
                stateStack.push(newState);
            }
            lastEnd = matcher.end();
        }
        if (lastEnd < text.length() && !(content = text.substring(lastEnd)).isEmpty()) {
            segmentMsg = TinyMsg.createStyledMessage(content, (StyleState)stateStack.peek());
            root = Message.join((Message[])new Message[]{root, segmentMsg});
        }
        return root;
    }

    private static Message createStyledMessage(String content, StyleState state) {
        Message msg = Message.raw((String)content);
        if (state.color != null) {
            msg = msg.color(state.color);
        }
        if (state.bold) {
            msg.getFormattedMessage().bold = MaybeBool.True;
        }
        if (state.italic) {
            msg.getFormattedMessage().italic = MaybeBool.True;
        }
        if (state.underlined) {
            msg.getFormattedMessage().underlined = MaybeBool.True;
        }
        return msg;
    }

    private static Color parseColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) {
            return null;
        }
        Color namedColor = NAMED_COLORS.get(colorStr.toLowerCase());
        if (namedColor != null) {
            return namedColor;
        }
        if (colorStr.startsWith("#")) {
            try {
                String hex = colorStr.substring(1);
                if (hex.length() == 6) {
                    int r = Integer.parseInt(hex.substring(0, 2), 16);
                    int g = Integer.parseInt(hex.substring(2, 4), 16);
                    int b = Integer.parseInt(hex.substring(4, 6), 16);
                    return new Color(r, g, b);
                }
                if (hex.length() == 3) {
                    int r = Integer.parseInt(hex.substring(0, 1), 16) * 17;
                    int g = Integer.parseInt(hex.substring(1, 2), 16) * 17;
                    int b = Integer.parseInt(hex.substring(2, 3), 16) * 17;
                    return new Color(r, g, b);
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return null;
    }

    static {
        NAMED_COLORS.put("black", new Color(0, 0, 0));
        NAMED_COLORS.put("dark_blue", new Color(0, 0, 170));
        NAMED_COLORS.put("dark_green", new Color(0, 170, 0));
        NAMED_COLORS.put("dark_aqua", new Color(0, 170, 170));
        NAMED_COLORS.put("dark_red", new Color(170, 0, 0));
        NAMED_COLORS.put("dark_purple", new Color(170, 0, 170));
        NAMED_COLORS.put("gold", new Color(255, 170, 0));
        NAMED_COLORS.put("gray", new Color(170, 170, 170));
        NAMED_COLORS.put("dark_gray", new Color(85, 85, 85));
        NAMED_COLORS.put("blue", new Color(85, 85, 255));
        NAMED_COLORS.put("green", new Color(85, 255, 85));
        NAMED_COLORS.put("aqua", new Color(85, 255, 255));
        NAMED_COLORS.put("red", new Color(255, 85, 85));
        NAMED_COLORS.put("light_purple", new Color(255, 85, 255));
        NAMED_COLORS.put("yellow", new Color(255, 255, 85));
        NAMED_COLORS.put("white", new Color(255, 255, 255));
        NAMED_COLORS.put("lime", new Color(85, 255, 85));
    }

    private record StyleState(Color color, boolean bold, boolean italic, boolean underlined, boolean mono) {
        StyleState() {
            this(null, false, false, false, false);
        }

        StyleState withColor(Color color) {
            return new StyleState(color, this.bold, this.italic, this.underlined, this.mono);
        }

        StyleState withBold(boolean bold) {
            return new StyleState(this.color, bold, this.italic, this.underlined, this.mono);
        }

        StyleState withItalic(boolean italic) {
            return new StyleState(this.color, this.bold, italic, this.underlined, this.mono);
        }

        StyleState withUnderlined(boolean underlined) {
            return new StyleState(this.color, this.bold, this.italic, underlined, this.mono);
        }

        StyleState withMono(boolean mono) {
            return new StyleState(this.color, this.bold, this.italic, this.underlined, mono);
        }
    }
}
