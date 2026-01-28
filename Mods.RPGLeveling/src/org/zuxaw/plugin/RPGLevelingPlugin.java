/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.system.ISystem
 *  com.hypixel.hytale.server.core.command.system.AbstractCommand
 *  com.hypixel.hytale.server.core.entity.UUIDComponent
 *  com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent
 *  com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent
 *  com.hypixel.hytale.server.core.plugin.JavaPlugin
 *  com.hypixel.hytale.server.core.plugin.JavaPluginInit
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.Universe
 *  com.hypixel.hytale.server.core.universe.world.World
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  com.hypixel.hytale.server.core.util.Config
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.commands.RPGLevelingCommand;
import org.zuxaw.plugin.components.DeathProcessedMarker;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.components.StatsAppliedMarker;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.hud.LevelProgressHudManager;
import org.zuxaw.plugin.services.LeaderboardService;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.systems.DamageModificationSystem;
import org.zuxaw.plugin.systems.DamageTrackingSystem;
import org.zuxaw.plugin.systems.DeathDetectionSystem;
import org.zuxaw.plugin.systems.DefenseModificationSystem;
import org.zuxaw.plugin.systems.LevelProgressHudSystem;
import org.zuxaw.plugin.systems.MiningDamageBlockSystem;
import org.zuxaw.plugin.systems.MiningDetectionSystem;
import org.zuxaw.plugin.systems.StaminaConsumptionSystem;
import org.zuxaw.plugin.systems.StaminaRegenSpeedSystem;
import org.zuxaw.plugin.systems.StatsApplicationSystem;
import org.zuxaw.plugin.utils.DebugLogger;
import org.zuxaw.plugin.utils.LocalizationValidator;

public class RPGLevelingPlugin
extends JavaPlugin {
    private static RPGLevelingPlugin instance;
    private final Config<LevelingConfig> config = this.withConfig("RPGLevelingConfig", LevelingConfig.CODEC);
    private final Map<UUID, UUID> lastAttackers = new ConcurrentHashMap<UUID, UUID>();
    private final Map<UUID, String> entityNames = new ConcurrentHashMap<UUID, String>();
    private final Set<UUID> statsGuiOpenPlayerIds = ConcurrentHashMap.newKeySet();
    private ComponentType<EntityStore, PlayerLevelData> playerLevelDataType;
    private ComponentType<EntityStore, DeathProcessedMarker> deathProcessedMarkerType;
    private ComponentType<EntityStore, StatsAppliedMarker> statsAppliedMarkerType;
    private LevelingService levelingService;
    private StatsService statsService;
    private LeaderboardService leaderboardService;
    private LevelProgressHudManager hudManager;
    private MessageService messageService;
    private static final DebugLogger DEBUG;

    public RPGLevelingPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        try {
            LevelingConfig levelingConfig = (LevelingConfig)this.config.get();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    protected void setup() {
        Path configPath;
        File configFile;
        LevelingConfig levelingConfig = (LevelingConfig)this.config.get();
        String workingDir = System.getProperty("user.dir");
        if (workingDir != null && !workingDir.isEmpty() && (configFile = (configPath = Paths.get(workingDir, "mods", "RPGLeveling", "RPGLevelingConfig.json")).toFile()).exists()) {
            try {
                Object fileBlacklistedStats;
                Object fileMaxStatPointsDefense;
                Object fileMaxStatPointsWoodcutting;
                Object fileMaxStatPointsMining;
                Object fileMaxStatPointsDamage;
                Object fileMaxStatPointsStaminaConsumption;
                Object fileMaxStatPointsStaminaRegenSpeed;
                Object fileMaxStatPointsOxygen;
                Object fileMaxStatPointsAmmo;
                Object fileMaxStatPointsMana;
                Object fileMaxStatPointsStamina;
                Object fileMaxStatPointsHealth;
                Object fileDefenseStatValuePerPoint;
                Object fileWoodcuttingStatValuePerPoint;
                Object fileMiningStatValuePerPoint;
                Object fileDamageStatValuePerPoint;
                Object fileResetLevelOnDeath;
                Object fileDebug;
                Object fileEnableHUD;
                Object fileStatValuePerPoint;
                Object fileStatPointsPerLevel;
                Object fileLevelOffset;
                Object fileLevelBaseXP;
                Object fileMaxLevel;
                Map<String, Object> fileConfig = this.readConfigFile(configFile);
                Object fileRateExp = fileConfig.get("RateExp");
                Object fileBaseXP = fileConfig.get("BaseXP");
                boolean configUpdated = false;
                if (fileRateExp != null) {
                    double fileRateExpValue;
                    double d = fileRateExpValue = fileRateExp instanceof Number ? ((Number)fileRateExp).doubleValue() : 0.0;
                    if (Math.abs(levelingConfig.getRateExp() - fileRateExpValue) > 0.01) {
                        levelingConfig.setRateExp(fileRateExpValue);
                        configUpdated = true;
                    }
                }
                if (fileBaseXP != null) {
                    double fileBaseXPValue;
                    double d = fileBaseXPValue = fileBaseXP instanceof Number ? ((Number)fileBaseXP).doubleValue() : 0.0;
                    if (Math.abs(levelingConfig.getBaseXP() - fileBaseXPValue) > 0.01) {
                        levelingConfig.setBaseXP(fileBaseXPValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxLevel = fileConfig.get("MaxLevel")) != null && fileMaxLevel instanceof Number) {
                    int fileMaxLevelValue = ((Number)fileMaxLevel).intValue();
                    if (levelingConfig.getMaxLevel() != fileMaxLevelValue) {
                        levelingConfig.setMaxLevel(fileMaxLevelValue);
                        configUpdated = true;
                    }
                }
                if ((fileLevelBaseXP = fileConfig.get("LevelBaseXP")) != null && fileLevelBaseXP instanceof Number) {
                    double fileLevelBaseXPValue = ((Number)fileLevelBaseXP).doubleValue();
                    if (Math.abs(levelingConfig.getLevelBaseXP() - fileLevelBaseXPValue) > 0.01) {
                        levelingConfig.setLevelBaseXP(fileLevelBaseXPValue);
                        configUpdated = true;
                    }
                }
                if ((fileLevelOffset = fileConfig.get("LevelOffset")) != null && fileLevelOffset instanceof Number) {
                    double fileLevelOffsetValue = ((Number)fileLevelOffset).doubleValue();
                    if (Math.abs(levelingConfig.getLevelOffset() - fileLevelOffsetValue) > 0.01) {
                        levelingConfig.setLevelOffset(fileLevelOffsetValue);
                        configUpdated = true;
                    }
                }
                if ((fileStatPointsPerLevel = fileConfig.get("StatPointsPerLevel")) != null && fileStatPointsPerLevel instanceof Number) {
                    int fileStatPointsPerLevelValue = ((Number)fileStatPointsPerLevel).intValue();
                    if (levelingConfig.getStatPointsPerLevel() != fileStatPointsPerLevelValue) {
                        levelingConfig.setStatPointsPerLevel(fileStatPointsPerLevelValue);
                        configUpdated = true;
                    }
                }
                if ((fileStatValuePerPoint = fileConfig.get("StatValuePerPoint")) != null && fileStatValuePerPoint instanceof Number) {
                    double fileStatValuePerPointValue = ((Number)fileStatValuePerPoint).doubleValue();
                    if (Math.abs(levelingConfig.getStatValuePerPoint() - fileStatValuePerPointValue) > 0.01) {
                        levelingConfig.setStatValuePerPoint(fileStatValuePerPointValue);
                        configUpdated = true;
                    }
                }
                if ((fileEnableHUD = fileConfig.get("EnableHUD")) != null && fileEnableHUD instanceof Boolean) {
                    boolean fileEnableHUDValue = (Boolean)fileEnableHUD;
                    if (levelingConfig.isEnableHUD() != fileEnableHUDValue) {
                        levelingConfig.setEnableHUD(fileEnableHUDValue);
                        configUpdated = true;
                    }
                }
                if ((fileDebug = fileConfig.get("Debug")) != null && fileDebug instanceof Boolean) {
                    boolean fileDebugValue = (Boolean)fileDebug;
                    if (levelingConfig.isDebug() != fileDebugValue) {
                        levelingConfig.setDebug(fileDebugValue);
                        configUpdated = true;
                    }
                }
                if ((fileResetLevelOnDeath = fileConfig.get("ResetLevelOnDeath")) != null && fileResetLevelOnDeath instanceof Boolean) {
                    boolean fileResetLevelOnDeathValue = (Boolean)fileResetLevelOnDeath;
                    if (levelingConfig.isResetLevelOnDeath() != fileResetLevelOnDeathValue) {
                        levelingConfig.setResetLevelOnDeath(fileResetLevelOnDeathValue);
                        configUpdated = true;
                    }
                }
                if ((fileDamageStatValuePerPoint = fileConfig.get("DamageStatValuePerPoint")) != null && fileDamageStatValuePerPoint instanceof Number) {
                    double fileValue = ((Number)fileDamageStatValuePerPoint).doubleValue();
                    if (Math.abs(levelingConfig.getDamageStatValuePerPoint() - fileValue) > 0.001) {
                        levelingConfig.setDamageStatValuePerPoint(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMiningStatValuePerPoint = fileConfig.get("MiningStatValuePerPoint")) != null && fileMiningStatValuePerPoint instanceof Number) {
                    double fileValue = ((Number)fileMiningStatValuePerPoint).doubleValue();
                    if (Math.abs(levelingConfig.getMiningStatValuePerPoint() - fileValue) > 0.001) {
                        levelingConfig.setMiningStatValuePerPoint(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileWoodcuttingStatValuePerPoint = fileConfig.get("WoodcuttingStatValuePerPoint")) != null && fileWoodcuttingStatValuePerPoint instanceof Number) {
                    double fileValue = ((Number)fileWoodcuttingStatValuePerPoint).doubleValue();
                    if (Math.abs(levelingConfig.getWoodcuttingStatValuePerPoint() - fileValue) > 0.001) {
                        levelingConfig.setWoodcuttingStatValuePerPoint(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileDefenseStatValuePerPoint = fileConfig.get("DefenseStatValuePerPoint")) != null && fileDefenseStatValuePerPoint instanceof Number) {
                    double fileValue = ((Number)fileDefenseStatValuePerPoint).doubleValue();
                    if (Math.abs(levelingConfig.getDefenseStatValuePerPoint() - fileValue) > 0.001) {
                        levelingConfig.setDefenseStatValuePerPoint(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsHealth = fileConfig.get("MaxStatPointsHealth")) != null && fileMaxStatPointsHealth instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsHealth).intValue();
                    if (levelingConfig.getMaxStatPointsHealth() != fileValue) {
                        levelingConfig.setMaxStatPointsHealth(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsStamina = fileConfig.get("MaxStatPointsStamina")) != null && fileMaxStatPointsStamina instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsStamina).intValue();
                    if (levelingConfig.getMaxStatPointsStamina() != fileValue) {
                        levelingConfig.setMaxStatPointsStamina(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsMana = fileConfig.get("MaxStatPointsMana")) != null && fileMaxStatPointsMana instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsMana).intValue();
                    if (levelingConfig.getMaxStatPointsMana() != fileValue) {
                        levelingConfig.setMaxStatPointsMana(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsAmmo = fileConfig.get("MaxStatPointsAmmo")) != null && fileMaxStatPointsAmmo instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsAmmo).intValue();
                    if (levelingConfig.getMaxStatPointsAmmo() != fileValue) {
                        levelingConfig.setMaxStatPointsAmmo(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsOxygen = fileConfig.get("MaxStatPointsOxygen")) != null && fileMaxStatPointsOxygen instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsOxygen).intValue();
                    if (levelingConfig.getMaxStatPointsOxygen() != fileValue) {
                        levelingConfig.setMaxStatPointsOxygen(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsStaminaRegenSpeed = fileConfig.get("MaxStatPointsStaminaRegenSpeed")) == null) {
                    fileMaxStatPointsStaminaRegenSpeed = fileConfig.get("MaxStatPointsStaminaRegenDelay");
                }
                if (fileMaxStatPointsStaminaRegenSpeed != null && fileMaxStatPointsStaminaRegenSpeed instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsStaminaRegenSpeed).intValue();
                    if (levelingConfig.getMaxStatPointsStaminaRegenSpeed() != fileValue) {
                        levelingConfig.setMaxStatPointsStaminaRegenSpeed(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsStaminaConsumption = fileConfig.get("MaxStatPointsStaminaConsumption")) != null && fileMaxStatPointsStaminaConsumption instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsStaminaConsumption).intValue();
                    if (levelingConfig.getMaxStatPointsStaminaConsumption() != fileValue) {
                        levelingConfig.setMaxStatPointsStaminaConsumption(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsDamage = fileConfig.get("MaxStatPointsDamage")) != null && fileMaxStatPointsDamage instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsDamage).intValue();
                    if (levelingConfig.getMaxStatPointsDamage() != fileValue) {
                        levelingConfig.setMaxStatPointsDamage(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsMining = fileConfig.get("MaxStatPointsMining")) != null && fileMaxStatPointsMining instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsMining).intValue();
                    if (levelingConfig.getMaxStatPointsMining() != fileValue) {
                        levelingConfig.setMaxStatPointsMining(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsWoodcutting = fileConfig.get("MaxStatPointsWoodcutting")) != null && fileMaxStatPointsWoodcutting instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsWoodcutting).intValue();
                    if (levelingConfig.getMaxStatPointsWoodcutting() != fileValue) {
                        levelingConfig.setMaxStatPointsWoodcutting(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileMaxStatPointsDefense = fileConfig.get("MaxStatPointsDefense")) != null && fileMaxStatPointsDefense instanceof Number) {
                    int fileValue = ((Number)fileMaxStatPointsDefense).intValue();
                    if (levelingConfig.getMaxStatPointsDefense() != fileValue) {
                        levelingConfig.setMaxStatPointsDefense(fileValue);
                        configUpdated = true;
                    }
                }
                if ((fileBlacklistedStats = fileConfig.get("BlacklistedStats")) != null) {
                    List<String> currentBlacklist;
                    boolean listsMatch;
                    String fileBlacklistString = fileBlacklistedStats instanceof String ? (String)fileBlacklistedStats : String.valueOf(fileBlacklistedStats);
                    ArrayList<String> fileBlacklist = new ArrayList<String>();
                    if (fileBlacklistString != null && !fileBlacklistString.isEmpty()) {
                        String[] stats;
                        for (String stat : stats = fileBlacklistString.split(",")) {
                            String trimmed = stat.trim();
                            if (trimmed.isEmpty()) continue;
                            fileBlacklist.add(trimmed);
                        }
                    }
                    boolean bl = listsMatch = (currentBlacklist = levelingConfig.getBlacklistedStats()).size() == fileBlacklist.size() && currentBlacklist.containsAll(fileBlacklist);
                    if (!listsMatch) {
                        levelingConfig.setBlacklistedStats(fileBlacklist);
                        configUpdated = true;
                    }
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.createConfigFileIfNeeded(levelingConfig);
        this.playerLevelDataType = this.getEntityStoreRegistry().registerComponent(PlayerLevelData.class, "PlayerLevelData", PlayerLevelData.CODEC);
        this.deathProcessedMarkerType = this.getEntityStoreRegistry().registerComponent(DeathProcessedMarker.class, "DeathProcessedMarker", DeathProcessedMarker.CODEC);
        this.statsAppliedMarkerType = this.getEntityStoreRegistry().registerComponent(StatsAppliedMarker.class, StatsAppliedMarker::new);
        this.messageService = new MessageService();
        if (levelingConfig.isDebug()) {
            LocalizationValidator.setEnabled(levelingConfig, true);
            LocalizationValidator.logCoverageSummary(this.messageService);
        }
        this.levelingService = new LevelingService(this.playerLevelDataType, this.messageService);
        this.statsService = new StatsService(this.playerLevelDataType);
        this.levelingService.setStatsService(this.statsService);
        this.leaderboardService = new LeaderboardService(levelingConfig);
        if (levelingConfig.isEnableHUD()) {
            this.hudManager = new LevelProgressHudManager(this.levelingService, levelingConfig, this.messageService);
        } else {
            DEBUG.info(levelingConfig, "[RPGLeveling] HUD disabled in config - level progress will not be displayed");
        }
        this.getCommandRegistry().registerCommand((AbstractCommand)new RPGLevelingCommand(this.levelingService, this.statsService, levelingConfig, this.messageService, this.leaderboardService));
        this.getEntityStoreRegistry().registerSystem((ISystem)new DamageTrackingSystem(this.lastAttackers, this.entityNames));
        this.getEntityStoreRegistry().registerSystem((ISystem)new DeathDetectionSystem(this.lastAttackers, this.entityNames, this.levelingService, this.config, this.messageService, this.deathProcessedMarkerType));
        this.getEntityStoreRegistry().registerSystem((ISystem)new StatsApplicationSystem(this.statsService, this.config, this.statsAppliedMarkerType));
        this.getEntityStoreRegistry().registerSystem((ISystem)new DamageModificationSystem(this.statsService, levelingConfig));
        this.getEntityStoreRegistry().registerSystem((ISystem)new MiningDetectionSystem(this.statsService, levelingConfig));
        this.getEntityStoreRegistry().registerSystem((ISystem)new MiningDamageBlockSystem(this.statsService, levelingConfig));
        this.getEntityStoreRegistry().registerSystem((ISystem)new DefenseModificationSystem(this.statsService, levelingConfig));
        this.getEntityStoreRegistry().registerSystem((ISystem)new StaminaRegenSpeedSystem(this.statsService, levelingConfig));
        this.getEntityStoreRegistry().registerSystem((ISystem)new StaminaConsumptionSystem(this.statsService, levelingConfig));
        if (levelingConfig.isEnableHUD()) {
            this.getEntityStoreRegistry().registerSystem((ISystem)new LevelProgressHudSystem());
        }
        if (levelingConfig.isEnableHUD()) {
            this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {
                Holder holder = event.getHolder();
                if (holder == null) {
                    return;
                }
                World world = event.getWorld();
                if (world == null || !world.isAlive()) {
                    return;
                }
                UUIDComponent uuidComponent = (UUIDComponent)holder.getComponent(UUIDComponent.getComponentType());
                if (uuidComponent == null) {
                    return;
                }
                UUID playerUuid = uuidComponent.getUuid();
                PlayerRef playerRef = Universe.get().getPlayer(playerUuid);
                if (playerRef == null) {
                    return;
                }
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        }
        this.getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
            PlayerRef playerRef = event.getPlayerRef();
            if (this.hudManager != null) {
                this.hudManager.onPlayerLeave(playerRef);
            }
            this.markStatsGuiClosed(playerRef.getUuid());
        });
    }

    protected void start() {
        LevelingConfig levelingConfig = (LevelingConfig)this.config.get();
    }

    public static RPGLevelingPlugin get() {
        return instance;
    }

    public ComponentType<EntityStore, PlayerLevelData> getPlayerLevelDataType() {
        return this.playerLevelDataType;
    }

    public LevelingService getLevelingService() {
        return this.levelingService;
    }

    public Config<LevelingConfig> getConfig() {
        return this.config;
    }

    public StatsService getStatsService() {
        return this.statsService;
    }

    @Nullable
    public LeaderboardService getLeaderboardService() {
        return this.leaderboardService;
    }

    public LevelProgressHudManager getHudManager() {
        return this.hudManager;
    }

    public MessageService getMessageService() {
        return this.messageService;
    }

    public void markStatsGuiOpen(@Nonnull UUID playerId) {
        this.statsGuiOpenPlayerIds.add(playerId);
    }

    public void markStatsGuiClosed(@Nonnull UUID playerId) {
        this.statsGuiOpenPlayerIds.remove(playerId);
    }

    public boolean isStatsGuiOpen(@Nonnull UUID playerId) {
        return this.statsGuiOpenPlayerIds.contains(playerId);
    }

    private void createConfigFileIfNeeded(@Nonnull LevelingConfig defaultConfig) {
        block10: {
            try {
                File configFile;
                String workingDir = System.getProperty("user.dir");
                if (workingDir == null || workingDir.isEmpty()) {
                    return;
                }
                Path pluginsDir = Paths.get(workingDir, "mods", "RPGLeveling");
                File pluginDataDir = pluginsDir.toFile();
                if (!pluginDataDir.exists()) {
                    pluginDataDir.mkdirs();
                }
                if (!(configFile = new File(pluginDataDir, "RPGLevelingConfig.json")).exists()) {
                    String jsonContent = this.formatConfigJson(defaultConfig);
                    try (FileWriter writer = new FileWriter(configFile);){
                        writer.write(jsonContent);
                        break block10;
                    }
                }
                this.mergeConfigFile(configFile, defaultConfig);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private void mergeConfigFile(@Nonnull File configFile, @Nonnull LevelingConfig defaultConfig) {
        block8: {
            try {
                Map<String, Object> existingConfig = this.readConfigFile(configFile);
                HashMap<String, Object> defaultValues = new HashMap<String, Object>();
                defaultValues.put("MaxLevel", defaultConfig.getMaxLevel());
                defaultValues.put("RateExp", defaultConfig.getRateExp());
                defaultValues.put("BaseXP", defaultConfig.getBaseXP());
                defaultValues.put("LevelBaseXP", defaultConfig.getLevelBaseXP());
                defaultValues.put("LevelOffset", defaultConfig.getLevelOffset());
                defaultValues.put("StatPointsPerLevel", defaultConfig.getStatPointsPerLevel());
                defaultValues.put("StatValuePerPoint", defaultConfig.getStatValuePerPoint());
                defaultValues.put("EnableHUD", defaultConfig.isEnableHUD());
                defaultValues.put("Debug", defaultConfig.isDebug());
                defaultValues.put("ResetLevelOnDeath", defaultConfig.isResetLevelOnDeath());
                defaultValues.put("DamageStatValuePerPoint", defaultConfig.getDamageStatValuePerPoint());
                defaultValues.put("MiningStatValuePerPoint", defaultConfig.getMiningStatValuePerPoint());
                defaultValues.put("WoodcuttingStatValuePerPoint", defaultConfig.getWoodcuttingStatValuePerPoint());
                defaultValues.put("DefenseStatValuePerPoint", defaultConfig.getDefenseStatValuePerPoint());
                defaultValues.put("DefenseMaxReductionRatio", defaultConfig.getDefenseMaxReductionRatio());
                defaultValues.put("BlockDamageScalingDivisor", defaultConfig.getBlockDamageScalingDivisor());
                defaultValues.put("MiningPerTickDivisor", defaultConfig.getMiningPerTickDivisor());
                defaultValues.put("StaminaConsumptionReductionPerPoint", defaultConfig.getStaminaConsumptionReductionPerPoint());
                defaultValues.put("StaminaRegenSpeedMultiplierPerPoint", defaultConfig.getStaminaRegenSpeedMultiplierPerPoint());
                defaultValues.put("MaxStatPointsHealth", defaultConfig.getMaxStatPointsHealth());
                defaultValues.put("MaxStatPointsStamina", defaultConfig.getMaxStatPointsStamina());
                defaultValues.put("MaxStatPointsMana", defaultConfig.getMaxStatPointsMana());
                defaultValues.put("MaxStatPointsAmmo", defaultConfig.getMaxStatPointsAmmo());
                defaultValues.put("MaxStatPointsOxygen", defaultConfig.getMaxStatPointsOxygen());
                defaultValues.put("MaxStatPointsStaminaRegenSpeed", defaultConfig.getMaxStatPointsStaminaRegenSpeed());
                defaultValues.put("MaxStatPointsStaminaConsumption", defaultConfig.getMaxStatPointsStaminaConsumption());
                defaultValues.put("MaxStatPointsDamage", defaultConfig.getMaxStatPointsDamage());
                defaultValues.put("MaxStatPointsMining", defaultConfig.getMaxStatPointsMining());
                defaultValues.put("MaxStatPointsWoodcutting", defaultConfig.getMaxStatPointsWoodcutting());
                defaultValues.put("MaxStatPointsDefense", defaultConfig.getMaxStatPointsDefense());
                defaultValues.put("BlacklistedStats", String.join((CharSequence)",", defaultConfig.getBlacklistedStats()));
                boolean hasChanges = false;
                for (Map.Entry entry : defaultValues.entrySet()) {
                    String key = (String)entry.getKey();
                    if (existingConfig.containsKey(key)) continue;
                    existingConfig.put(key, entry.getValue());
                    hasChanges = true;
                }
                if (!hasChanges) break block8;
                String mergedJson = this.formatConfigJsonFromMap(existingConfig);
                try (FileWriter writer = new FileWriter(configFile);){
                    writer.write(mergedJson);
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private Map<String, Object> readConfigFile(@Nonnull File configFile) throws IOException {
        HashMap<String, Object> config = new HashMap<String, Object>();
        try (FileReader reader = new FileReader(configFile);){
            int ch;
            StringBuilder content = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                content.append((char)ch);
            }
            String json = content.toString().trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1).trim();
            }
            if (!json.isEmpty()) {
                int depth = 0;
                boolean inString = false;
                boolean escapeNext = false;
                StringBuilder currentPair = new StringBuilder();
                for (int i = 0; i < json.length(); ++i) {
                    char c = json.charAt(i);
                    if (escapeNext) {
                        currentPair.append(c);
                        escapeNext = false;
                        continue;
                    }
                    if (c == '\\') {
                        escapeNext = true;
                        currentPair.append(c);
                        continue;
                    }
                    if (c == '\"') {
                        inString = !inString;
                        currentPair.append(c);
                        continue;
                    }
                    if (c == '{' || c == '[') {
                        ++depth;
                        currentPair.append(c);
                        continue;
                    }
                    if (c == '}' || c == ']') {
                        --depth;
                        currentPair.append(c);
                        continue;
                    }
                    if (c == ',' && depth == 0 && !inString) {
                        this.parseKeyValuePair(currentPair.toString(), config);
                        currentPair.setLength(0);
                        continue;
                    }
                    currentPair.append(c);
                }
                if (currentPair.length() > 0) {
                    this.parseKeyValuePair(currentPair.toString(), config);
                }
            }
        }
        return config;
    }

    private void parseKeyValuePair(@Nonnull String pair, @Nonnull Map<String, Object> config) {
        Object value;
        if ((pair = pair.trim()).isEmpty()) {
            return;
        }
        int colonIndex = pair.indexOf(58);
        if (colonIndex == -1) {
            return;
        }
        String key = pair.substring(0, colonIndex).trim();
        String valueStr = pair.substring(colonIndex + 1).trim();
        if (key.startsWith("\"") && key.endsWith("\"")) {
            key = key.substring(1, key.length() - 1);
        }
        if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
            value = valueStr.substring(1, valueStr.length() - 1);
        } else if (valueStr.contains(".")) {
            try {
                value = Double.parseDouble(valueStr);
            }
            catch (NumberFormatException e) {
                value = valueStr;
            }
        } else {
            try {
                value = Integer.parseInt(valueStr);
            }
            catch (NumberFormatException e) {
                value = valueStr.equalsIgnoreCase("true") ? Boolean.valueOf(true) : (valueStr.equalsIgnoreCase("false") ? Boolean.valueOf(false) : valueStr);
            }
        }
        config.put(key, value);
    }

    private String formatConfigJson(@Nonnull LevelingConfig config) {
        return String.format("{\n  \"MaxLevel\": %d,\n  \"RateExp\": %.1f,\n  \"BaseXP\": %.1f,\n  \"LevelBaseXP\": %.1f,\n  \"LevelOffset\": %.1f,\n  \"StatPointsPerLevel\": %d,\n  \"StatValuePerPoint\": %.1f,\n  \"EnableHUD\": %s,\n  \"Debug\": %s,\n  \"ResetLevelOnDeath\": %s,\n  \"DamageStatValuePerPoint\": %.1f,\n  \"MiningStatValuePerPoint\": %.1f,\n  \"WoodcuttingStatValuePerPoint\": %.1f,\n  \"DefenseStatValuePerPoint\": %.1f,\n  \"DefenseMaxReductionRatio\": %.2f,\n  \"BlockDamageScalingDivisor\": %.1f,\n  \"MiningPerTickDivisor\": %.1f,\n  \"StaminaConsumptionReductionPerPoint\": %.2f,\n  \"StaminaRegenSpeedMultiplierPerPoint\": %.2f,\n  \"MaxStatPointsHealth\": %d,\n  \"MaxStatPointsStamina\": %d,\n  \"MaxStatPointsMana\": %d,\n  \"MaxStatPointsAmmo\": %d,\n  \"MaxStatPointsOxygen\": %d,\n  \"MaxStatPointsStaminaRegenSpeed\": %d,\n  \"MaxStatPointsStaminaConsumption\": %d,\n  \"MaxStatPointsDamage\": %d,\n  \"MaxStatPointsMining\": %d,\n  \"MaxStatPointsWoodcutting\": %d,\n  \"MaxStatPointsDefense\": %d,\n  \"BlacklistedStats\": \"%s\"\n}", config.getMaxLevel(), config.getRateExp(), config.getBaseXP(), config.getLevelBaseXP(), config.getLevelOffset(), config.getStatPointsPerLevel(), config.getStatValuePerPoint(), config.isEnableHUD(), config.isDebug(), config.isResetLevelOnDeath(), config.getDamageStatValuePerPoint(), config.getMiningStatValuePerPoint(), config.getWoodcuttingStatValuePerPoint(), config.getDefenseStatValuePerPoint(), config.getDefenseMaxReductionRatio(), config.getBlockDamageScalingDivisor(), config.getMiningPerTickDivisor(), config.getStaminaConsumptionReductionPerPoint(), config.getStaminaRegenSpeedMultiplierPerPoint(), config.getMaxStatPointsHealth(), config.getMaxStatPointsStamina(), config.getMaxStatPointsMana(), config.getMaxStatPointsAmmo(), config.getMaxStatPointsOxygen(), config.getMaxStatPointsStaminaRegenSpeed(), config.getMaxStatPointsStaminaConsumption(), config.getMaxStatPointsDamage(), config.getMaxStatPointsMining(), config.getMaxStatPointsWoodcutting(), config.getMaxStatPointsDefense(), String.join((CharSequence)",", config.getBlacklistedStats()));
    }

    private String formatConfigJsonFromMap(@Nonnull Map<String, Object> configMap) {
        String[] keys;
        StringBuilder json = new StringBuilder("{\n");
        boolean first = true;
        for (String key : keys = new String[]{"MaxLevel", "RateExp", "BaseXP", "LevelBaseXP", "LevelOffset", "StatPointsPerLevel", "StatValuePerPoint", "EnableHUD", "Debug", "ResetLevelOnDeath", "DamageStatValuePerPoint", "MiningStatValuePerPoint", "WoodcuttingStatValuePerPoint", "DefenseStatValuePerPoint", "DefenseMaxReductionRatio", "BlockDamageScalingDivisor", "MiningPerTickDivisor", "StaminaConsumptionReductionPerPoint", "StaminaRegenSpeedMultiplierPerPoint", "MaxStatPointsHealth", "MaxStatPointsStamina", "MaxStatPointsMana", "MaxStatPointsAmmo", "MaxStatPointsOxygen", "MaxStatPointsStaminaRegenSpeed", "MaxStatPointsStaminaConsumption", "MaxStatPointsDamage", "MaxStatPointsMining", "MaxStatPointsWoodcutting", "MaxStatPointsDefense", "BlacklistedStats"}) {
            if (!configMap.containsKey(key)) continue;
            if (!first) {
                json.append(",\n");
            }
            first = false;
            Object value = configMap.get(key);
            if (value instanceof Integer) {
                json.append("  \"").append(key).append("\": ").append(value);
                continue;
            }
            if (value instanceof Double) {
                json.append("  \"").append(key).append("\": ").append(String.format("%.2f", (Double)value));
                continue;
            }
            if (value instanceof Boolean) {
                json.append("  \"").append(key).append("\": ").append(value);
                continue;
            }
            json.append("  \"").append(key).append("\": \"").append(value).append("\"");
        }
        json.append("\n}");
        return json.toString();
    }

    static {
        DEBUG = DebugLogger.forEnclosingClass();
    }
}
