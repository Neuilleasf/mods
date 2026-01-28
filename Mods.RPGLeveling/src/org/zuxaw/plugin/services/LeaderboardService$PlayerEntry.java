/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.services;

import java.util.UUID;
import javax.annotation.Nonnull;

public static class LeaderboardService.PlayerEntry {
    private final UUID uuid;
    private final String username;
    private final int level;
    private final double totalExperience;
    private int rank;

    public LeaderboardService.PlayerEntry(@Nonnull UUID uuid, @Nonnull String username, int level, double totalExperience) {
        this.uuid = uuid;
        this.username = username;
        this.level = level;
        this.totalExperience = totalExperience;
        this.rank = 0;
    }

    @Nonnull
    public UUID getUuid() {
        return this.uuid;
    }

    @Nonnull
    public String getUsername() {
        return this.username;
    }

    public int getLevel() {
        return this.level;
    }

    public double getExperience() {
        return this.totalExperience;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
