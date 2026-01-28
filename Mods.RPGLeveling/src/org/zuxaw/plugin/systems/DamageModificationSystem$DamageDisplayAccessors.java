/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.systems;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

private static final class DamageModificationSystem.DamageDisplayAccessors {
    @Nullable
    final Method putMeta;
    @Nullable
    final Method setInitialAmount;
    @Nullable
    final Field initialAmountField;
    final AtomicReference<Field> matchedAmountField = new AtomicReference<Object>(null);

    DamageModificationSystem.DamageDisplayAccessors(@Nullable Method putMeta, @Nullable Method setInitialAmount, @Nullable Field initialAmountField) {
        this.putMeta = putMeta;
        this.setInitialAmount = setInitialAmount;
        this.initialAmountField = initialAmountField;
    }
}
