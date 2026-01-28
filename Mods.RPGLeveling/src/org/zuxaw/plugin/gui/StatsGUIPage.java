/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
 *  com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
 *  com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule
 *  com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes
 *  com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType
 *  com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier
 *  com.hypixel.hytale.server.core.ui.builder.EventData
 *  com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
 *  com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.gui;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.RPGLevelingPlugin;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.gui.StatsGUIEventData;
import org.zuxaw.plugin.services.LeaderboardService;
import org.zuxaw.plugin.services.LevelingService;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.services.StatsService;
import org.zuxaw.plugin.utils.DebugLogger;
import org.zuxaw.plugin.utils.LocalizationValidator;
import org.zuxaw.plugin.utils.Localized;

public class StatsGUIPage
extends InteractiveCustomUIPage<StatsGUIEventData> {
    private static final DebugLogger DEBUG = DebugLogger.forEnclosingClass();
    private final PlayerRef playerRef;
    private final LevelingService levelingService;
    private final StatsService statsService;
    private final LevelingConfig config;
    private final MessageService messageService;
    private final Map<String, Integer> pendingChanges = new HashMap<String, Integer>();

    public StatsGUIPage(@Nonnull PlayerRef playerRef, @Nonnull LevelingService levelingService, @Nonnull StatsService statsService, @Nonnull LevelingConfig config, @Nonnull MessageService messageService) {
        super(playerRef, CustomPageLifetime.CanDismiss, StatsGUIEventData.CODEC);
        this.playerRef = playerRef;
        this.levelingService = levelingService;
        this.statsService = statsService;
        this.config = config;
        this.messageService = messageService;
    }

    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        PlayerLevelData data;
        uiCommandBuilder.append("Pages/Stats/StatsGUI.ui");
        uiCommandBuilder.set("#NavStatsButton.Text", this.messageService.getMessage("gui.navigation.stats"));
        uiCommandBuilder.set("#NavStatsButtonSelected.Text", this.messageService.getMessage("gui.navigation.stats"));
        uiCommandBuilder.set("#NavLeaderboardButton.Text", this.messageService.getMessage("gui.navigation.leaderboard"));
        uiCommandBuilder.set("#NavLeaderboardButtonSelected.Text", this.messageService.getMessage("gui.navigation.leaderboard"));
        uiCommandBuilder.set("#SaveButton.Text", this.messageService.getButtonLabel("save"));
        uiCommandBuilder.set("#CancelButton.Text", this.messageService.getButtonLabel("cancel"));
        uiCommandBuilder.set("#LeaderboardTitle.Text", this.messageService.getMessage("gui.leaderboard.title"));
        uiCommandBuilder.set("#HeaderRank.Text", this.messageService.getMessage("gui.leaderboard.header_rank"));
        uiCommandBuilder.set("#HeaderPlayer.Text", this.messageService.getMessage("gui.leaderboard.header_player"));
        uiCommandBuilder.set("#HeaderLevel.Text", this.messageService.getMessage("gui.leaderboard.header_level"));
        uiCommandBuilder.set("#HeaderXP.Text", this.messageService.getMessage("gui.leaderboard.header_xp"));
        uiCommandBuilder.set("#EmptyMessage.Text", this.messageService.getMessage("gui.leaderboard.empty_message"));
        PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
        RPGLevelingPlugin plugin = RPGLevelingPlugin.get();
        if (plugin != null && playerRef != null) {
            plugin.markStatsGuiOpen(playerRef.getUuid());
        }
        if ((data = (PlayerLevelData)store.getComponent(ref, this.levelingService.getPlayerLevelDataType())) == null) {
            data = this.levelingService.getPlayerData(playerRef);
        }
        this.updateUIWithPending(ref, store, uiCommandBuilder, data);
        this.bindStatButtons(uiEventBuilder);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#NavStatsButton", EventData.of((String)"NavBar", (String)"stats").append("StatName", "").append("Action", "").append("Amount", "0"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#NavLeaderboardButton", EventData.of((String)"NavBar", (String)"leaderboard").append("StatName", "").append("Action", "").append("Amount", "0"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton", EventData.of((String)"Action", (String)"cancel").append("StatName", "").append("Amount", "0"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", EventData.of((String)"Action", (String)"save").append("StatName", "").append("Amount", "0"), false);
    }

    private void bindStatButtons(@Nonnull UIEventBuilder uiEventBuilder) {
        String[] statsWithUI;
        for (String statName : statsWithUI = new String[]{"Health", "Stamina", "StaminaRegenDelay", "StaminaConsumption", "Defense", "Damage", "Mana", "Ammo", "Oxygen", "Mining", "Woodcutting"}) {
            if (this.config.isStatBlacklisted(statName)) continue;
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#" + statName + "Decrement5", EventData.of((String)"StatName", (String)statName).append("Action", "decrement").append("Amount", "5"), false);
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#" + statName + "Decrement1", EventData.of((String)"StatName", (String)statName).append("Action", "decrement").append("Amount", "1"), false);
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#" + statName + "Increment1", EventData.of((String)"StatName", (String)statName).append("Action", "increment").append("Amount", "1"), false);
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#" + statName + "Increment5", EventData.of((String)"StatName", (String)statName).append("Action", "increment").append("Amount", "5"), false);
        }
    }

    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull StatsGUIEventData data) {
        super.handleDataEvent(ref, store, (Object)data);
        PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
        if (data.navBar != null && !data.navBar.isEmpty()) {
            this.applyNavBarTab(data.navBar);
            return;
        }
        if ("cancel".equals(data.action)) {
            this.handleCancel();
            return;
        }
        if ("save".equals(data.action)) {
            this.handleSave(ref, store, playerRef);
            return;
        }
        if (data.statName == null || data.statName.isEmpty() || data.action == null || data.amount == null) {
            DEBUG.warning(this.config, () -> "Invalid event data received: statName=" + data.statName + ", action=" + data.action + ", amount=" + data.amount);
            this.showError(this.messageService.getError("invalid_action", new Object[0]));
            this.sendUpdate();
            return;
        }
        if (!this.statsService.isValidStat(data.statName)) {
            DEBUG.warning(this.config, () -> "Invalid stat name: " + data.statName);
            this.showError(this.messageService.getError("invalid_stat", data.statName));
            this.sendUpdate();
            return;
        }
        if (this.config.isStatBlacklisted(data.statName)) {
            DEBUG.warning(this.config, () -> "Attempted to modify blacklisted stat: " + data.statName);
            this.showError(this.messageService.getError("stat_blacklisted", new Object[0]));
            this.sendUpdate();
            return;
        }
        PlayerLevelData playerData = (PlayerLevelData)store.getComponent(ref, this.levelingService.getPlayerLevelDataType());
        if (playerData == null) {
            playerData = this.levelingService.getPlayerData(playerRef);
        }
        if ("increment".equals(data.action)) {
            this.handleIncrement(ref, store, data, playerData, playerRef);
        } else if ("decrement".equals(data.action)) {
            this.handleDecrement(ref, store, data, playerData, playerRef);
        } else {
            DEBUG.warning(this.config, () -> "Unknown action: " + data.action);
            this.showError(this.messageService.getError("unknown_action", data.action));
            this.sendUpdate();
        }
    }

    private void handleIncrement(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull StatsGUIEventData data, @Nonnull PlayerLevelData playerData, @Nonnull PlayerRef playerRef) {
        int maxStatPoints;
        int currentPending;
        int amount = data.amount;
        if (amount <= 0) {
            this.showError(this.messageService.getError("invalid_amount", amount));
            this.sendUpdate();
            return;
        }
        int currentAllocation = playerData.getAllocatedPoints(data.statName);
        int totalAfterIncrement = currentAllocation + (currentPending = this.pendingChanges.getOrDefault(data.statName, 0).intValue()) + amount;
        if (totalAfterIncrement > (maxStatPoints = this.config.getMaxStatPointsForStat(data.statName))) {
            this.showError(this.messageService.getError("max_points_exceeded", maxStatPoints, data.statName, currentAllocation, currentPending));
            this.sendUpdate();
            return;
        }
        int totalPending = this.pendingChanges.values().stream().mapToInt(Integer::intValue).sum();
        if (playerData.getAvailableStatPoints() < totalPending + amount) {
            this.showError(this.messageService.getError("not_enough_points", playerData.getAvailableStatPoints()));
            this.sendUpdate();
            return;
        }
        this.pendingChanges.put(data.statName, currentPending + amount);
        this.clearError();
        this.updateUIWithPending(ref, store, playerData);
    }

    private void handleDecrement(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull StatsGUIEventData data, @Nonnull PlayerLevelData playerData, @Nonnull PlayerRef playerRef) {
        int amount = data.amount;
        if (amount <= 0) {
            this.showError(this.messageService.getError("invalid_amount", amount));
            this.sendUpdate();
            return;
        }
        int currentPending = this.pendingChanges.getOrDefault(data.statName, 0);
        if (currentPending < amount) {
            this.showError(this.messageService.getError("not_enough_pending", currentPending, data.statName, amount));
            this.sendUpdate();
            return;
        }
        int newPending = currentPending - amount;
        if (newPending <= 0) {
            this.pendingChanges.remove(data.statName);
        } else {
            this.pendingChanges.put(data.statName, newPending);
        }
        this.clearError();
        this.updateUIWithPending(ref, store, playerData);
    }

    public void onDismiss(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        RPGLevelingPlugin plugin = RPGLevelingPlugin.get();
        if (plugin != null) {
            plugin.markStatsGuiClosed(this.playerRef.getUuid());
        }
        super.onDismiss(ref, store);
    }

    protected void close() {
        try {
            RPGLevelingPlugin plugin = RPGLevelingPlugin.get();
            if (plugin != null) {
                plugin.markStatsGuiClosed(this.playerRef.getUuid());
            }
        }
        finally {
            super.close();
        }
    }

    private void handleCancel() {
        this.pendingChanges.clear();
        this.close();
    }

    private void applyNavBarTab(String id) {
        boolean statsActive = "stats".equals(id);
        UICommandBuilder b = new UICommandBuilder();
        b.set("#StatsPage.Visible", statsActive);
        b.set("#LeaderboardPage.Visible", !statsActive);
        if (statsActive) {
            b.set("#NavStatsButtonSelected.Visible", true);
            b.set("#NavStatsButton.Visible", false);
            b.set("#NavLeaderboardButtonSelected.Visible", false);
            b.set("#NavLeaderboardButton.Visible", true);
        } else {
            b.set("#NavStatsButtonSelected.Visible", false);
            b.set("#NavStatsButton.Visible", true);
            b.set("#NavLeaderboardButtonSelected.Visible", true);
            b.set("#NavLeaderboardButton.Visible", false);
            this.buildLeaderboard(b);
        }
        this.sendUpdate(b);
    }

    private void buildLeaderboard(@Nonnull UICommandBuilder b) {
        RPGLevelingPlugin plugin = RPGLevelingPlugin.get();
        if (plugin == null) {
            DEBUG.warning(this.config, () -> "Plugin instance is null, cannot build leaderboard");
            b.set("#EmptyMessage.Visible", true);
            return;
        }
        LeaderboardService leaderboardService = plugin.getLeaderboardService();
        if (leaderboardService == null) {
            DEBUG.warning(this.config, () -> "LeaderboardService is null, cannot build leaderboard");
            b.set("#EmptyMessage.Visible", true);
            return;
        }
        List<LeaderboardService.PlayerEntry> players = leaderboardService.getTopPlayers(100);
        if (players.isEmpty()) {
            b.set("#EmptyMessage.Visible", true);
            return;
        }
        b.set("#EmptyMessage.Visible", false);
        b.clear("#LeaderboardEntries");
        for (LeaderboardService.PlayerEntry entry : players) {
            String rankColor;
            String rankText;
            int rank = entry.getRank();
            if (rank == 1) {
                rankText = String.valueOf(rank);
                rankColor = "#FFD700";
            } else if (rank == 2) {
                rankText = String.valueOf(rank);
                rankColor = "#C0C0C0";
            } else if (rank == 3) {
                rankText = String.valueOf(rank);
                rankColor = "#CD7F32";
            } else {
                rankText = String.valueOf(rank);
                rankColor = "#ffffff";
            }
            boolean isCurrentPlayer = entry.getUuid().equals(this.playerRef.getUuid());
            String backgroundColor = isCurrentPlayer ? "#2a4d7a(0.4)" : (rank % 2 == 0 ? "#1a1a1a(0.3)" : "#1a1a1a(0.1)");
            String levelText = this.messageService.getMessage("gui.leaderboard.level_display", entry.getLevel());
            String xpText = this.messageService.getMessage("gui.leaderboard.xp_display", entry.getExperience());
            String entryUI = String.format("Group {   LayoutMode: Left;   Anchor: (Height: 45);   Background: %s;   Padding: (Full: 10, Left: 15, Right: 15);   Label {     Style: (FontSize: 16, TextColor: %s, RenderBold: %s);     Anchor: (Width: 80);     Text: \"%s\";   }   Label {     Style: (FontSize: 16, TextColor: %s, RenderBold: %s);     Anchor: (Width: 300);     Text: \"%s\";   }   Label {     Style: (FontSize: 16, TextColor: #90ee90, RenderBold: true);     Anchor: (Width: 120);     Text: \"%s\";   }   Label {     Style: (FontSize: 14, TextColor: #aaaaaa, RenderBold: false);     FlexWeight: 1;     Text: \"%s\";   } }", backgroundColor, rankColor, rank <= 3 ? "true" : "false", rankText, isCurrentPlayer ? "#5a8bd8" : "#ffffff", isCurrentPlayer ? "true" : "false", entry.getUsername(), levelText, xpText);
            b.appendInline("#LeaderboardEntries", entryUI);
        }
    }

    private void handleSave(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PlayerRef playerRef) {
        if (this.pendingChanges.isEmpty()) {
            this.showError(this.messageService.getError("no_changes_to_save", new Object[0]));
            this.sendUpdate();
            return;
        }
        try {
            PlayerLevelData playerData = (PlayerLevelData)store.getComponent(ref, this.levelingService.getPlayerLevelDataType());
            if (playerData == null) {
                playerData = this.levelingService.getPlayerData(playerRef);
            }
            if (playerData == null) {
                this.showError(this.messageService.getError("unable_to_access_data", new Object[0]));
                this.sendUpdate();
                return;
            }
            int totalPending = this.pendingChanges.values().stream().mapToInt(Integer::intValue).sum();
            if (playerData.getAvailableStatPoints() < totalPending) {
                this.showError(this.messageService.getError("not_enough_points_save", playerData.getAvailableStatPoints(), totalPending));
                this.sendUpdate();
                return;
            }
            for (Map.Entry<String, Integer> entry : this.pendingChanges.entrySet()) {
                int maxStatPoints;
                String statName = entry.getKey();
                int amount = entry.getValue();
                if (!this.statsService.isValidStat(statName)) {
                    DEBUG.warning(this.config, () -> "Invalid stat name during save: " + statName);
                    this.showError(this.messageService.getError("invalid_stat", statName));
                    this.sendUpdate();
                    return;
                }
                int currentAllocation = playerData.getAllocatedPoints(statName);
                int newAllocation = currentAllocation + amount;
                if (newAllocation > (maxStatPoints = this.config.getMaxStatPointsForStat(statName))) {
                    this.showError(this.messageService.getError("max_points_save_exceeded", maxStatPoints, statName, currentAllocation, amount));
                    this.sendUpdate();
                    return;
                }
                if (playerData.getAvailableStatPoints() < amount) {
                    this.showError(this.messageService.getError("not_enough_points_save", playerData.getAvailableStatPoints(), amount));
                    this.sendUpdate();
                    return;
                }
                playerData.setAvailableStatPoints(playerData.getAvailableStatPoints() - amount);
                playerData.allocatePoints(statName, newAllocation);
            }
            store.putComponent(ref, this.levelingService.getPlayerLevelDataType(), (Component)playerData);
            Holder holder = playerRef.getHolder();
            if (holder != null) {
                holder.putComponent(this.levelingService.getPlayerLevelDataType(), (Component)((PlayerLevelData)playerData.clone()));
            }
            this.statsService.applyStatModifiers(ref, store, playerData, this.config);
            this.pendingChanges.clear();
            this.close();
        }
        catch (Exception e) {
            DEBUG.severe(this.config, () -> "Error saving stat changes for player " + playerRef.getUsername() + ": " + e.getMessage());
            this.showError(this.messageService.getError("save_error", new Object[0]));
            this.sendUpdate();
        }
    }

    private void updateUIWithPending(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PlayerLevelData data) {
        UICommandBuilder builder = new UICommandBuilder();
        this.updateUIWithPending(ref, store, builder, data);
        this.sendUpdate(builder);
    }

    private void updateUIWithPending(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull UICommandBuilder builder, @Nonnull PlayerLevelData data) {
        String xpProgressText;
        int totalPending = this.pendingChanges.values().stream().mapToInt(Integer::intValue).sum();
        int availableAfterPending = data.getAvailableStatPoints() - totalPending;
        int currentLevel = data.getLevel();
        int maxLevel = this.config.getMaxLevel();
        PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
        String pseudo = playerRef != null && playerRef.getUsername() != null ? playerRef.getUsername() : "";
        builder.set("#LevelLabel.Text", this.messageService.getGuiLabel("player_level", pseudo, currentLevel));
        double currentXP = data.getExperience();
        double progressValue = 0.0;
        if (currentLevel >= maxLevel) {
            progressValue = 1.0;
            xpProgressText = String.format("%.1f / %.1f XP (MAX)", currentXP, currentXP);
        } else {
            double xpNeeded = this.levelingService.getXPNeededForNextLevel(currentLevel, this.config);
            if (xpNeeded > 0.0) {
                progressValue = Math.min(1.0, Math.max(0.0, currentXP / xpNeeded));
                int progressPercent = (int)(progressValue * 100.0);
                xpProgressText = String.format("%.1f / %.1f XP (%d%%)", currentXP, xpNeeded, progressPercent);
            } else {
                progressValue = 1.0;
                xpProgressText = String.format("%.1f / %.1f XP", currentXP, xpNeeded);
            }
        }
        builder.set("#ProgressBar.Value", (float)progressValue);
        builder.set("#XPProgressLabel.Text", xpProgressText);
        builder.set("#AvailablePointsLabel.Text", this.messageService.getGuiLabel("available_points", availableAfterPending));
        EntityStatMap statMap = (EntityStatMap)store.getComponent(ref, EntityStatsModule.get().getEntityStatMapComponentType());
        Map<String, Integer> allocatedStats = data.getAllocatedStats();
        String[] statsWithUI = new String[]{"Health", "Stamina", "StaminaRegenDelay", "StaminaConsumption", "Defense", "Damage", "Mana", "Ammo", "Oxygen", "Mining", "Woodcutting"};
        HashMap<String, Integer> normalizedStats = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : allocatedStats.entrySet()) {
            String normalizedKey = this.normalizeStatName(entry.getKey());
            normalizedStats.put(normalizedKey, entry.getValue());
        }
        for (String statName : statsWithUI) {
            if (this.config.isStatBlacklisted(statName)) {
                builder.set("#" + statName + "Group.Visible", false);
                continue;
            }
            builder.set("#" + statName + "Group.Visible", true);
            int allocatedPoints = normalizedStats.getOrDefault(statName, 0);
            int pendingPoints = this.pendingChanges.getOrDefault(statName, 0);
            float currentValue = 0.0f;
            boolean foundInStatMap = false;
            if (statName.equalsIgnoreCase("StaminaRegenDelay") || statName.equalsIgnoreCase("StaminaConsumption")) {
                currentValue = allocatedPoints + pendingPoints;
            } else {
                EntityStatValue statValue;
                int statIndex;
                if (statMap != null && (statIndex = this.getStatIndex(statName)) >= 0 && (statValue = statMap.get(statIndex)) != null) {
                    Modifier ourModifier;
                    float actualValue = statValue.get();
                    float maxValue = statValue.getMax();
                    Map modifiers = statValue.getModifiers();
                    boolean hasModifiers = modifiers != null && !modifiers.isEmpty();
                    String modifierKey = "RPGLeveling_" + statName + "_Bonus";
                    Modifier modifier = ourModifier = modifiers != null ? (Modifier)modifiers.get(modifierKey) : null;
                    if (!hasModifiers && actualValue == 0.0f) {
                        currentValue = 0.0f;
                    } else if (ourModifier != null && actualValue == 0.0f) {
                        float modifierValue;
                        double statValuePerPointForStat = this.getStatValuePerPoint(statName);
                        currentValue = modifierValue = (float)allocatedPoints * (float)statValuePerPointForStat;
                    } else {
                        currentValue = maxValue;
                    }
                    foundInStatMap = true;
                }
                if (!foundInStatMap) {
                    int totalAllocatedPoints = 0;
                    statValue = allocatedStats.entrySet().iterator();
                    while (statValue.hasNext()) {
                        Map.Entry entry = (Map.Entry)statValue.next();
                        if (!statName.equalsIgnoreCase((String)entry.getKey())) continue;
                        totalAllocatedPoints += ((Integer)entry.getValue()).intValue();
                    }
                    currentValue = totalAllocatedPoints;
                }
            }
            String displayName = this.messageService.getStatName(this.normalizeStatNameForMessage(statName));
            builder.set("#" + statName + "Allocation.Text", String.format("%s %.0f", displayName, Float.valueOf(currentValue)));
            String iconId = this.getStatIconId(statName);
            if (iconId != null) {
                builder.set("#" + statName + "Icon.ItemId", iconId);
            }
            String description = this.messageService.getStatDescription(this.normalizeStatNameForMessage(statName));
            builder.set("#" + statName + "Description.Text", description);
            if (pendingPoints > 0) {
                if (!foundInStatMap) {
                    builder.set("#" + statName + "Bonus.Text", String.format("+%d", pendingPoints));
                } else {
                    double statValuePerPointForStat = this.getStatValuePerPoint(statName);
                    float pendingBonus = (float)pendingPoints * (float)statValuePerPointForStat;
                    builder.set("#" + statName + "Bonus.Text", String.format("+%.0f", Float.valueOf(pendingBonus)));
                }
                builder.set("#" + statName + "Bonus.Visible", true);
            } else {
                builder.set("#" + statName + "Bonus.Visible", false);
            }
            boolean hasPending = pendingPoints > 0;
            builder.set("#" + statName + "Decrement1.Visible", hasPending && pendingPoints >= 1);
            builder.set("#" + statName + "Decrement5.Visible", hasPending && pendingPoints >= 5);
            int maxStatPoints = this.config.getMaxStatPointsForStat(statName);
            int totalWithPending = allocatedPoints + pendingPoints;
            boolean canIncrement1 = availableAfterPending >= 1 && totalWithPending < maxStatPoints;
            boolean canIncrement5 = availableAfterPending >= 5 && totalWithPending + 5 <= maxStatPoints;
            builder.set("#" + statName + "Increment1.Visible", canIncrement1);
            builder.set("#" + statName + "Increment5.Visible", canIncrement5);
        }
        boolean hasChanges = !this.pendingChanges.isEmpty();
        builder.set("#SaveButton.Visible", hasChanges);
        builder.set("#CancelButton.Visible", hasChanges);
    }

    private int getStatIndex(String statName) {
        switch (statName.toLowerCase()) {
            case "health": {
                return DefaultEntityStatTypes.getHealth();
            }
            case "stamina": {
                return DefaultEntityStatTypes.getStamina();
            }
            case "mana": {
                return DefaultEntityStatTypes.getMana();
            }
            case "ammo": {
                return DefaultEntityStatTypes.getAmmo();
            }
            case "oxygen": {
                return DefaultEntityStatTypes.getOxygen();
            }
            case "staminaregendelay": {
                try {
                    return EntityStatType.getAssetMap().getIndex((Object)"StaminaRegenDelay");
                }
                catch (Exception e) {
                    return -1;
                }
            }
        }
        return -1;
    }

    private double getStatValuePerPoint(String statName) {
        switch (statName.toLowerCase()) {
            case "damage": {
                return this.config.getDamageStatValuePerPoint();
            }
            case "mining": {
                return this.config.getMiningStatValuePerPoint();
            }
            case "woodcutting": {
                return this.config.getWoodcuttingStatValuePerPoint();
            }
            case "defense": {
                return this.config.getDefenseStatValuePerPoint();
            }
        }
        return this.config.getStatValuePerPoint();
    }

    private String getStatIconId(String statName) {
        if (statName == null) {
            return null;
        }
        switch (statName.toLowerCase()) {
            case "mining": {
                return "Tool_Pickaxe_Iron";
            }
            case "woodcutting": {
                return "Tool_Hatchet_Iron";
            }
            case "health": {
                return "Potion_Health";
            }
            case "stamina": {
                return "Potion_Stamina";
            }
            case "staminaregendelay": {
                return "Potion_Stamina";
            }
            case "staminaconsumption": {
                return "Potion_Stamina";
            }
            case "defense": {
                return "Weapon_Shield_Onyxium";
            }
            case "damage": {
                return "Weapon_Longsword_Flame";
            }
            case "mana": {
                return "Weapon_Wand_Wood";
            }
            case "ammo": {
                return "Weapon_Crossbow_Ancient_Steel";
            }
            case "oxygen": {
                return "Container_Bucket";
            }
        }
        return null;
    }

    private String normalizeStatNameForMessage(String statName) {
        if (statName == null) {
            return "";
        }
        String normalized = statName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        return normalized;
    }

    private String normalizeStatName(String statName) {
        if (statName == null || statName.isEmpty()) {
            return statName;
        }
        for (String validStat : StatsService.VALID_STATS) {
            if (!validStat.equalsIgnoreCase(statName)) continue;
            return validStat;
        }
        return statName.substring(0, 1).toUpperCase() + statName.substring(1).toLowerCase();
    }

    @Localized(category="errors")
    private void showError(@Nonnull String message) {
        LocalizationValidator.validateErrorMessage(message, ((Object)((Object)this)).getClass());
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#ErrorLabel.Text", message);
        this.sendUpdate(builder);
    }

    private void clearError() {
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#ErrorLabel.Text", "");
        this.sendUpdate(builder);
    }
}
