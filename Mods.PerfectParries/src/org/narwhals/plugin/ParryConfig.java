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

public final class ParryConfig {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static volatile ParryConfig instance;
    public long parryWindowMs;
    public float reflectDamagePercent;
    public float knockbackX;
    public float knockbackY;
    public int parriesToFullSignatureEnergy;
    public long counterattackWindowMs;
    public float counterattackDamageMultiplier;
    public float staggerDurationSeconds;
    public float stunDurationSeconds;
    public boolean enableEntityStamina;
    public float parryStaminaDrainMultiplier;
    public float stunnedDamageMultiplier;

    public static void load(File configDir, Gson gson) {
        File diskFile = new File(configDir, "parry_config.json");
        ParryConfig defaults = ParryConfig.loadDefaults(gson);
        if (!diskFile.exists()) {
            instance = defaults;
            ParryConfig.save(diskFile, gson);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("ParryConfig: Created new parry_config.json");
        } else {
            instance = ParryConfig.loadAndMerge(diskFile, defaults, gson);
        }
        ((HytaleLogger.Api)LOGGER.atInfo()).log("ParryConfig: Loaded successfully");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static ParryConfig loadDefaults(Gson gson) {
        try (InputStream is = ParryConfig.class.getClassLoader().getResourceAsStream("parry_defaults.json");){
            if (is == null) {
                ((HytaleLogger.Api)LOGGER.atSevere()).log("ParryConfig: 'parry_defaults.json' missing from JAR!");
                ParryConfig parryConfig2 = new ParryConfig();
                return parryConfig2;
            }
            ParryConfig parryConfig = (ParryConfig)gson.fromJson((Reader)new InputStreamReader(is), ParryConfig.class);
            return parryConfig;
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("ParryConfig: Failed to load defaults from JAR");
            e.printStackTrace();
            return new ParryConfig();
        }
    }

    private static ParryConfig loadAndMerge(File diskFile, ParryConfig defaults, Gson gson) {
        ParryConfig parryConfig;
        FileReader reader = new FileReader(diskFile);
        try {
            JsonObject userJson = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            JsonObject defaultsJson = gson.toJsonTree((Object)defaults).getAsJsonObject();
            boolean updated = false;
            for (String key : defaultsJson.keySet()) {
                if (userJson.has(key)) continue;
                userJson.add(key, defaultsJson.get(key));
                ((HytaleLogger.Api)LOGGER.atInfo()).log("ParryConfig: Added missing field: " + key);
                updated = true;
            }
            ParryConfig config = (ParryConfig)gson.fromJson((JsonElement)userJson, ParryConfig.class);
            if (updated) {
                instance = config;
                ParryConfig.save(diskFile, gson);
            }
            parryConfig = config;
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
                ((HytaleLogger.Api)LOGGER.atWarning()).log("ParryConfig: Failed to read config; using defaults");
                return defaults;
            }
        }
        reader.close();
        return parryConfig;
    }

    private static void save(File file, Gson gson) {
        try (FileWriter writer = new FileWriter(file);){
            gson.toJson((Object)instance, (Appendable)writer);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("ParryConfig: Failed to save config");
            e.printStackTrace();
        }
    }

    public static ParryConfig get() {
        return instance;
    }
}
