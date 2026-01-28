/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.zuxaw.plugin.utils;

import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.zuxaw.plugin.config.LevelingConfig;
import org.zuxaw.plugin.services.MessageService;
import org.zuxaw.plugin.utils.DebugLogger;

public class LocalizationValidator {
    private static final DebugLogger DEBUG = DebugLogger.forEnclosingClass();
    private static boolean enableValidation = false;
    @Nullable
    private static LevelingConfig config = null;
    private static long lastNotificationWarningMs = 0L;
    private static final long NOTIFICATION_WARNING_COOLDOWN_MS = 30000L;
    private static final Pattern USER_TEXT_PATTERN = Pattern.compile("(?i)(level|points?|stat|error|success|failed?|invalid|earned?|congratulations|you|your|player|available|maximum|health|stamina|damage|defense|mining)");

    public static void setEnabled(@Nullable LevelingConfig cfg, boolean enabled) {
        enableValidation = enabled;
        config = cfg;
    }

    public static void validateNotification(@Nonnull String message, @Nonnull Class<?> callerClass) {
        if (!enableValidation) {
            return;
        }
        String cleanMessage = message.replaceAll("</?color:[^>]+>", "");
        String cleanMessageFinal = cleanMessage = cleanMessage.replaceAll("</?[biu]>", "");
        if (USER_TEXT_PATTERN.matcher(cleanMessage).find()) {
            long now = System.currentTimeMillis();
            if (now - lastNotificationWarningMs < 30000L) {
                return;
            }
            lastNotificationWarningMs = now;
            DEBUG.warning(config, () -> "[LOCALIZATION WARNING] Possible hardcoded notification in " + callerClass.getSimpleName() + " | Message=\"" + cleanMessageFinal + "\" | Use messageService.getNotification()");
        }
    }

    public static void validateGuiText(@Nonnull String text, @Nonnull String elementId, @Nonnull Class<?> callerClass) {
        if (!enableValidation) {
            return;
        }
        if (text.trim().isEmpty() || text.matches("^[\\d\\s\\.\\-\\+%/]+$")) {
            return;
        }
        if (USER_TEXT_PATTERN.matcher(text).find()) {
            DEBUG.warning(config, () -> "[LOCALIZATION WARNING] Possible hardcoded GUI text in " + callerClass.getSimpleName() + " | element=" + elementId + " | text=\"" + text + "\" | Use messageService.getGuiLabel()/getStatName()");
        }
    }

    public static void validateErrorMessage(@Nonnull String errorMessage, @Nonnull Class<?> callerClass) {
        if (!enableValidation) {
            return;
        }
        if (USER_TEXT_PATTERN.matcher(errorMessage).find()) {
            DEBUG.warning(config, () -> "[LOCALIZATION WARNING] Possible hardcoded error message in " + callerClass.getSimpleName() + " | error=\"" + errorMessage + "\" | Use messageService.getError()");
        }
    }

    public static void validateCommandMessage(@Nonnull String message, @Nonnull Class<?> callerClass) {
        if (!enableValidation) {
            return;
        }
        String cleanMessage = message.replaceAll("</?color:[^>]+>", "");
        String cleanMessageFinal = cleanMessage = cleanMessage.replaceAll("</?[biu]>", "");
        if (USER_TEXT_PATTERN.matcher(cleanMessage).find()) {
            DEBUG.warning(config, () -> "[LOCALIZATION WARNING] Possible hardcoded command message in " + callerClass.getSimpleName() + " | message=\"" + cleanMessageFinal + "\" | Use messageService.getCommand()");
        }
    }

    public static void logCoverageSummary(@Nonnull MessageService messageService) {
        if (!enableValidation) {
            return;
        }
        DEBUG.info(config, () -> "[LOCALIZATION] Validation enabled (messages.json present).");
    }
}
