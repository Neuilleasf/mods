/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.data;

import java.util.HashMap;
import java.util.Map;

public enum HybridEntityStatType {
    AMMO,
    DEPLOYABLE_PREVIEW,
    GLIDING_ACTIVE,
    HEALTH,
    IMMUNITY,
    MAGIC_CHARGES,
    MANA,
    OXYGEN,
    SIGNATURE_CHARGES,
    SIGNATURE_ENERGY,
    STAMINA,
    STAMINA_REGEN_DELAY;

    private static final Map<String, HybridEntityStatType> BY_ID;

    public static HybridEntityStatType fromId(String id) {
        return BY_ID.get(id);
    }

    public String getId() {
        String[] parts = this.name().toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return result.toString();
    }

    static {
        BY_ID = new HashMap<String, HybridEntityStatType>();
        for (HybridEntityStatType type : HybridEntityStatType.values()) {
            BY_ID.put(type.getId(), type);
        }
    }
}
