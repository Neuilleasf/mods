/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.server.core.inventory.ItemStack;

public class ToolFunctions {
    public static boolean isTool(ItemStack itemStack) {
        return itemStack != null && ToolFunctions.isTool(itemStack.getItemId());
    }

    public static boolean isTool(String itemId) {
        return itemId.startsWith("Tool_");
    }

    public static boolean isPickaxe(ItemStack itemStack) {
        return itemStack != null && ToolFunctions.isPickaxe(itemStack.getItemId());
    }

    public static boolean isPickaxe(String itemId) {
        return ToolFunctions.isTool(itemId) && itemId.contains("Pickaxe_");
    }

    public static boolean isHatchet(ItemStack itemStack) {
        return itemStack != null && ToolFunctions.isHatchet(itemStack.getItemId());
    }

    public static boolean isHatchet(String itemId) {
        return ToolFunctions.isTool(itemId) && itemId.contains("Hatchet_");
    }

    public static boolean isShovel(ItemStack itemStack) {
        return itemStack != null && ToolFunctions.isShovel(itemStack.getItemId());
    }

    public static boolean isShovel(String itemId) {
        return ToolFunctions.isTool(itemId) && itemId.contains("Shovel_");
    }

    public static boolean isHoe(ItemStack itemStack) {
        return itemStack != null && ToolFunctions.isHoe(itemStack.getItemId());
    }

    public static boolean isHoe(String itemId) {
        return ToolFunctions.isTool(itemId) && itemId.contains("Hoe_");
    }

    public static boolean isShears(ItemStack itemStack) {
        return itemStack != null && ToolFunctions.isShears(itemStack.getItemId());
    }

    public static boolean isShears(String itemId) {
        return ToolFunctions.isTool(itemId) && itemId.contains("Shears_");
    }
}
