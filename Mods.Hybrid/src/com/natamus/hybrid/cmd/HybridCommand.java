/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.protocol.GameMode
 *  com.hypixel.hytale.server.core.Message
 *  com.hypixel.hytale.server.core.command.system.CommandContext
 *  com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
 *  javax.annotation.Nonnull
 */
package com.natamus.hybrid.cmd;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import javax.annotation.Nonnull;

public class HybridCommand
extends CommandBase {
    public HybridCommand() {
        super("hybrid", "Internal test command for Hybrid.");
        this.setPermissionGroup(GameMode.Creative);
    }

    protected void executeSync(@Nonnull CommandContext ctx) {
        ctx.sendMessage(Message.raw((String)"Hybrid command ran."));
        if (!ctx.isPlayer()) {
            return;
        }
        ctx.sendMessage(Message.raw((String)"Test sent."));
    }
}
