/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityStaminaComponent
implements Component<EntityStore> {
    private final String modelAssetId;
    private float currentStamina;
    private float maxStamina;
    private long lastActionTimeMs = Long.MIN_VALUE;
    @Nullable
    private Ref<EntityStore> lastAttacker = null;

    public EntityStaminaComponent() {
        this("default", 100.0f);
    }

    public EntityStaminaComponent(String modelAssetId, float maxStamina) {
        this.modelAssetId = modelAssetId;
        this.maxStamina = maxStamina;
        this.currentStamina = maxStamina;
    }

    public String getModelAssetId() {
        return this.modelAssetId;
    }

    public float getCurrentStamina() {
        return this.currentStamina;
    }

    public void setCurrentStamina(float stamina) {
        this.currentStamina = stamina;
    }

    public float getMaxStamina() {
        return this.maxStamina;
    }

    public void setMaxStamina(float maxStamina) {
        this.maxStamina = maxStamina;
    }

    public long getLastActionTimeMs() {
        return this.lastActionTimeMs;
    }

    public void setLastActionTimeMs(long timeMs) {
        this.lastActionTimeMs = timeMs;
    }

    @Nullable
    public Ref<EntityStore> getLastAttacker() {
        return this.lastAttacker;
    }

    public void setLastAttacker(@Nullable Ref<EntityStore> lastAttacker) {
        this.lastAttacker = lastAttacker;
    }

    public boolean isStaminaDepleted() {
        return this.currentStamina <= 0.0f;
    }

    public float getStaminaPercent() {
        return this.maxStamina > 0.0f ? this.currentStamina / this.maxStamina : 0.0f;
    }

    public void restoreStamina() {
        this.currentStamina = this.maxStamina;
    }

    @Nonnull
    public EntityStaminaComponent clone() {
        EntityStaminaComponent c = new EntityStaminaComponent(this.modelAssetId, this.maxStamina);
        c.currentStamina = this.currentStamina;
        c.lastActionTimeMs = this.lastActionTimeMs;
        c.lastAttacker = this.lastAttacker;
        return c;
    }
}
