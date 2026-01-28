/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.Codec
 *  com.hypixel.hytale.codec.KeyedCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec$Builder
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerLevelData
implements Component<EntityStore> {
    public static final BuilderCodec<PlayerLevelData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlayerLevelData.class, PlayerLevelData::new).append(new KeyedCodec("Level", (Codec)Codec.INTEGER), (data, value) -> {
        data.level = value;
    }, data -> data.level).add()).append(new KeyedCodec("Experience", (Codec)Codec.DOUBLE), (data, value) -> {
        data.experience = value;
    }, data -> data.experience).add()).append(new KeyedCodec("AvailableStatPoints", (Codec)Codec.INTEGER), (data, value) -> {
        data.availableStatPoints = value;
    }, data -> data.availableStatPoints).add()).append(new KeyedCodec("AllocatedStats", (Codec)Codec.STRING), (data, value) -> {
        data.allocatedStats = new HashMap<String, Integer>();
        if (value != null && !value.isEmpty()) {
            String[] entries;
            for (String entry : entries = value.split(",")) {
                String[] parts = entry.split(":", 2);
                if (parts.length != 2) continue;
                try {
                    data.allocatedStats.put(parts[0], Integer.parseInt(parts[1]));
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
    }, data -> {
        if (data.allocatedStats == null || data.allocatedStats.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : data.allocatedStats.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append(entry.getKey()).append(":").append(entry.getValue());
            first = false;
        }
        return sb.toString();
    }).add()).build();
    private int level = 1;
    private double experience = 0.0;
    private int availableStatPoints = 0;
    private Map<String, Integer> allocatedStats = new HashMap<String, Integer>();

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getExperience() {
        return this.experience;
    }

    public void setExperience(double experience) {
        this.experience = experience;
    }

    public void addExperience(double amount) {
        this.experience += amount;
    }

    public int getAvailableStatPoints() {
        return this.availableStatPoints;
    }

    public void setAvailableStatPoints(int availableStatPoints) {
        this.availableStatPoints = availableStatPoints;
    }

    @Nonnull
    public Map<String, Integer> getAllocatedStats() {
        if (this.allocatedStats == null) {
            this.allocatedStats = new HashMap<String, Integer>();
        }
        return this.allocatedStats;
    }

    public int getAllocatedPoints(@Nonnull String statName) {
        if (this.allocatedStats == null) {
            return 0;
        }
        return this.allocatedStats.getOrDefault(statName, 0);
    }

    public void allocatePoints(@Nonnull String statName, int points) {
        if (this.allocatedStats == null) {
            this.allocatedStats = new HashMap<String, Integer>();
        }
        this.allocatedStats.put(statName, points);
    }

    @Nullable
    public Component<EntityStore> clone() {
        PlayerLevelData copy = new PlayerLevelData();
        copy.level = this.level;
        copy.experience = this.experience;
        copy.availableStatPoints = this.availableStatPoints;
        if (this.allocatedStats != null) {
            copy.allocatedStats = new HashMap<String, Integer>(this.allocatedStats);
        }
        return copy;
    }
}
