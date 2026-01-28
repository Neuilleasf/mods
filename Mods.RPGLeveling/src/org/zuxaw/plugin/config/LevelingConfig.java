/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.Codec
 *  com.hypixel.hytale.codec.KeyedCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec$Builder
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public class LevelingConfig {
    public static final BuilderCodec<LevelingConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LevelingConfig.class, LevelingConfig::new).append(new KeyedCodec("MaxLevel", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxLevel = value;
    }, (config, extraInfo) -> config.maxLevel).add()).append(new KeyedCodec("RateExp", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.rateExp = value;
    }, (config, extraInfo) -> config.rateExp).add()).append(new KeyedCodec("BaseXP", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.baseXP = value;
    }, (config, extraInfo) -> config.baseXP).add()).append(new KeyedCodec("LevelBaseXP", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.levelBaseXP = value;
    }, (config, extraInfo) -> config.levelBaseXP).add()).append(new KeyedCodec("LevelOffset", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.levelOffset = value;
    }, (config, extraInfo) -> config.levelOffset).add()).append(new KeyedCodec("StatPointsPerLevel", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.statPointsPerLevel = value;
    }, (config, extraInfo) -> config.statPointsPerLevel).add()).append(new KeyedCodec("StatValuePerPoint", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.statValuePerPoint = value;
    }, (config, extraInfo) -> config.statValuePerPoint).add()).append(new KeyedCodec("EnableHUD", (Codec)Codec.BOOLEAN), (config, value, extraInfo) -> {
        config.enableHUD = value;
    }, (config, extraInfo) -> config.enableHUD).add()).append(new KeyedCodec("Debug", (Codec)Codec.BOOLEAN), (config, value, extraInfo) -> {
        config.debug = value;
    }, (config, extraInfo) -> config.debug).add()).append(new KeyedCodec("ResetLevelOnDeath", (Codec)Codec.BOOLEAN), (config, value, extraInfo) -> {
        config.resetLevelOnDeath = value;
    }, (config, extraInfo) -> config.resetLevelOnDeath).add()).append(new KeyedCodec("DamageStatValuePerPoint", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.damageStatValuePerPoint = value;
    }, (config, extraInfo) -> config.damageStatValuePerPoint).add()).append(new KeyedCodec("MiningStatValuePerPoint", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.miningStatValuePerPoint = value;
    }, (config, extraInfo) -> config.miningStatValuePerPoint).add()).append(new KeyedCodec("WoodcuttingStatValuePerPoint", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.woodcuttingStatValuePerPoint = value;
    }, (config, extraInfo) -> config.woodcuttingStatValuePerPoint).add()).append(new KeyedCodec("DefenseStatValuePerPoint", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.defenseStatValuePerPoint = value;
    }, (config, extraInfo) -> config.defenseStatValuePerPoint).add()).append(new KeyedCodec("DefenseMaxReductionRatio", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.defenseMaxReductionRatio = value;
    }, (config, extraInfo) -> config.defenseMaxReductionRatio).add()).append(new KeyedCodec("BlockDamageScalingDivisor", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.blockDamageScalingDivisor = value;
    }, (config, extraInfo) -> config.blockDamageScalingDivisor).add()).append(new KeyedCodec("MiningPerTickDivisor", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.miningPerTickDivisor = value;
    }, (config, extraInfo) -> config.miningPerTickDivisor).add()).append(new KeyedCodec("StaminaConsumptionReductionPerPoint", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.staminaConsumptionReductionPerPoint = value;
    }, (config, extraInfo) -> config.staminaConsumptionReductionPerPoint).add()).append(new KeyedCodec("StaminaRegenSpeedMultiplierPerPoint", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> {
        config.staminaRegenSpeedMultiplierPerPoint = value;
    }, (config, extraInfo) -> config.staminaRegenSpeedMultiplierPerPoint).add()).append(new KeyedCodec("MaxStatPointsHealth", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsHealth = value;
    }, (config, extraInfo) -> config.maxStatPointsHealth).add()).append(new KeyedCodec("MaxStatPointsStamina", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsStamina = value;
    }, (config, extraInfo) -> config.maxStatPointsStamina).add()).append(new KeyedCodec("MaxStatPointsMana", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsMana = value;
    }, (config, extraInfo) -> config.maxStatPointsMana).add()).append(new KeyedCodec("MaxStatPointsAmmo", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsAmmo = value;
    }, (config, extraInfo) -> config.maxStatPointsAmmo).add()).append(new KeyedCodec("MaxStatPointsOxygen", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsOxygen = value;
    }, (config, extraInfo) -> config.maxStatPointsOxygen).add()).append(new KeyedCodec("MaxStatPointsStaminaRegenSpeed", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsStaminaRegenSpeed = value;
    }, (config, extraInfo) -> config.maxStatPointsStaminaRegenSpeed).add()).append(new KeyedCodec("MaxStatPointsDamage", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsDamage = value;
    }, (config, extraInfo) -> config.maxStatPointsDamage).add()).append(new KeyedCodec("MaxStatPointsMining", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsMining = value;
    }, (config, extraInfo) -> config.maxStatPointsMining).add()).append(new KeyedCodec("MaxStatPointsWoodcutting", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsWoodcutting = value;
    }, (config, extraInfo) -> config.maxStatPointsWoodcutting).add()).append(new KeyedCodec("MaxStatPointsDefense", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsDefense = value;
    }, (config, extraInfo) -> config.maxStatPointsDefense).add()).append(new KeyedCodec("MaxStatPointsStaminaConsumption", (Codec)Codec.INTEGER), (config, value, extraInfo) -> {
        config.maxStatPointsStaminaConsumption = value;
    }, (config, extraInfo) -> config.maxStatPointsStaminaConsumption).add()).append(new KeyedCodec("BlacklistedStats", (Codec)Codec.STRING), (config, value, extraInfo) -> {
        config.blacklistedStats = new ArrayList<String>();
        if (value != null && !value.isEmpty()) {
            String[] stats;
            for (String stat : stats = value.split(",")) {
                String trimmed = stat.trim();
                if (trimmed.isEmpty()) continue;
                config.blacklistedStats.add(trimmed);
            }
        }
        if (value == null && config.blacklistedStats.isEmpty()) {
            config.blacklistedStats = new ArrayList<String>(Arrays.asList("StaminaRegenDelay"));
        }
    }, (config, extraInfo) -> {
        if (config.blacklistedStats == null || config.blacklistedStats.isEmpty()) {
            return "";
        }
        return String.join((CharSequence)",", config.blacklistedStats);
    }).add()).build();
    private int maxLevel = 100;
    private double rateExp = 1.0;
    private double baseXP = 3.0;
    private double levelBaseXP = 50.0;
    private double levelOffset = 0.0;
    private int statPointsPerLevel = 3;
    private double statValuePerPoint = 1.0;
    private boolean enableHUD = true;
    private boolean debug = false;
    private boolean resetLevelOnDeath = false;
    private double damageStatValuePerPoint = 1.0;
    private double miningStatValuePerPoint = 0.5;
    private double woodcuttingStatValuePerPoint = 0.5;
    private double defenseStatValuePerPoint = 1.0;
    private double defenseMaxReductionRatio = 0.8;
    private double blockDamageScalingDivisor = 25.0;
    private double miningPerTickDivisor = 100.0;
    private double staminaConsumptionReductionPerPoint = 0.01;
    private double staminaRegenSpeedMultiplierPerPoint = 0.18;
    private int maxStatPointsHealth = 50;
    private int maxStatPointsStamina = 50;
    private int maxStatPointsMana = 50;
    private int maxStatPointsAmmo = 4;
    private int maxStatPointsOxygen = 50;
    private int maxStatPointsStaminaRegenSpeed = 50;
    private int maxStatPointsDamage = 50;
    private int maxStatPointsMining = 50;
    private int maxStatPointsWoodcutting = 50;
    private int maxStatPointsDefense = 50;
    private int maxStatPointsStaminaConsumption = 50;
    private List<String> blacklistedStats = new ArrayList<String>(Arrays.asList(new String[0]));

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public double getRateExp() {
        return this.rateExp;
    }

    public double getBaseXP() {
        return this.baseXP;
    }

    public double getLevelBaseXP() {
        return this.levelBaseXP;
    }

    public double getLevelOffset() {
        return this.levelOffset;
    }

    public int getStatPointsPerLevel() {
        return this.statPointsPerLevel;
    }

    public double getStatValuePerPoint() {
        return this.statValuePerPoint;
    }

    public void setRateExp(double rateExp) {
        this.rateExp = rateExp;
    }

    public void setBaseXP(double baseXP) {
        this.baseXP = baseXP;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setLevelBaseXP(double levelBaseXP) {
        this.levelBaseXP = levelBaseXP;
    }

    public void setLevelOffset(double levelOffset) {
        this.levelOffset = levelOffset;
    }

    public void setStatPointsPerLevel(int statPointsPerLevel) {
        this.statPointsPerLevel = statPointsPerLevel;
    }

    public void setStatValuePerPoint(double statValuePerPoint) {
        this.statValuePerPoint = statValuePerPoint;
    }

    public boolean isEnableHUD() {
        return this.enableHUD;
    }

    public void setEnableHUD(boolean enableHUD) {
        this.enableHUD = enableHUD;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isResetLevelOnDeath() {
        return this.resetLevelOnDeath;
    }

    public void setResetLevelOnDeath(boolean resetLevelOnDeath) {
        this.resetLevelOnDeath = resetLevelOnDeath;
    }

    public double getDamageStatValuePerPoint() {
        return this.damageStatValuePerPoint;
    }

    public void setDamageStatValuePerPoint(double damageStatValuePerPoint) {
        this.damageStatValuePerPoint = damageStatValuePerPoint;
    }

    public double getMiningStatValuePerPoint() {
        return this.miningStatValuePerPoint;
    }

    public void setMiningStatValuePerPoint(double miningStatValuePerPoint) {
        this.miningStatValuePerPoint = miningStatValuePerPoint;
    }

    public double getWoodcuttingStatValuePerPoint() {
        return this.woodcuttingStatValuePerPoint;
    }

    public void setWoodcuttingStatValuePerPoint(double woodcuttingStatValuePerPoint) {
        this.woodcuttingStatValuePerPoint = woodcuttingStatValuePerPoint;
    }

    public double getDefenseStatValuePerPoint() {
        return this.defenseStatValuePerPoint;
    }

    public void setDefenseStatValuePerPoint(double defenseStatValuePerPoint) {
        this.defenseStatValuePerPoint = defenseStatValuePerPoint;
    }

    public double getDefenseMaxReductionRatio() {
        return this.defenseMaxReductionRatio;
    }

    public void setDefenseMaxReductionRatio(double v) {
        this.defenseMaxReductionRatio = v;
    }

    public double getBlockDamageScalingDivisor() {
        return this.blockDamageScalingDivisor;
    }

    public void setBlockDamageScalingDivisor(double v) {
        this.blockDamageScalingDivisor = v;
    }

    public double getMiningPerTickDivisor() {
        return this.miningPerTickDivisor;
    }

    public void setMiningPerTickDivisor(double v) {
        this.miningPerTickDivisor = v;
    }

    public double getStaminaConsumptionReductionPerPoint() {
        return this.staminaConsumptionReductionPerPoint;
    }

    public void setStaminaConsumptionReductionPerPoint(double v) {
        this.staminaConsumptionReductionPerPoint = v;
    }

    public double getStaminaRegenSpeedMultiplierPerPoint() {
        return this.staminaRegenSpeedMultiplierPerPoint;
    }

    public void setStaminaRegenSpeedMultiplierPerPoint(double v) {
        this.staminaRegenSpeedMultiplierPerPoint = v;
    }

    public int getMaxStatPointsForStat(@Nonnull String statName) {
        switch (statName.toLowerCase()) {
            case "health": {
                return this.maxStatPointsHealth;
            }
            case "stamina": {
                return this.maxStatPointsStamina;
            }
            case "mana": {
                return this.maxStatPointsMana;
            }
            case "ammo": {
                return this.maxStatPointsAmmo;
            }
            case "oxygen": {
                return this.maxStatPointsOxygen;
            }
            case "staminaregendelay": {
                return this.maxStatPointsStaminaRegenSpeed;
            }
            case "damage": {
                return this.maxStatPointsDamage;
            }
            case "mining": {
                return this.maxStatPointsMining;
            }
            case "woodcutting": {
                return this.maxStatPointsWoodcutting;
            }
            case "defense": {
                return this.maxStatPointsDefense;
            }
            case "staminaconsumption": {
                return this.maxStatPointsStaminaConsumption;
            }
        }
        return 50;
    }

    public int getMaxStatPointsHealth() {
        return this.maxStatPointsHealth;
    }

    public int getMaxStatPointsStamina() {
        return this.maxStatPointsStamina;
    }

    public int getMaxStatPointsMana() {
        return this.maxStatPointsMana;
    }

    public int getMaxStatPointsAmmo() {
        return this.maxStatPointsAmmo;
    }

    public int getMaxStatPointsOxygen() {
        return this.maxStatPointsOxygen;
    }

    public int getMaxStatPointsStaminaRegenSpeed() {
        return this.maxStatPointsStaminaRegenSpeed;
    }

    public int getMaxStatPointsDamage() {
        return this.maxStatPointsDamage;
    }

    public int getMaxStatPointsMining() {
        return this.maxStatPointsMining;
    }

    public int getMaxStatPointsWoodcutting() {
        return this.maxStatPointsWoodcutting;
    }

    public int getMaxStatPointsDefense() {
        return this.maxStatPointsDefense;
    }

    public int getMaxStatPointsStaminaConsumption() {
        return this.maxStatPointsStaminaConsumption;
    }

    public void setMaxStatPointsHealth(int value) {
        this.maxStatPointsHealth = value;
    }

    public void setMaxStatPointsStamina(int value) {
        this.maxStatPointsStamina = value;
    }

    public void setMaxStatPointsMana(int value) {
        this.maxStatPointsMana = value;
    }

    public void setMaxStatPointsAmmo(int value) {
        this.maxStatPointsAmmo = value;
    }

    public void setMaxStatPointsOxygen(int value) {
        this.maxStatPointsOxygen = value;
    }

    public void setMaxStatPointsStaminaRegenSpeed(int value) {
        this.maxStatPointsStaminaRegenSpeed = value;
    }

    public void setMaxStatPointsDamage(int value) {
        this.maxStatPointsDamage = value;
    }

    public void setMaxStatPointsMining(int value) {
        this.maxStatPointsMining = value;
    }

    public void setMaxStatPointsWoodcutting(int value) {
        this.maxStatPointsWoodcutting = value;
    }

    public void setMaxStatPointsDefense(int value) {
        this.maxStatPointsDefense = value;
    }

    public void setMaxStatPointsStaminaConsumption(int value) {
        this.maxStatPointsStaminaConsumption = value;
    }

    @Nonnull
    public List<String> getBlacklistedStats() {
        return this.blacklistedStats;
    }

    public void setBlacklistedStats(@Nonnull List<String> blacklistedStats) {
        this.blacklistedStats = new ArrayList<String>(blacklistedStats);
    }

    public boolean isStatBlacklisted(@Nonnull String statName) {
        if (this.blacklistedStats == null || this.blacklistedStats.isEmpty()) {
            return false;
        }
        String normalizedStatName = statName.toLowerCase();
        return this.blacklistedStats.stream().anyMatch(blacklisted -> blacklisted != null && blacklisted.toLowerCase().equals(normalizedStatName));
    }
}
