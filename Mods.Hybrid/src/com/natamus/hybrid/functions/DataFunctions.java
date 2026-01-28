/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.Constants
 *  com.hypixel.hytale.server.core.plugin.JavaPlugin
 *  com.hypixel.hytale.server.core.plugin.PluginManager
 *  javax.annotation.Nullable
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.natamus.hybrid.HybridMain;
import com.natamus.hybrid.functions.DevFunctions;
import java.nio.file.Path;
import javax.annotation.Nullable;

public class DataFunctions {
    public static Path getUniversePath() {
        return Constants.UNIVERSE_PATH;
    }

    @Nullable
    public static Path getHybridDataDirectory() {
        return DataFunctions.getHybridDataDirectory(true);
    }

    @Nullable
    public static Path getHybridDataDirectory(boolean fixDevDataPath) {
        return DataFunctions.getModDataDirectory(HybridMain.getInstance(), fixDevDataPath);
    }

    public static Path getModDataDirectory(JavaPlugin javaPlugin) {
        return DataFunctions.getModDataDirectory(javaPlugin, true);
    }

    public static Path getModDataDirectory(JavaPlugin javaPlugin, boolean fixDevDataPath) {
        Path dataDirectory = javaPlugin.getDataDirectory();
        if (fixDevDataPath && DevFunctions.isInDev(javaPlugin)) {
            dataDirectory = PluginManager.MODS_PATH.resolve(javaPlugin.getName().replace(":", "_"));
        }
        return dataDirectory;
    }

    public static String getModIdFromMainClass(JavaPlugin javaPlugin) {
        String name = javaPlugin.getClass().getPackage().getName();
        return name.substring(name.lastIndexOf(46) + 1);
    }
}
