/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
 *  com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage
 *  com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
 *  com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.narwhals.plugin.ui;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Locale;
import javax.annotation.Nonnull;
import org.narwhals.plugin.ParryConfig;
import org.narwhals.plugin.PvPConfig;

public class ConfigUIPage
extends CustomUIPage {
    public ConfigUIPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss);
    }

    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commands, @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        commands.append("Pages/ConfigUIPage.ui");
        ParryConfig parryConfig = ParryConfig.get();
        PvPConfig pvpConfig = PvPConfig.get();
        commands.set("#TitleLabel.Text", "Perfect Parries");
        commands.set("#InfoLabel.Text", "v0.5.1");
        commands.set("#ParryWindowValue.Text", parryConfig.parryWindowMs + "ms");
        commands.set("#StaminaDrainValue.Text", String.format(Locale.US, "%.0f%%", Float.valueOf(parryConfig.parryStaminaDrainMultiplier * 100.0f)));
        commands.set("#ReflectDamageValue.Text", String.format(Locale.US, "%.0f%%", Float.valueOf(parryConfig.reflectDamagePercent * 100.0f)));
        commands.set("#KnockbackValue.Text", String.format(Locale.US, "%.0f / %.0f", Float.valueOf(parryConfig.knockbackX), Float.valueOf(parryConfig.knockbackY)));
        commands.set("#SignatureEnergyValue.Text", String.valueOf(parryConfig.parriesToFullSignatureEnergy));
        commands.set("#CounterattackWindowValue.Text", parryConfig.counterattackWindowMs + "ms");
        commands.set("#CounterattackDamageValue.Text", String.format(Locale.US, "%.0f%%", Float.valueOf(parryConfig.counterattackDamageMultiplier * 100.0f)));
        commands.set("#EnableEntityStaminaValue.Text", parryConfig.enableEntityStamina ? "Enabled" : "Disabled");
        commands.set("#StaggerDurationValue.Text", String.format(Locale.US, "%.2fs", Float.valueOf(parryConfig.staggerDurationSeconds)));
        commands.set("#StunDurationValue.Text", String.format(Locale.US, "%.2fs", Float.valueOf(parryConfig.stunDurationSeconds)));
        commands.set("#StunnedDamageMultiplierValue.Text", String.format(Locale.US, "%.0f%%", Float.valueOf(parryConfig.stunnedDamageMultiplier * 100.0f)));
        commands.set("#PvPEnabledValue.Text", pvpConfig.enablePvPParryEffects ? "Enabled" : "Disabled");
        commands.set("#PvPStaminaDamageValue.Text", String.valueOf(pvpConfig.parriedStaminaDamage));
        commands.set("#PvPRegenCooldownValue.Text", String.format(Locale.US, "%.2fs", Float.valueOf(pvpConfig.parriedStaminaRegenCooldownSeconds)));
    }
}
