/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.formulas.Formulas;
import org.zuxaw.plugin.utils.DebugLogger;

public class LeaderboardService {
    private static final DebugLogger DEBUG = DebugLogger.forEnclosingClass();
    private static final String PLAYERS_DIRECTORY = "universe/players";
    private final LevelingConfig config;
    private volatile List<PlayerEntry> cachedLeaderboard = List.of();
    private final AtomicLong lastRefreshTime = new AtomicLong(0L);
    private final AtomicBoolean refreshInProgress = new AtomicBoolean(false);
    private static final long CACHE_DURATION_MS = 30000L;
    private static final long OVERRIDE_TTL_MS = 60000L;
    private final ConcurrentHashMap<UUID, OverrideEntry> overrides = new ConcurrentHashMap();

    public LeaderboardService(@Nonnull LevelingConfig config) {
        this.config = config;
    }

    @Nonnull
    public List<PlayerEntry> getTopPlayers(int limit) {
        this.refreshLeaderboardIfNeeded();
        List<PlayerEntry> snapshot = this.snapshotWithOverrides(this.cachedLeaderboard);
        if (limit <= 0 || limit > snapshot.size()) {
            return new ArrayList<PlayerEntry>(snapshot);
        }
        return new ArrayList<PlayerEntry>(snapshot.subList(0, limit));
    }

    @Nonnull
    public List<PlayerEntry> getAllPlayers() {
        this.refreshLeaderboardIfNeeded();
        return new ArrayList<PlayerEntry>(this.snapshotWithOverrides(this.cachedLeaderboard));
    }

    @Nullable
    public PlayerEntry getPlayerRank(@Nonnull UUID uuid) {
        this.refreshLeaderboardIfNeeded();
        for (PlayerEntry entry : this.snapshotWithOverrides(this.cachedLeaderboard)) {
            if (!entry.getUuid().equals(uuid)) continue;
            return entry;
        }
        return null;
    }

    public void overridePlayer(@Nonnull UUID uuid, @Nonnull String username, int level, double experienceOnCurrentLevel) {
        PlayerEntry entry = new PlayerEntry(uuid, username, level, this.computeTotalXP(level, experienceOnCurrentLevel));
        this.overrides.put(uuid, new OverrideEntry(entry, System.currentTimeMillis()));
        if (!this.cachedLeaderboard.isEmpty()) {
            this.cachedLeaderboard = this.snapshotWithOverrides(this.cachedLeaderboard);
            this.lastRefreshTime.set(System.currentTimeMillis());
        }
    }

    public void forceRefresh() {
        this.lastRefreshTime.set(0L);
        this.refreshLeaderboardIfNeeded();
    }

    private void refreshLeaderboardIfNeeded() {
        long currentTime = System.currentTimeMillis();
        long last = this.lastRefreshTime.get();
        if (this.cachedLeaderboard.isEmpty()) {
            this.refreshLeaderboard();
            this.lastRefreshTime.set(System.currentTimeMillis());
            return;
        }
        if (currentTime - last <= 30000L) {
            return;
        }
        if (!this.refreshInProgress.compareAndSet(false, true)) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                this.refreshLeaderboard();
                this.lastRefreshTime.set(System.currentTimeMillis());
            }
            finally {
                this.refreshInProgress.set(false);
            }
        });
    }

    private double computeTotalXP(int level, double experienceOnCurrentLevel) {
        return Formulas.totalXpForLeaderboard(level, experienceOnCurrentLevel, this.config);
    }

    private void refreshLeaderboard() {
        ArrayList<PlayerEntry> entries = new ArrayList<PlayerEntry>();
        Path playersPath = Paths.get(PLAYERS_DIRECTORY, new String[0]);
        if (!Files.exists(playersPath, new LinkOption[0]) || !Files.isDirectory(playersPath, new LinkOption[0])) {
            DEBUG.warning(this.config, () -> "Players directory not found: universe/players");
            DEBUG.warning(this.config, () -> "Absolute path checked: " + String.valueOf(playersPath.toAbsolutePath()));
            this.cachedLeaderboard = entries;
            return;
        }
        try {
            List playerFiles;
            try (Stream<Path> stream = Files.list(playersPath);){
                playerFiles = stream.filter(path -> path.toString().endsWith(".json")).map(Path::toFile).collect(Collectors.toList());
            }
            for (File file : playerFiles) {
                try {
                    PlayerEntry entry = this.parsePlayerFile(file);
                    if (entry == null) continue;
                    entries.add(entry);
                }
                catch (Exception e) {
                    DEBUG.warning(this.config, () -> "Failed to parse player file " + file.getName() + ": " + e.getMessage());
                }
            }
            entries.sort(Comparator.comparingDouble(PlayerEntry::getExperience).reversed());
            for (int i = 0; i < entries.size(); ++i) {
                ((PlayerEntry)entries.get(i)).setRank(i + 1);
            }
            this.cachedLeaderboard = List.copyOf(entries);
        }
        catch (Exception e) {
            DEBUG.severe(this.config, () -> "Failed to refresh leaderboard: " + e.getMessage());
            this.cachedLeaderboard = List.copyOf(entries);
        }
    }

    @Nonnull
    private List<PlayerEntry> snapshotWithOverrides(@Nonnull List<PlayerEntry> base) {
        if (this.overrides.isEmpty()) {
            return base;
        }
        long now = System.currentTimeMillis();
        this.cleanupOverrides(now);
        if (this.overrides.isEmpty()) {
            return base;
        }
        ArrayList<PlayerEntry> merged = new ArrayList<PlayerEntry>(base.size() + this.overrides.size());
        HashSet<UUID> seen = new HashSet<UUID>();
        for (PlayerEntry playerEntry : base) {
            OverrideEntry o = this.overrides.get(playerEntry.getUuid());
            if (o != null) {
                merged.add(o.entry);
                seen.add(playerEntry.getUuid());
                continue;
            }
            merged.add(playerEntry);
        }
        for (Map.Entry entry : this.overrides.entrySet()) {
            if (seen.contains(entry.getKey())) continue;
            merged.add(((OverrideEntry)entry.getValue()).entry);
        }
        merged.sort(Comparator.comparingDouble(PlayerEntry::getExperience).reversed());
        for (int i = 0; i < merged.size(); ++i) {
            ((PlayerEntry)merged.get(i)).setRank(i + 1);
        }
        return List.copyOf(merged);
    }

    private void cleanupOverrides(long nowMs) {
        for (Map.Entry<UUID, OverrideEntry> kv : this.overrides.entrySet()) {
            OverrideEntry o = kv.getValue();
            if (o == null || nowMs - o.createdAtMs <= 60000L) continue;
            this.overrides.remove(kv.getKey(), o);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private PlayerEntry parsePlayerFile(@Nonnull File file) {
        try (FileReader reader = new FileReader(file);){
            UUID uuid;
            JsonElement element = JsonParser.parseReader((Reader)reader);
            if (!element.isJsonObject()) {
                PlayerEntry playerEntry = null;
                return playerEntry;
            }
            JsonObject root = element.getAsJsonObject();
            JsonObject components = root.getAsJsonObject("Components");
            if (components == null) {
                PlayerEntry playerEntry = null;
                return playerEntry;
            }
            String filename = file.getName();
            String uuidString = filename.substring(0, filename.length() - 5);
            try {
                uuid = UUID.fromString(uuidString);
            }
            catch (IllegalArgumentException e) {
                PlayerEntry playerEntry = null;
                reader.close();
                return playerEntry;
            }
            String username = "Unknown Player";
            JsonObject nameplate = components.getAsJsonObject("Nameplate");
            if (nameplate != null && nameplate.has("Text")) {
                username = nameplate.get("Text").getAsString();
            }
            int level = 1;
            double experienceOnCurrentLevel = 0.0;
            JsonObject playerLevelData = components.getAsJsonObject("PlayerLevelData");
            if (playerLevelData != null) {
                if (playerLevelData.has("Level")) {
                    level = playerLevelData.get("Level").getAsInt();
                }
                if (playerLevelData.has("Experience")) {
                    experienceOnCurrentLevel = playerLevelData.get("Experience").getAsDouble();
                }
            }
            double totalXP = this.computeTotalXP(level, experienceOnCurrentLevel);
            PlayerEntry playerEntry = new PlayerEntry(uuid, username, level, totalXP);
            return playerEntry;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static class PlayerEntry {
        private final UUID uuid;
        private final String username;
        private final int level;
        private final double totalExperience;
        private int rank;

        public PlayerEntry(@Nonnull UUID uuid, @Nonnull String username, int level, double totalExperience) {
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

    private static final class OverrideEntry {
        final PlayerEntry entry;
        final long createdAtMs;

        OverrideEntry(@Nonnull PlayerEntry entry, long createdAtMs) {
            this.entry = entry;
            this.createdAtMs = createdAtMs;
        }
    }
}
