/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.systems;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

private static final class MiningDamageBlockSystem.DamageAccessors {
    @Nullable
    final Method getAmount;
    @Nullable
    final Method setAmount;
    @Nonnull
    final String via;

    MiningDamageBlockSystem.DamageAccessors(@Nullable Method getAmount, @Nullable Method setAmount, @Nonnull String via) {
        this.getAmount = getAmount;
        this.setAmount = setAmount;
        this.via = via;
    }
}
