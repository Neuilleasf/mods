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

public class StunComponent
implements Component<EntityStore> {
    private float timeRemaining;
    private final StunType stunType;
    private float timeSinceStunStart;
    private static final float BONUS_DAMAGE_DELAY_SECONDS = 2.0f;
    @Nullable
    private Ref<EntityStore> stunCauser;
    private boolean bonusMessageSent;
    private boolean wakingUp;

    public StunComponent() {
        this(0.0f, StunType.STAGGER);
    }

    public StunComponent(float duration, StunType type) {
        this.timeRemaining = duration;
        this.stunType = type;
        this.timeSinceStunStart = 0.0f;
        this.bonusMessageSent = false;
        this.wakingUp = false;
    }

    public StunComponent(StunComponent other) {
        this.timeRemaining = other.timeRemaining;
        this.stunType = other.stunType;
        this.timeSinceStunStart = other.timeSinceStunStart;
        this.stunCauser = other.stunCauser;
        this.bonusMessageSent = other.bonusMessageSent;
        this.wakingUp = other.wakingUp;
    }

    public float getTimeRemaining() {
        return this.timeRemaining;
    }

    public void setTimeRemaining(float time) {
        this.timeRemaining = time;
    }

    public float getTimeSinceStunStart() {
        return this.timeSinceStunStart;
    }

    public void addTimeSinceStunStart(float delta) {
        this.timeSinceStunStart += delta;
    }

    public boolean isBonusDamageWindowActive() {
        return this.stunType == StunType.STUN && this.timeSinceStunStart >= 2.0f;
    }

    public StunType getStunType() {
        return this.stunType;
    }

    public boolean isFullStun() {
        return this.stunType == StunType.STUN;
    }

    public boolean isStagger() {
        return this.stunType == StunType.STAGGER;
    }

    public void setStunCauser(@Nullable Ref<EntityStore> causer) {
        this.stunCauser = causer;
    }

    public boolean isWakingUp() {
        return this.wakingUp;
    }

    public void setWakingUp(boolean wakingUp) {
        this.wakingUp = wakingUp;
    }

    @Nonnull
    public StunComponent clone() {
        return new StunComponent(this);
    }

    public static enum StunType {
        STUN,
        STAGGER;

    }
}
