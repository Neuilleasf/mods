/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class MessageService {
    private static final String MESSAGES_FILE_NAME = "messages.json";
    private static final String PLUGIN_FOLDER = "RPGLeveling";
    private static final String VERSION_KEY = "_version";
    private static final int CURRENT_VERSION = 1;
    private JsonObject messages;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File messagesFile;
    private final Map<String, Object> defaultMessages;

    public MessageService() {
        String workingDir = System.getProperty("user.dir");
        Path pluginDir = Paths.get(workingDir, "mods", PLUGIN_FOLDER);
        this.messagesFile = new File(pluginDir.toFile(), MESSAGES_FILE_NAME);
        pluginDir.toFile().mkdirs();
        this.defaultMessages = this.createDefaultMessages();
        if (!this.messagesFile.exists()) {
            this.generateDefaultMessagesFile();
        } else {
            this.mergeMessagesFile();
        }
        this.loadMessagesFromFile();
    }

    private void generateDefaultMessagesFile() {
        try (FileWriter writer = new FileWriter(this.messagesFile);){
            this.gson.toJson(this.defaultMessages, (Appendable)writer);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void mergeMessagesFile() {
        block12: {
            try (FileReader reader = new FileReader(this.messagesFile);){
                JsonObject existingMessages = (JsonObject)this.gson.fromJson((Reader)reader, JsonObject.class);
                JsonObject defaultMessagesJson = this.gson.toJsonTree(this.defaultMessages).getAsJsonObject();
                MergeResult result = this.mergeJsonObjects(existingMessages, defaultMessagesJson, "");
                if (!result.hasChanges()) break block12;
                try (FileWriter writer = new FileWriter(this.messagesFile);){
                    this.gson.toJson((JsonElement)result.mergedJson, (Appendable)writer);
                }
            }
            catch (IOException e) {
                this.generateDefaultMessagesFile();
            }
        }
    }

    private MergeResult mergeJsonObjects(@Nonnull JsonObject existing, @Nonnull JsonObject defaults, @Nonnull String pathPrefix) {
        String fullPath;
        MergeResult result = new MergeResult();
        result.mergedJson = new JsonObject();
        HashSet<String> defaultKeys = new HashSet<String>();
        for (String key : defaults.keySet()) {
            if (key.startsWith("_")) continue;
            defaultKeys.add(key);
        }
        HashSet<String> existingKeys = new HashSet<String>();
        for (String key : existing.keySet()) {
            if (key.startsWith("_")) continue;
            existingKeys.add(key);
        }
        for (String key : defaultKeys) {
            fullPath = pathPrefix.isEmpty() ? key : pathPrefix + "." + key;
            JsonElement defaultElement = defaults.get(key);
            JsonElement existingElement = existing.get(key);
            if (defaultElement.isJsonObject()) {
                if (existingElement != null && existingElement.isJsonObject()) {
                    MergeResult nestedResult = this.mergeJsonObjects(existingElement.getAsJsonObject(), defaultElement.getAsJsonObject(), fullPath);
                    result.mergedJson.add(key, (JsonElement)nestedResult.mergedJson);
                    result.combine(nestedResult);
                    continue;
                }
                result.mergedJson.add(key, defaultElement);
                result.addedKeys.add(fullPath);
                result.countNestedKeys(defaultElement.getAsJsonObject(), fullPath, result.addedKeys);
                continue;
            }
            if (existingElement == null) {
                result.mergedJson.add(key, defaultElement);
                result.addedKeys.add(fullPath);
                continue;
            }
            if (existingElement.isJsonPrimitive()) {
                String existingValue;
                String defaultValue = defaultElement.getAsString();
                if (!defaultValue.equals(existingValue = existingElement.getAsString())) {
                    result.mergedJson.add(key, existingElement);
                    ++result.preservedCount;
                    continue;
                }
                result.mergedJson.add(key, existingElement);
                continue;
            }
            result.mergedJson.add(key, defaultElement);
            result.updatedKeys.add(fullPath);
        }
        for (String key : existingKeys) {
            if (defaultKeys.contains(key)) continue;
            fullPath = pathPrefix.isEmpty() ? key : pathPrefix + "." + key;
            result.removedKeys.add(fullPath);
        }
        return result;
    }

    private void loadMessagesFromFile() {
        try (FileReader reader = new FileReader(this.messagesFile);){
            this.messages = (JsonObject)this.gson.fromJson((Reader)reader, JsonObject.class);
        }
        catch (IOException e) {
            this.messages = this.gson.toJsonTree(this.defaultMessages).getAsJsonObject();
        }
    }

    private Map<String, Object> createDefaultMessages() {
        LinkedHashMap<String, Object> root = new LinkedHashMap<String, Object>();
        LinkedHashMap gui = new LinkedHashMap();
        LinkedHashMap<String, String> hud = new LinkedHashMap<String, String>();
        hud.put("level_xp_progress", "Level: {0} XP: {1} / {2}");
        hud.put("level_xp_max", "Level: {0} XP: {1} / {2} (MAX)");
        root.put("hud", hud);
        LinkedHashMap<String, String> navigation = new LinkedHashMap<String, String>();
        navigation.put("stats", "STATS MANAGEMENT");
        navigation.put("leaderboard", "LEADERBOARD");
        gui.put("navigation", navigation);
        LinkedHashMap<String, String> buttons = new LinkedHashMap<String, String>();
        buttons.put("save", "Save");
        buttons.put("cancel", "Cancel");
        gui.put("buttons", buttons);
        LinkedHashMap<String, String> labels = new LinkedHashMap<String, String>();
        labels.put("level", "Level");
        labels.put("available_points", "Available Stat Points: {0}");
        labels.put("player_level", "{0} - Level {1}");
        gui.put("labels", labels);
        LinkedHashMap<String, String> leaderboard = new LinkedHashMap<String, String>();
        leaderboard.put("title", "Leaderboard - Top Players");
        leaderboard.put("header_rank", "Rank");
        leaderboard.put("header_player", "Player");
        leaderboard.put("header_level", "Level");
        leaderboard.put("header_xp", "Experience");
        leaderboard.put("empty_message", "No players found. Play to appear on the leaderboard!");
        leaderboard.put("level_display", "Level {0}");
        leaderboard.put("xp_display", "{0} XP");
        gui.put("leaderboard", leaderboard);
        root.put("gui", gui);
        LinkedHashMap stats = new LinkedHashMap();
        LinkedHashMap<String, String> statNames = new LinkedHashMap<String, String>();
        statNames.put("health", "Health");
        statNames.put("stamina", "Stamina");
        statNames.put("stamina_regen_delay", "Stamina Regen Speed");
        statNames.put("stamina_consumption", "Stamina Consumption");
        statNames.put("defense", "Defense");
        statNames.put("damage", "Damage");
        statNames.put("mana", "Mana");
        statNames.put("ammo", "Ammo");
        statNames.put("oxygen", "Oxygen");
        statNames.put("mining", "Mining");
        statNames.put("woodcutting", "Woodcutting");
        stats.put("names", statNames);
        LinkedHashMap<String, String> statDescs = new LinkedHashMap<String, String>();
        statDescs.put("health", "Increases maximum health");
        statDescs.put("stamina", "Increases maximum stamina");
        statDescs.put("stamina_regen_delay", "Increases stamina regeneration speed (10x faster at 50 points)");
        statDescs.put("stamina_consumption", "Reduces stamina consumption when sprinting (50% less at 50 points)");
        statDescs.put("defense", "Increases damage reduction");
        statDescs.put("damage", "Increases melee damage");
        statDescs.put("mana", "Increases maximum mana");
        statDescs.put("ammo", "Increases maximum ammo");
        statDescs.put("oxygen", "Increases underwater breathing time");
        statDescs.put("mining", "Increases mining speed for ores and stone");
        statDescs.put("woodcutting", "Increases woodcutting speed for logs and wood");
        stats.put("descriptions", statDescs);
        root.put("stats", stats);
        LinkedHashMap<String, String> errors = new LinkedHashMap<String, String>();
        errors.put("invalid_action", "Invalid action. Please try again.");
        errors.put("invalid_stat", "Invalid stat name: {0}");
        errors.put("stat_blacklisted", "This stat is blacklisted and cannot be modified.");
        errors.put("unknown_action", "Unknown action: {0}");
        errors.put("invalid_amount", "Invalid amount: {0}");
        errors.put("max_points_exceeded", "Cannot exceed maximum of {0} points for {1}! (Current: {2}, Pending: {3})");
        errors.put("not_enough_points", "Not enough stat points! You have {0} available.");
        errors.put("not_enough_pending", "Not enough pending points! You have {0} pending for {1} but tried to remove {2}.");
        errors.put("no_changes_to_save", "No changes to save!");
        errors.put("unable_to_access_data", "Unable to access player data. Please try again.");
        errors.put("save_error", "An error occurred while saving. Please try again.");
        errors.put("max_points_save_exceeded", "Cannot exceed maximum of {0} points for {1}! (Current: {2}, Trying to add: {3})");
        errors.put("not_enough_points_save", "Not enough stat points! You have {0} but need {1}.");
        root.put("errors", errors);
        LinkedHashMap<String, String> notifications = new LinkedHashMap<String, String>();
        notifications.put("level_up_title", "LEVEL UP!");
        notifications.put("level_up_subtitle", "You are now level {0}!");
        notifications.put("max_level_title", "CONGRATULATIONS!");
        notifications.put("max_level_subtitle", "You have reached the maximum level {0}!");
        notifications.put("stat_points_earned", "You earned {0} stat point{1}!");
        notifications.put("entity_killed", "You killed: {0}");
        notifications.put("level_reset_death_title", "YOU DIED!");
        notifications.put("level_reset_death_subtitle", "Your level has been reset to 1");
        root.put("notifications", notifications);
        LinkedHashMap commands = new LinkedHashMap();
        LinkedHashMap<String, String> guiCmd = new LinkedHashMap<String, String>();
        guiCmd.put("description", "Open the stats management GUI.");
        guiCmd.put("error_entity_data", "Unable to access your entity data. Please try again in a moment.");
        guiCmd.put("error_players_only", "This command can only be used by players.");
        commands.put("gui", guiCmd);
        LinkedHashMap<String, String> infoCmd = new LinkedHashMap<String, String>();
        infoCmd.put("description", "Show all stats information and available commands.");
        infoCmd.put("error_players_only", "This command can only be used by players.");
        infoCmd.put("error_no_player_data", "Unable to find your player data.");
        infoCmd.put("header", "=== RPG Leveling Info ===");
        infoCmd.put("available_stats", "Available Stats:");
        infoCmd.put("stats_list", "{0} (+{1} max per point)");
        infoCmd.put("available_commands", "Available Commands:");
        infoCmd.put("cmd_gui", "Open stats management GUI");
        infoCmd.put("cmd_info", "Show this information");
        infoCmd.put("admin_commands", "Admin Commands:");
        infoCmd.put("cmd_setlevel", "Set player level");
        infoCmd.put("cmd_setpoints", "Set available stat points");
        infoCmd.put("cmd_addxp", "Add experience points to player");
        infoCmd.put("cmd_resetstats", "Reset allocated stats");
        infoCmd.put("admin_available", "Admin commands available (requires OP permission)");
        commands.put("info", infoCmd);
        LinkedHashMap<String, String> setlevelCmd = new LinkedHashMap<String, String>();
        setlevelCmd.put("description", "Set a player's level (admin only).");
        setlevelCmd.put("player_not_found", "Player {0} not found or not online.");
        setlevelCmd.put("invalid_level", "Level must be between 1 and {0}.");
        setlevelCmd.put("success", "Set {0} to level {1}.");
        setlevelCmd.put("error", "Failed to set player level. Please try again.");
        commands.put("setlevel", setlevelCmd);
        LinkedHashMap<String, String> setpointsCmd = new LinkedHashMap<String, String>();
        setpointsCmd.put("description", "Set available stat points for a player (admin only).");
        setpointsCmd.put("player_not_found", "Player {0} not found or not online.");
        setpointsCmd.put("invalid_points", "Points must be 0 or greater.");
        setpointsCmd.put("success", "Set {0}'s available stat points to {1}.");
        setpointsCmd.put("error_no_world", "Player {0} is not in a world and has no holder data.");
        commands.put("setpoints", setpointsCmd);
        LinkedHashMap<String, String> resetstatsCmd = new LinkedHashMap<String, String>();
        resetstatsCmd.put("description", "Reset a player's allocated stats (admin only).");
        resetstatsCmd.put("player_not_found", "Player {0} not found or not online.");
        resetstatsCmd.put("success", "Reset stats for {0}. Allocated points have been returned to available points.");
        resetstatsCmd.put("error", "Failed to reset player stats. Please try again.");
        commands.put("resetstats", resetstatsCmd);
        LinkedHashMap<String, String> addxpCmd = new LinkedHashMap<String, String>();
        addxpCmd.put("description", "Add experience points to a player (admin only).");
        addxpCmd.put("player_not_found", "Player {0} not found or not online.");
        addxpCmd.put("invalid_xp", "XP amount must be greater than 0.");
        addxpCmd.put("success", "Added {0} XP to {1}.");
        commands.put("addxp", addxpCmd);
        root.put("commands", commands);
        return root;
    }

    public void reload() {
        this.loadMessagesFromFile();
    }

    public void forceMerge() {
        this.mergeMessagesFile();
        this.loadMessagesFromFile();
    }

    @Nonnull
    public String getMessage(@Nonnull String path) {
        String[] parts = path.split("\\.");
        JsonObject current = this.messages;
        for (int i = 0; i < parts.length - 1; ++i) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                return path;
            }
            current = current.getAsJsonObject(parts[i]);
        }
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart) && current.get(lastPart).isJsonPrimitive()) {
            return current.get(lastPart).getAsString();
        }
        return path;
    }

    @Nonnull
    public String getMessage(@Nonnull String path, Object ... args) {
        String message = this.getMessage(path);
        if (args.length == 0) {
            return message;
        }
        try {
            return MessageFormat.format(message, args);
        }
        catch (Exception e) {
            return message;
        }
    }

    @Nonnull
    public String getStatName(@Nonnull String statKey) {
        return this.getMessage("stats.names." + statKey.toLowerCase());
    }

    @Nonnull
    public String getStatDescription(@Nonnull String statKey) {
        return this.getMessage("stats.descriptions." + statKey.toLowerCase());
    }

    @Nonnull
    public String getGuiLabel(@Nonnull String key, Object ... args) {
        return this.getMessage("gui.labels." + key, args);
    }

    @Nonnull
    public String getButtonLabel(@Nonnull String key) {
        return this.getMessage("gui.buttons." + key);
    }

    @Nonnull
    public String getHudMessage(@Nonnull String hudKey, Object ... args) {
        return this.getMessage("hud." + hudKey, args);
    }

    @Nonnull
    public String getError(@Nonnull String key, Object ... args) {
        return this.getMessage("errors." + key, args);
    }

    @Nonnull
    public String getNotification(@Nonnull String key, Object ... args) {
        return this.getMessage("notifications." + key, args);
    }

    @Nonnull
    public String getCommand(@Nonnull String commandKey, @Nonnull String messageKey, Object ... args) {
        return this.getMessage("commands." + commandKey + "." + messageKey, args);
    }

    private static class MergeResult {
        JsonObject mergedJson;
        List<String> addedKeys = new ArrayList<String>();
        List<String> updatedKeys = new ArrayList<String>();
        List<String> removedKeys = new ArrayList<String>();
        int preservedCount = 0;

        private MergeResult() {
        }

        boolean hasChanges() {
            return !this.addedKeys.isEmpty() || !this.updatedKeys.isEmpty() || !this.removedKeys.isEmpty();
        }

        void combine(MergeResult other) {
            this.addedKeys.addAll(other.addedKeys);
            this.updatedKeys.addAll(other.updatedKeys);
            this.removedKeys.addAll(other.removedKeys);
            this.preservedCount += other.preservedCount;
        }

        void countNestedKeys(JsonObject obj, String prefix, List<String> keyList) {
            for (String key : obj.keySet()) {
                String fullPath = prefix + "." + key;
                JsonElement element = obj.get(key);
                if (!element.isJsonObject()) continue;
                this.countNestedKeys(element.getAsJsonObject(), fullPath, keyList);
            }
        }
    }
}
