/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.command.system.AbstractCommand
 *  com.hypixel.hytale.server.core.command.system.CommandRegistry
 */
package com.natamus.hybrid.cmd;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.natamus.hybrid.cmd.HybridCommand;

public class _RegisterHybridCommands {
    public static void init(CommandRegistry commandRegistry) {
        commandRegistry.registerCommand((AbstractCommand)new HybridCommand());
    }
}
