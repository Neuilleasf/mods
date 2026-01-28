/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.utils;

import com.hypixel.hytale.logger.HytaleLogger;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.config.LevelingConfig;

public class DebugLogger {
    private final HytaleLogger logger;
    @Nullable
    private static final Method FOR_CLASS_METHOD = DebugLogger.resolveForClassMethod();

    public DebugLogger(@Nonnull HytaleLogger logger) {
        this.logger = logger;
    }

    @Nonnull
    public static DebugLogger forEnclosingClass() {
        Class caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(frames -> frames.map(StackWalker.StackFrame::getDeclaringClass).filter(c -> c != DebugLogger.class).findFirst().orElse(DebugLogger.class));
        HytaleLogger logger = DebugLogger.resolveLoggerForCaller(caller);
        return new DebugLogger(logger);
    }

    @Nonnull
    public static DebugLogger forClass(@Nonnull Class<?> clazz) {
        return new DebugLogger(DebugLogger.resolveLoggerForCaller(clazz));
    }

    @Nonnull
    private static HytaleLogger resolveLoggerForCaller(@Nonnull Class<?> clazz) {
        if (FOR_CLASS_METHOD != null) {
            try {
                Object o = FOR_CLASS_METHOD.invoke(null, clazz);
                if (o instanceof HytaleLogger) {
                    return (HytaleLogger)o;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return HytaleLogger.forEnclosingClass();
    }

    @Nullable
    private static Method resolveForClassMethod() {
        try {
            return HytaleLogger.class.getMethod("forClass", Class.class);
        }
        catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    public void info(@Nullable LevelingConfig config, @Nonnull String message) {
        if (config != null && config.isDebug()) {
            ((HytaleLogger.Api)this.logger.atInfo()).log(message);
        }
    }

    public void info(@Nullable LevelingConfig config, @Nonnull Supplier<String> messageSupplier) {
        if (config != null && config.isDebug()) {
            ((HytaleLogger.Api)this.logger.atInfo()).log(messageSupplier.get());
        }
    }

    public void warning(@Nullable LevelingConfig config, @Nonnull String message) {
        if (config != null && config.isDebug()) {
            ((HytaleLogger.Api)this.logger.atWarning()).log(message);
        }
    }

    public void warning(@Nullable LevelingConfig config, @Nonnull Supplier<String> messageSupplier) {
        if (config != null && config.isDebug()) {
            ((HytaleLogger.Api)this.logger.atWarning()).log(messageSupplier.get());
        }
    }

    public void severe(@Nullable LevelingConfig config, @Nonnull String message) {
        if (config != null && config.isDebug()) {
            ((HytaleLogger.Api)this.logger.atSevere()).log(message);
        }
    }

    public void severe(@Nullable LevelingConfig config, @Nonnull Supplier<String> messageSupplier) {
        if (config != null && config.isDebug()) {
            ((HytaleLogger.Api)this.logger.atSevere()).log(messageSupplier.get());
        }
    }
}
