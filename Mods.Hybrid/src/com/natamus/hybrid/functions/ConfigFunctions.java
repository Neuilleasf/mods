/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.Codec
 *  com.hypixel.hytale.codec.KeyedCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec$Builder
 *  com.hypixel.hytale.codec.builder.BuilderField$FieldBuilder
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.server.core.plugin.JavaPlugin
 *  com.hypixel.hytale.server.core.util.Config
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.builder.BuilderField;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.util.Config;
import com.natamus.hybrid.data.HybridConstants;
import com.natamus.hybrid.functions.DataFunctions;
import com.natamus.hybrid.functions.StringFunctions;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.function.Supplier;

public class ConfigFunctions {
    public static String getConfigName(String modName) {
        return modName.replace(" ", "").toLowerCase() + ".config";
    }

    public static void processConfigSetup(JavaPlugin javaPlugin, Config<?> config) {
        String pluginName = javaPlugin.getName();
        if (!pluginName.contains(":")) {
            return;
        }
        String modName = pluginName.split(":")[1];
        String configName = ConfigFunctions.getConfigName(modName);
        Path dataDirectory = DataFunctions.getModDataDirectory(javaPlugin, false);
        if (dataDirectory == null) {
            return;
        }
        File mainConfigFile = dataDirectory.resolve(configName + ".json").toFile();
        if (!mainConfigFile.exists()) {
            config.save();
            ((HytaleLogger.Api)HybridConstants.LOGGER.atInfo()).log(modName + "'s config saved.");
        } else {
            config.load();
            ((HytaleLogger.Api)HybridConstants.LOGGER.atInfo()).log(modName + "'s config loaded.");
        }
        File exampleConfigFile = dataDirectory.resolve(configName + ".example.json").toFile();
        if (exampleConfigFile.exists()) {
            exampleConfigFile.delete();
        }
    }

    public static <T> BuilderCodec<T> buildCodec(Class<T> clazz) {
        BuilderCodec.Builder builder = BuilderCodec.builder(clazz, ConfigFunctions.getDefaultSupplier(clazz));
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            KeyedCodec<?> keyedCodec = ConfigFunctions.getKeyedCodec(field);
            if (keyedCodec == null) continue;
            BuilderField.FieldBuilder fieldBuilder = builder.append(keyedCodec, (t, v, extraInfo) -> {
                try {
                    field.set(t, v);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }, (t, extraInfo) -> {
                try {
                    return field.get(t);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            fieldBuilder.add();
        }
        return builder.build();
    }

    private static KeyedCodec<?> getKeyedCodec(Field field) {
        KeyedCodec keyedCodec = null;
        String keyName = StringFunctions.capitalizeFirst(field.getName());
        if (field.getType() == String.class) {
            keyedCodec = new KeyedCodec(keyName, (Codec)Codec.STRING);
        } else if (field.getType() == Integer.class || field.getType() == Integer.TYPE) {
            keyedCodec = new KeyedCodec(keyName, (Codec)Codec.INTEGER);
        } else if (field.getType() == Double.class || field.getType() == Double.TYPE) {
            keyedCodec = new KeyedCodec(keyName, (Codec)Codec.DOUBLE);
        } else if (field.getType() == Boolean.class || field.getType() == Boolean.TYPE) {
            keyedCodec = new KeyedCodec(keyName, (Codec)Codec.BOOLEAN);
        }
        return keyedCodec;
    }

    private static <T> Supplier<T> getDefaultSupplier(Class<T> clazz) {
        return () -> {
            try {
                return clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to instantiate " + String.valueOf(clazz), e);
            }
        };
    }
}
