/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.formulas;

import javax.annotation.Nonnull;
import org.zuxaw.plugin.config.LevelingConfig;

public final class Formulas {
    private Formulas() {
    }

    public static double xpFromKill(double maxHealth, @Nonnull LevelingConfig config) {
        if (maxHealth <= 0.0) {
            return 0.0;
        }
        return Math.sqrt(maxHealth) * config.getBaseXP() * config.getRateExp();
    }

    public static double xpRequiredForLevel(int level, @Nonnull LevelingConfig config) {
        if (level <= 1) {
            return 0.0;
        }
        return config.getLevelBaseXP() * (double)level * (double)level + config.getLevelOffset();
    }

    public static double xpNeededForNextLevel(int currentLevel, @Nonnull LevelingConfig config) {
        if (currentLevel >= config.getMaxLevel()) {
            return 0.0;
        }
        return Formulas.xpRequiredForLevel(currentLevel + 1, config) - Formulas.xpRequiredForLevel(currentLevel, config);
    }

    public static double totalXpForLeaderboard(int level, double experienceOnCurrentLevel, @Nonnull LevelingConfig config) {
        double xpToReachLevel = level <= 1 ? 0.0 : config.getLevelBaseXP() * (double)level * (double)level + config.getLevelOffset();
        return xpToReachLevel + experienceOnCurrentLevel;
    }

    public static float damageMultiplier(int statLevel, @Nonnull LevelingConfig config) {
        if (statLevel <= 0) {
            return 1.0f;
        }
        return (float)(1.0 + (double)statLevel * config.getDamageStatValuePerPoint() / 100.0);
    }

    public static float damageFlatBonus(int statLevel, @Nonnull LevelingConfig config) {
        if (statLevel <= 0) {
            return 0.0f;
        }
        return statLevel / 10;
    }

    public static float defenseReductionRatio(int statLevel, @Nonnull LevelingConfig config) {
        if (statLevel <= 0) {
            return 0.0f;
        }
        double effective = (double)statLevel * config.getDefenseStatValuePerPoint();
        double ratio = config.getDefenseMaxReductionRatio() * effective / (effective + 50.0);
        return (float)Math.min(1.0, Math.max(0.0, ratio));
    }

    public static double blockDamageMultiplier(int statLevel, double statValuePerPoint, @Nonnull LevelingConfig config) {
        if (statLevel <= 0) {
            return 1.0;
        }
        double divisor = config.getBlockDamageScalingDivisor();
        if (divisor <= 0.0) {
            return 1.0;
        }
        return 1.0 + (double)statLevel * statValuePerPoint / divisor;
    }

    public static float blockDamagePerTickBonus(int statLevel, double statValuePerPoint, @Nonnull LevelingConfig config) {
        if (statLevel <= 0) {
            return 0.0f;
        }
        double scaling = config.getBlockDamageScalingDivisor();
        double tickDiv = config.getMiningPerTickDivisor();
        if (scaling <= 0.0 || tickDiv <= 0.0) {
            return 0.0f;
        }
        return (float)((double)statLevel * statValuePerPoint / scaling / tickDiv);
    }

    public static float staminaConsumptionMultiplier(int allocatedPoints, @Nonnull LevelingConfig config) {
        if (allocatedPoints <= 0) {
            return 1.0f;
        }
        float reduction = (float)((double)allocatedPoints * config.getStaminaConsumptionReductionPerPoint());
        return Math.max(0.1f, 1.0f - reduction);
    }

    public static float staminaRegenSpeedMultiplier(int allocatedPoints, @Nonnull LevelingConfig config) {
        if (allocatedPoints <= 0) {
            return 1.0f;
        }
        return 1.0f + (float)((double)allocatedPoints * config.getStaminaRegenSpeedMultiplierPerPoint());
    }

    public static float statModifierValue(int allocatedPoints, double statValuePerPoint) {
        if (allocatedPoints <= 0) {
            return 0.0f;
        }
        return (float)((double)allocatedPoints * statValuePerPoint);
    }
}
