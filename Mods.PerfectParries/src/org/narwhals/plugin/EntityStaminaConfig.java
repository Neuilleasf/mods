/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 */
package org.narwhals.plugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.logger.HytaleLogger;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EntityStaminaConfig {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Type STATS_MAP_TYPE = new TypeToken<Map<String, StaminaStats>>(){}.getType();
    private static EntityStaminaConfig instance;
    private Map<String, StaminaStats> entityStats = new HashMap<String, StaminaStats>();

    public static void load(File configDir, Gson gson) {
        File diskFile = new File(configDir, "entity_stamina.json");
        Map<String, StaminaStats> defaults = EntityStaminaConfig.loadDefaults(gson);
        instance = new EntityStaminaConfig();
        if (!diskFile.exists()) {
            EntityStaminaConfig.instance.entityStats = new HashMap<String, StaminaStats>(defaults);
            EntityStaminaConfig.save(diskFile, gson);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("EntityStaminaConfig: Created new entity_stamina.json");
        } else {
            EntityStaminaConfig.instance.entityStats = EntityStaminaConfig.loadAndMerge(diskFile, defaults, gson);
        }
        ((HytaleLogger.Api)LOGGER.atInfo()).log("EntityStaminaConfig: Loaded successfully");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Map<String, StaminaStats> loadDefaults(Gson gson) {
        try (InputStream is = EntityStaminaConfig.class.getClassLoader().getResourceAsStream("entity_stamina_defaults.json");){
            if (is == null) {
                ((HytaleLogger.Api)LOGGER.atSevere()).log("EntityStaminaConfig: 'entity_stamina_defaults.json' missing from JAR!");
                HashMap<String, StaminaStats> hashMap = new HashMap<String, StaminaStats>();
                return hashMap;
            }
            Map map = (Map)gson.fromJson((Reader)new InputStreamReader(is), STATS_MAP_TYPE);
            return map;
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("EntityStaminaConfig: Failed to load defaults from JAR");
            e.printStackTrace();
            return new HashMap<String, StaminaStats>();
        }
    }

    private static Map<String, StaminaStats> loadAndMerge(File diskFile, Map<String, StaminaStats> defaults, Gson gson) {
        HashMap<String, StaminaStats> hashMap;
        FileReader reader = new FileReader(diskFile);
        try {
            HashMap<String, StaminaStats> userConfig = (HashMap<String, StaminaStats>)gson.fromJson((Reader)reader, STATS_MAP_TYPE);
            if (userConfig == null) {
                userConfig = new HashMap<String, StaminaStats>();
            }
            boolean updated = false;
            for (String key : defaults.keySet()) {
                if (userConfig.containsKey(key)) continue;
                userConfig.put(key, defaults.get(key));
                ((HytaleLogger.Api)LOGGER.atInfo()).log("EntityStaminaConfig: Added missing entry: " + key);
                updated = true;
            }
            if (updated) {
                EntityStaminaConfig.instance.entityStats = userConfig;
                EntityStaminaConfig.save(diskFile, gson);
            }
            hashMap = userConfig;
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
                ((HytaleLogger.Api)LOGGER.atWarning()).log("EntityStaminaConfig: Failed to read config; using defaults");
                return new HashMap<String, StaminaStats>(defaults);
            }
        }
        reader.close();
        return hashMap;
    }

    private static void save(File file, Gson gson) {
        try (FileWriter writer = new FileWriter(file);){
            gson.toJson(EntityStaminaConfig.instance.entityStats, (Appendable)writer);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("EntityStaminaConfig: Failed to save config");
            e.printStackTrace();
        }
    }

    public static EntityStaminaConfig get() {
        return instance;
    }

    public static StaminaStats getStats(String modelAssetId) {
        if (instance == null) {
            return null;
        }
        return EntityStaminaConfig.instance.entityStats.get(modelAssetId);
    }

    public static class StaminaStats {
        public float max_stamina;
        public long stamina_regen_timer;
        public float stamina_regen_rate;
        public float damaged_stamina_multiplier;
        public float parried_stamina_change;
        public float stagger_chance;
    }
}
