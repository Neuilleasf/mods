/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.Message
 */
package com.jarhax.eyespy.api;

import com.hypixel.hytale.server.core.Message;

public class MessageHelpers {
    public static Message tier(int tier) {
        return Message.translation((String)"server.eyespy.tier").param("tier", tier);
    }

    public static Message level(int tier) {
        return Message.translation((String)"server.eyespy.level").param("level", tier);
    }

    public static Message capacity(int capacity) {
        return Message.translation((String)"server.eyespy.capacity").param("capacity", capacity);
    }

    public static Message size(int size) {
        return Message.translation((String)"server.eyespy.size").param("size", size);
    }

    public static Message progress(int current, int max) {
        return MessageHelpers.progress((float)current / (float)max);
    }

    public static Message progress(float amount) {
        return Message.translation((String)"server.eyespy.progress").param("progress", amount);
    }

    public static Message stage(int current, int max) {
        return Message.translation((String)"server.eyespy.stage").param("current", current).param("max", max);
    }
}
