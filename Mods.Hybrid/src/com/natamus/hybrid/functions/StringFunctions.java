/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.functions;

public class StringFunctions {
    public static String toTitleCase(String input) {
        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase()).append(" ");
        }
        return result.toString().trim();
    }

    public static String capitalizeFirst(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String firstNParts(String input, String separator, int count) {
        if (input == null || separator == null || count <= 0) {
            return input;
        }
        int currentIndex = -separator.length();
        for (int i = 0; i < count; ++i) {
            int nextIndex = input.indexOf(separator, currentIndex + separator.length());
            if (nextIndex == -1) {
                return input;
            }
            currentIndex = nextIndex;
        }
        return input.substring(0, currentIndex);
    }
}
