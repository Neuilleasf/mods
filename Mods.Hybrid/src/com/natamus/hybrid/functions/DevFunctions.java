/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.plugin.JavaPlugin
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.natamus.hybrid.functions.DataFunctions;
import java.nio.file.Path;

public class DevFunctions {
    public static boolean isInDev(JavaPlugin javaPlugin) {
        Path dataDirectory = DataFunctions.getModDataDirectory(javaPlugin, false);
        if (dataDirectory == null) {
            return false;
        }
        Path twoUp = dataDirectory.getParent() != null ? dataDirectory.getParent().getParent() : null;
        return twoUp != null && "build".equals(twoUp.getFileName().toString());
    }
}
