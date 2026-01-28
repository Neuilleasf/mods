/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.SystemGroup
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.protocol.AnimationSlot
 *  com.hypixel.hytale.protocol.ChangeVelocityType
 *  com.hypixel.hytale.protocol.Packet
 *  com.hypixel.hytale.protocol.SoundCategory
 *  com.hypixel.hytale.protocol.packets.entities.PlayAnimation
 *  com.hypixel.hytale.server.core.asset.type.item.config.Item
 *  com.hypixel.hytale.server.core.asset.type.item.config.ItemWeapon
 *  com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle
 *  com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent
 *  com.hypixel.hytale.server.core.entity.damage.DamageDataComponent
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.inventory.Inventory
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
 *  com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$EntitySource
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$Source
 *  com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem
 *  com.hypixel.hytale.server.core.modules.entity.damage.DamageModule
 *  com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems
 *  com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId
 *  com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap
 *  com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes
 *  com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier
 *  com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.WieldingInteraction
 *  com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageEffects
 *  com.hypixel.hytale.server.core.modules.physics.component.Velocity
 *  com.hypixel.hytale.server.core.modules.time.TimeResource
 *  com.hypixel.hytale.server.core.universe.world.PlayerUtil
 *  com.hypixel.hytale.server.core.universe.world.SoundUtil
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.entities.PlayAnimation;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemWeapon;
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.WieldingInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageEffects;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.narwhals.plugin.AnimationInjector;
import org.narwhals.plugin.AnimationUtil;
import org.narwhals.plugin.EffectUtil;
import org.narwhals.plugin.EntityStaminaComponent;
import org.narwhals.plugin.EntityStaminaConfig;
import org.narwhals.plugin.ParryComponent;
import org.narwhals.plugin.ParryConfig;
import org.narwhals.plugin.PvPConfig;
import org.narwhals.plugin.PvPParrySystem;
import org.narwhals.plugin.StunComponent;
import org.narwhals.plugin.StunUtil;

public class ParrySystem
extends DamageEventSystem {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final int STAMINA_INDEX = 9;
    private static final WorldParticle PARRY_PARTICLE = new WorldParticle("Perfect_Parry", null, 1.0f, null, null);
    private static final String PARRY_ANIM_ACTION = "PP_Parry";
    private static final long MIN_PARRY_INTERVAL_MS = 50L;
    private final ComponentType<EntityStore, ParryComponent> parryComponentType;
    private final ComponentType<EntityStore, EntityStaminaComponent> staminaComponentType;
    private final ComponentType<EntityStore, StunComponent> stunComponentType;
    private final Vector3d tempDirection = new Vector3d();
    private final Vector3d tempForce = new Vector3d();
    private final Vector3d tempTargetPos = new Vector3d();
    private final Map<Ref<EntityStore>, EntityStaminaComponent> pendingStaminaComponents = new HashMap<Ref<EntityStore>, EntityStaminaComponent>();
    private long lastTickTime = -1L;

    public ParrySystem(ComponentType<EntityStore, ParryComponent> parryComponentType, ComponentType<EntityStore, EntityStaminaComponent> staminaComponentType, ComponentType<EntityStore, StunComponent> stunComponentType) {
        this.parryComponentType = parryComponentType;
        this.staminaComponentType = staminaComponentType;
        this.stunComponentType = stunComponentType;
    }

    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.parryComponentType;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        EntityStaminaConfig.StaminaStats stats;
        int signatureEnergyIndex;
        StaticModifier[] sigEnergyModifiers;
        Int2ObjectMap statModifiers;
        Item item;
        ItemWeapon weapon;
        Inventory inventory;
        ItemStack itemInHand;
        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource)) return;
        Damage.EntitySource entitySource = (Damage.EntitySource)source;
        Ref attackerRef = entitySource.getRef();
        if (!attackerRef.isValid()) {
            return;
        }
        ParryComponent parryComponent = (ParryComponent)chunk.getComponent(index, this.parryComponentType);
        if (parryComponent == null) {
            return;
        }
        DamageDataComponent damageData = (DamageDataComponent)chunk.getComponent(index, DamageDataComponent.getComponentType());
        if (damageData == null) {
            return;
        }
        WieldingInteraction currentWielding = damageData.getCurrentWielding();
        if (currentWielding == null) {
            return;
        }
        ParryConfig config = ParryConfig.get();
        TimeResource timeResource = (TimeResource)store.getResource(TimeResource.getResourceType());
        long nowMs = timeResource.getNow().toEpochMilli();
        if (nowMs != this.lastTickTime) {
            this.pendingStaminaComponents.clear();
            this.lastTickTime = nowMs;
        }
        if (nowMs - parryComponent.getLastSuccessfulParryTimeMs() < 50L) {
            damage.putMetaObject(Damage.STAMINA_DRAIN_MULTIPLIER, (Object)Float.valueOf(0.0f));
            damage.setAmount(0.0f);
            return;
        }
        long timeSinceBlockStart = nowMs - parryComponent.getBlockStartTimeMs();
        if (timeSinceBlockStart > config.parryWindowMs) {
            return;
        }
        parryComponent.setLastSuccessfulParryTimeMs(nowMs);
        damage.putMetaObject(Damage.STAMINA_DRAIN_MULTIPLIER, (Object)Float.valueOf(config.parryStaminaDrainMultiplier));
        float rawReflect = damage.getInitialAmount() * config.reflectDamagePercent;
        float finalReflect = Math.max(1.0f, rawReflect);
        Ref defenderRef = chunk.getReferenceTo(index);
        Damage reflectedDamage = new Damage((Damage.Source)new Damage.EntitySource(defenderRef), damage.getDamageCauseIndex(), finalReflect);
        TransformComponent defenderTransform = (TransformComponent)chunk.getComponent(index, TransformComponent.getComponentType());
        EffectUtil.spawnCombatParticle(PARRY_PARTICLE, damage, this.tempTargetPos, defenderTransform, store, commandBuffer);
        DamageEffects effects = currentWielding.getBlockedEffects();
        if (effects != null && effects.toPacket().soundEventIndex != 0) {
            String itemAnimationId = AnimationUtil.getItemAnimationId((Ref<EntityStore>)defenderRef, store);
            int soundIndex = itemAnimationId != null && itemAnimationId.equals("Shield") ? SoundEvent.getAssetMap().getIndex((Object)"SFX_Shield_T2_Impact") : SoundEvent.getAssetMap().getIndex((Object)"SFX_PP_Perfect_Parry");
            SoundUtil.playSoundEvent3d((int)soundIndex, (SoundCategory)SoundCategory.SFX, (double)this.tempTargetPos.x, (double)this.tempTargetPos.y, (double)this.tempTargetPos.z, (float)2.5f, (float)1.0f, p -> true, commandBuffer);
        }
        DamageSystems.executeDamage((Ref)attackerRef, commandBuffer, (Damage)reflectedDamage);
        this.playParryAnimation((Ref<EntityStore>)defenderRef, store, commandBuffer);
        this.playHurtAnimation((Ref<EntityStore>)attackerRef, store, commandBuffer);
        PvPParrySystem.applyPvPParryEffects((Ref<EntityStore>)attackerRef, store, commandBuffer);
        AnimationInjector.injectIfNeeded((Ref<EntityStore>)attackerRef, commandBuffer);
        this.applyKnockback((Ref<EntityStore>)attackerRef, defenderTransform, commandBuffer, config);
        Player defenderPlayer = (Player)chunk.getComponent(index, Player.getComponentType());
        if (defenderPlayer != null && config.parriesToFullSignatureEnergy > 0 && (itemInHand = (inventory = defenderPlayer.getInventory()).getItemInHand()) != null && (weapon = (item = itemInHand.getItem()).getWeapon()) != null && (statModifiers = weapon.getStatModifiers()) != null && (sigEnergyModifiers = (StaticModifier[])statModifiers.get(signatureEnergyIndex = DefaultEntityStatTypes.getSignatureEnergy())) != null && sigEnergyModifiers.length > 0) {
            float itemSignatureEnergy = sigEnergyModifiers[0].getAmount();
            float signatureEnergyGain = itemSignatureEnergy / (float)config.parriesToFullSignatureEnergy;
            EntityStatMap defenderStats = (EntityStatMap)chunk.getComponent(index, EntityStatMap.getComponentType());
            if (defenderStats != null) {
                float previousValue = defenderStats.addStatValue(signatureEnergyIndex, signatureEnergyGain);
                ((HytaleLogger.Api)LOGGER.atInfo()).log("ParrySystem: Granted " + signatureEnergyGain + " signature energy (item max: " + itemSignatureEnergy + ", parries needed: " + config.parriesToFullSignatureEnergy + "). Previous: " + previousValue);
            }
        }
        parryComponent.setPerfectParryTimeMs(nowMs);
        parryComponent.setCounterattackReady(true);
        parryComponent.setParriedEntityRef((Ref<EntityStore>)attackerRef);
        EntityStaminaComponent staminaComp = (EntityStaminaComponent)commandBuffer.getComponent(attackerRef, this.staminaComponentType);
        if (staminaComp == null) {
            staminaComp = this.pendingStaminaComponents.get(attackerRef);
        }
        if (staminaComp == null) {
            ModelComponent attackerModelComp = (ModelComponent)commandBuffer.getComponent(attackerRef, ModelComponent.getComponentType());
            if (attackerModelComp == null) return;
            if (attackerModelComp.getModel() == null) return;
            String modelAssetId = attackerModelComp.getModel().getModelAssetId();
            stats = EntityStaminaConfig.getStats(modelAssetId);
            if (stats == null) return;
            staminaComp = new EntityStaminaComponent(modelAssetId, stats.max_stamina);
            commandBuffer.addComponent(attackerRef, this.staminaComponentType, (Component)staminaComp);
            this.pendingStaminaComponents.put((Ref<EntityStore>)attackerRef, staminaComp);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("ParrySystem: Attached NEW StaminaComponent.");
        } else {
            stats = EntityStaminaConfig.getStats(staminaComp.getModelAssetId());
        }
        if (stats == null) {
            return;
        }
        if (config.enableEntityStamina) {
            float current = staminaComp.getCurrentStamina();
            float loss = stats.parried_stamina_change;
            float newStamina = current - loss;
            staminaComp.setCurrentStamina(newStamina);
            staminaComp.setLastActionTimeMs(nowMs);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("ParrySystem: Parried! Stamina: " + current + " - " + loss + " = " + newStamina);
        }
        boolean appliedFullStun = false;
        if (config.enableEntityStamina && staminaComp.isStaminaDepleted() && !StunUtil.isStunned((Ref<EntityStore>)attackerRef, store)) {
            ((HytaleLogger.Api)LOGGER.atInfo()).log("ParrySystem: Stamina Depleted! Triggering Stun: " + config.stunDurationSeconds + "s");
            staminaComp.setCurrentStamina(0.0f);
            staminaComp.setLastAttacker((Ref<EntityStore>)defenderRef);
            TransformComponent transform = (TransformComponent)chunk.getComponent(index, TransformComponent.getComponentType());
            if (transform != null) {
                Vector3d pos = transform.getPosition();
                String modelType = AnimationUtil.getMobAnimId((Ref<EntityStore>)attackerRef, commandBuffer);
                String SFXString = "SFX_" + modelType + "_Death";
                int soundIndex = SoundEvent.getAssetMap().getIndex((Object)SFXString);
                SoundUtil.playSoundEvent3d((int)soundIndex, (SoundCategory)SoundCategory.SFX, (double)pos.x, (double)pos.y, (double)pos.z, (float)50.0f, (float)1.0f, store);
                SFXString = SFXString + "_1";
                soundIndex = SoundEvent.getAssetMap().getIndex((Object)SFXString);
                SoundUtil.playSoundEvent3d((int)soundIndex, (SoundCategory)SoundCategory.SFX, (double)pos.x, (double)pos.y, (double)pos.z, (float)50.0f, (float)1.0f, store);
                soundIndex = SoundEvent.getAssetMap().getIndex((Object)"SFX_PP_Guard_Broken");
                SoundUtil.playSoundEvent3d((int)soundIndex, (SoundCategory)SoundCategory.SFX, (double)pos.x, (double)pos.y, (double)pos.z, (float)0.25f, (float)1.0f, store);
            }
            StunUtil.applyStun((Ref<EntityStore>)attackerRef, store, commandBuffer, config.stunDurationSeconds, (Ref<EntityStore>)defenderRef);
            return;
        }
        if (appliedFullStun) return;
        double roll = ThreadLocalRandom.current().nextDouble();
        if (!(roll < (double)stats.stagger_chance)) return;
        ((HytaleLogger.Api)LOGGER.atInfo()).log("ParrySystem: Stagger triggered! Duration: " + config.staggerDurationSeconds + "s");
        TransformComponent transform = (TransformComponent)chunk.getComponent(index, TransformComponent.getComponentType());
        if (transform != null) {
            Vector3d pos = transform.getPosition();
            int soundIndex = SoundEvent.getAssetMap().getIndex((Object)"SFX_Guard_Broken");
            SoundUtil.playSoundEvent3d((int)soundIndex, (SoundCategory)SoundCategory.SFX, (double)pos.x, (double)pos.y, (double)pos.z, (float)0.75f, (float)1.0f, store);
        }
        StunUtil.applyStagger((Ref<EntityStore>)attackerRef, store, commandBuffer, config.staggerDurationSeconds);
    }

    private void applyKnockback(Ref<EntityStore> attackerRef, TransformComponent defenderTransform, CommandBuffer<EntityStore> commandBuffer, ParryConfig config) {
        Velocity attackerVelocity = (Velocity)commandBuffer.getComponent(attackerRef, Velocity.getComponentType());
        TransformComponent attackerTransform = (TransformComponent)commandBuffer.getComponent(attackerRef, TransformComponent.getComponentType());
        if (attackerVelocity == null || attackerTransform == null || defenderTransform == null) {
            return;
        }
        Vector3d attackerPos = attackerTransform.getPosition();
        Vector3d defenderPos = defenderTransform.getPosition();
        double dx = attackerPos.x - defenderPos.x;
        double dy = attackerPos.y - defenderPos.y;
        double dz = attackerPos.z - defenderPos.z;
        double lengthSquared = dx * dx + dy * dy + dz * dz;
        if (lengthSquared > 1.0E-8) {
            double invLength = 1.0 / Math.sqrt(lengthSquared);
            this.tempDirection.x = dx * invLength;
            this.tempDirection.y = dy * invLength;
            this.tempDirection.z = dz * invLength;
        } else {
            this.tempDirection.x = 0.0;
            this.tempDirection.y = 0.0;
            this.tempDirection.z = 1.0;
        }
        float knockbackX = config.knockbackX;
        PvPConfig pvpConfig = PvPConfig.get();
        Player playerComponent = (Player)commandBuffer.getComponent(attackerRef, Player.getComponentType());
        if (!pvpConfig.enableSuperKnockback && playerComponent != null) {
            ((HytaleLogger.Api)LOGGER.atInfo()).log("ParrySystem: Entity is a player, applying less knockback.");
            attackerVelocity.addInstruction(this.tempForce, null, ChangeVelocityType.Set);
            this.tempForce.x = this.tempDirection.x * (double)knockbackX * 0.025;
            this.tempForce.z = this.tempDirection.z * (double)knockbackX * 0.025;
        } else {
            this.tempForce.x = this.tempDirection.x * (double)knockbackX;
            this.tempForce.z = this.tempDirection.z * (double)knockbackX;
        }
        this.tempForce.y = config.knockbackY;
        attackerVelocity.addInstruction(this.tempForce, null, ChangeVelocityType.Set);
    }

    private void playParryAnimation(Ref<EntityStore> defenderRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        NetworkId networkIdComponent = (NetworkId)commandBuffer.getComponent(defenderRef, NetworkId.getComponentType());
        if (networkIdComponent == null) {
            return;
        }
        String itemAnimationId = AnimationUtil.getItemAnimationId(defenderRef, store);
        if (itemAnimationId == null) {
            return;
        }
        PlayAnimation packet = new PlayAnimation(networkIdComponent.getId(), itemAnimationId, PARRY_ANIM_ACTION, AnimationSlot.Action);
        PlayerUtil.forEachPlayerThatCanSeeEntity(defenderRef, (playerRef, playerRefComponent, ca) -> playerRefComponent.getPacketHandler().write((Packet)packet), store);
    }

    private void playHurtAnimation(Ref<EntityStore> attackerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        NetworkId networkIdComponent = (NetworkId)commandBuffer.getComponent(attackerRef, NetworkId.getComponentType());
        if (networkIdComponent == null) {
            return;
        }
        String itemAnimationId = AnimationUtil.getMobAnimId(attackerRef, commandBuffer);
        if (itemAnimationId == null || itemAnimationId.isEmpty()) {
            return;
        }
        PlayAnimation hurtPacket = new PlayAnimation(networkIdComponent.getId(), itemAnimationId, "Hurt", AnimationSlot.Action);
        PlayerUtil.forEachPlayerThatCanSeeEntity(attackerRef, (playerRef, playerRefComponent, ca) -> playerRefComponent.getPacketHandler().write((Packet)hurtPacket), store);
    }
}
