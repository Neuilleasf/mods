/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.reflect.TypeToken
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.inventory.Inventory
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.inventory.container.EmptyItemContainer
 *  com.hypixel.hytale.server.core.inventory.container.ItemContainer
 *  com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer
 *  org.bson.BsonDocument
 */
package com.natamus.hybrid.functions;

import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.EmptyItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.natamus.hybrid.data.HybridConstants;
import com.natamus.hybrid.functions.FileFunctions;
import com.natamus.hybrid.functions.ItemStackFunctions;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bson.BsonDocument;

public class InventoryFunctions {
    public static boolean isInventoryEmpty(Player player) {
        return InventoryFunctions.isInventoryEmpty(player.getInventory());
    }

    public static boolean isInventoryEmpty(Inventory inventory) {
        HashMap<String, ItemContainer> itemContainers = InventoryFunctions.getAllItemContainers(inventory);
        for (String identifier : InventoryFunctions.getInventoryIdentifiers()) {
            ItemContainer itemContainer = itemContainers.get(identifier);
            if (itemContainer == null) continue;
            short capacity = itemContainer.getCapacity();
            for (short i = 0; i < capacity; i = (short)(i + 1)) {
                ItemStack itemStack = itemContainer.getItemStack(i);
                if (itemStack == null || itemStack.isEmpty()) continue;
                return false;
            }
        }
        return true;
    }

    public static String getInventoryData(Player player) {
        return InventoryFunctions.getInventoryData(player.getInventory());
    }

    public static String getInventoryData(Inventory inventory) {
        StringBuilder inventoryData = new StringBuilder();
        HashMap<String, ItemContainer> itemContainers = InventoryFunctions.getAllItemContainers(inventory);
        for (String identifier : InventoryFunctions.getInventoryIdentifiers()) {
            ItemContainer itemContainer = itemContainers.get(identifier);
            if (itemContainer == null) continue;
            short capacity = itemContainer.getCapacity();
            for (short i = 0; i < capacity; i = (short)(i + 1)) {
                String inventoryDataPrefix = "'" + identifier + "/" + i + "'";
                ItemStack itemStack = itemContainer.getItemStack(i);
                String itemStackJsonData = ItemStackFunctions.getItemStackJsonData(itemStack);
                if (!inventoryData.isEmpty()) {
                    inventoryData.append("\n");
                }
                inventoryData.append(inventoryDataPrefix).append(" : ").append(itemStackJsonData);
            }
        }
        return inventoryData.toString();
    }

    public static void writeInventoryDataToFile(String inventoryData, File outputFile) throws IOException {
        FileFunctions.writeStringToFile(inventoryData, outputFile);
    }

    public static Inventory getInventoryFromDataFile(File dataFile) throws IOException {
        String inventoryData = Files.readString(dataFile.toPath(), StandardCharsets.UTF_8);
        return InventoryFunctions.getInventoryFromData(inventoryData);
    }

    public static Inventory getInventoryFromData(String inventoryData) {
        HashMap<String, EmptyItemContainer> itemContainers = new HashMap<String, EmptyItemContainer>();
        Map<String, Integer> inventoryCapacities = InventoryFunctions.getInventoryCapacities(inventoryData);
        Iterator<String> iterator = InventoryFunctions.getInventoryIdentifiers().iterator();
        while (iterator.hasNext()) {
            String identifier;
            short capacity = inventoryCapacities.get(identifier = iterator.next()).shortValue();
            itemContainers.put(identifier, (EmptyItemContainer)(capacity == 0 ? EmptyItemContainer.INSTANCE : new SimpleItemContainer(capacity)));
        }
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        for (String line : inventoryData.split("\n")) {
            ItemStack itemStack;
            String jsonValue;
            Map map;
            int colonIndex = line.indexOf(" : ");
            if (colonIndex == -1 || (map = (Map)HybridConstants.GSON.fromJson(jsonValue = line.substring(colonIndex + 3), type)).isEmpty()) continue;
            String itemId = (String)map.get("itemId");
            int quantity = 0;
            Object quantityObject = map.get("quantity");
            if (quantityObject instanceof Number) {
                Number number = (Number)quantityObject;
                quantity = number.intValue();
            }
            if (quantity == 0) continue;
            BsonDocument metadata = null;
            String rawMetadata = (String)map.get("metadata");
            if (!rawMetadata.equals("none")) {
                metadata = BsonDocument.parse((String)rawMetadata);
            }
            if ((itemStack = new ItemStack(itemId, quantity, metadata)).isEmpty()) continue;
            String key = line.substring(0, colonIndex).replace("'", "");
            String[] keySpl = key.split("/");
            String lineIdentifier = keySpl[0];
            short slot = Short.parseShort(keySpl[1]);
            ((HytaleLogger.Api)HybridConstants.LOGGER.atInfo()).log("Setting " + lineIdentifier + ", slot: " + slot + ", itemStack: " + String.valueOf(itemStack));
            ((ItemContainer)itemContainers.get(lineIdentifier)).setItemStackForSlot(slot, itemStack);
        }
        return new Inventory((ItemContainer)itemContainers.get("storage"), (ItemContainer)itemContainers.get("armour"), (ItemContainer)itemContainers.get("hotbar"), (ItemContainer)itemContainers.get("utility"), (ItemContainer)itemContainers.get("tools"), (ItemContainer)itemContainers.get("backpack"));
    }

    private static Map<String, Integer> getInventoryCapacities(String inventoryData) {
        HashMap<String, Integer> capacities = new HashMap<String, Integer>();
        for (String identifier : InventoryFunctions.getInventoryIdentifiers()) {
            capacities.put(identifier, 0);
        }
        for (String line : inventoryData.split("\n")) {
            String key;
            String lineIdentifier;
            int colonIndex = line.indexOf(" : ");
            if (colonIndex == -1 || !capacities.containsKey(lineIdentifier = (key = line.substring(0, colonIndex).replace("'", "")).split("/")[0])) continue;
            capacities.merge(lineIdentifier, 1, Integer::sum);
        }
        return capacities;
    }

    public static HashMap<String, ItemContainer> getAllItemContainers(Player player) {
        return InventoryFunctions.getAllItemContainers(player.getInventory());
    }

    public static HashMap<String, ItemContainer> getAllItemContainers(Inventory inventory) {
        HashMap<String, ItemContainer> itemContainers = new HashMap<String, ItemContainer>();
        itemContainers.put("armour", inventory.getArmor());
        itemContainers.put("storage", inventory.getStorage());
        itemContainers.put("hotbar", inventory.getHotbar());
        itemContainers.put("utility", inventory.getUtility());
        itemContainers.put("backpack", inventory.getBackpack());
        return itemContainers;
    }

    public static List<String> getInventoryIdentifiers() {
        return Arrays.asList("armour", "storage", "hotbar", "utility", "tools", "backpack");
    }
}
