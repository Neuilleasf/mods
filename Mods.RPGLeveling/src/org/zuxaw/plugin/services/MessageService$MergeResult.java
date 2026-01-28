/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package org.zuxaw.plugin.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

private static class MessageService.MergeResult {
    JsonObject mergedJson;
    List<String> addedKeys = new ArrayList<String>();
    List<String> updatedKeys = new ArrayList<String>();
    List<String> removedKeys = new ArrayList<String>();
    int preservedCount = 0;

    private MessageService.MergeResult() {
    }

    boolean hasChanges() {
        return !this.addedKeys.isEmpty() || !this.updatedKeys.isEmpty() || !this.removedKeys.isEmpty();
    }

    void combine(MessageService.MergeResult other) {
        this.addedKeys.addAll(other.addedKeys);
        this.updatedKeys.addAll(other.updatedKeys);
        this.removedKeys.addAll(other.removedKeys);
        this.preservedCount += other.preservedCount;
    }

    void countNestedKeys(JsonObject obj, String prefix, List<String> keyList) {
        for (String key : obj.keySet()) {
            String fullPath = prefix + "." + key;
            JsonElement element = obj.get(key);
            if (!element.isJsonObject()) continue;
            this.countNestedKeys(element.getAsJsonObject(), fullPath, keyList);
        }
    }
}
