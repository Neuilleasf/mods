/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.Message
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  javax.annotation.Nullable
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import java.awt.Color;
import javax.annotation.Nullable;

public class MessageFunctions {
    public static void broadcastMessage(String messageString) {
        MessageFunctions.broadcastMessage(messageString, null);
    }

    public static void broadcastMessage(String messageString, @Nullable Color color) {
        MessageFunctions.broadcastMessage(Message.raw((String)messageString), color);
    }

    public static void broadcastMessage(Message message) {
        MessageFunctions.broadcastMessage(message, null);
    }

    public static void broadcastMessage(Message messageIn, @Nullable Color color) {
        Message message = messageIn;
        if (color != null) {
            message = messageIn.color(color);
        }
        for (PlayerRef universePlayerRef : Universe.get().getPlayers()) {
            universePlayerRef.sendMessage(message);
        }
    }
}
