/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 */
package org.narwhals.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.logger.HytaleLogger;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public final class PvPConfig {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static volatile PvPConfig instance;
    public boolean enablePvPParryEffects;
    public float parriedStaminaDamage;
    public float parriedStaminaRegenCooldownSeconds;
    public boolean enableSuperKnockback;

    public static void load(File configDir, Gson gson) {
        File diskFile = new File(configDir, "pvp_config.json");
        PvPConfig defaults = PvPConfig.loadDefaults(gson);
        if (!diskFile.exists()) {
            instance = defaults;
            PvPConfig.save(diskFile, gson);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("PvPConfig: Created new pvp_config.json");
        } else {
            instance = PvPConfig.loadAndMerge(diskFile, defaults, gson);
        }
        ((HytaleLogger.Api)LOGGER.atInfo()).log("PvPConfig: Loaded successfully");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static PvPConfig loadDefaults(Gson gson) {
        try (InputStream is = PvPConfig.class.getClassLoader().getResourceAsStream("pvp_defaults.json");){
            if (is == null) {
                ((HytaleLogger.Api)LOGGER.atSevere()).log("PvPConfig: 'pvp_defaults.json' missing from JAR!");
                PvPConfig pvPConfig2 = new PvPConfig();
                return pvPConfig2;
            }
            PvPConfig pvPConfig = (PvPConfig)gson.fromJson((Reader)new InputStreamReader(is), PvPConfig.class);
            return pvPConfig;
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("PvPConfig: Failed to load defaults from JAR");
            e.printStackTrace();
            return new PvPConfig();
        }
    }

    private static PvPConfig loadAndMerge(File diskFile, PvPConfig defaults, Gson gson) {
        PvPConfig pvPConfig;
        FileReader reader = new FileReader(diskFile);
        try {
            JsonObject userJson = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            JsonObject defaultsJson = gson.toJsonTree((Object)defaults).getAsJsonObject();
            boolean updated = false;
            for (String key : defaultsJson.keySet()) {
                if (userJson.has(key)) continue;
                userJson.add(key, defaultsJson.get(key));
                ((HytaleLogger.Api)LOGGER.atInfo()).log("PvPConfig: Added missing field: " + key);
                updated = true;
            }
            PvPConfig config = (PvPConfig)gson.fromJson((JsonElement)userJson, PvPConfig.class);
            if (updated) {
                instance = config;
                PvPConfig.save(diskFile, gson);
            }
            pvPConfig = config;
        }
        catch (Throwable throwable) {
            try {
                try {
                    reader.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                ((HytaleLogger.Api)LOGGER.atWarning()).log("PvPConfig: Failed to read config; using defaults");
                return defaults;
            }
        }
        reader.close();
        return pvPConfig;
    }

    private static void save(File file, Gson gson) {
        try (FileWriter writer = new FileWriter(file);){
            gson.toJson((Object)instance, (Appendable)writer);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("PvPConfig: Failed to save config");
            e.printStackTrace();
        }
    }

    public static PvPConfig get() {
        return instance;
    }
}
