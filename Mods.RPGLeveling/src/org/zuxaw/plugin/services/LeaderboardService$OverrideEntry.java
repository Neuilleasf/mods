/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.services;

import javax.annotation.Nonnull;
import org.zuxaw.plugin.services.LeaderboardService;

private static final class LeaderboardService.OverrideEntry {
    final LeaderboardService.PlayerEntry entry;
    final long createdAtMs;

    LeaderboardService.OverrideEntry(@Nonnull LeaderboardService.PlayerEntry entry, long createdAtMs) {
        this.entry = entry;
        this.createdAtMs = createdAtMs;
    }
}
