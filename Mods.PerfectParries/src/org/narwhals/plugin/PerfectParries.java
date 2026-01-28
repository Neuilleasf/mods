/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.hypixel.hytale.component.ComponentRegistryProxy
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.system.ISystem
 *  com.hypixel.hytale.logger.HytaleLogger
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 *  com.hypixel.hytale.server.core.command.system.AbstractCommand
 *  com.hypixel.hytale.server.core.plugin.JavaPlugin
 *  com.hypixel.hytale.server.core.plugin.JavaPluginInit
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.narwhals.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.io.File;
import javax.annotation.Nonnull;
import org.narwhals.plugin.BlockTrackingSystem;
import org.narwhals.plugin.CounterattackSystem;
import org.narwhals.plugin.EntityStaminaComponent;
import org.narwhals.plugin.EntityStaminaConfig;
import org.narwhals.plugin.EntityStaminaDamageSystem;
import org.narwhals.plugin.EntityStaminaRegenSystem;
import org.narwhals.plugin.ParryComponent;
import org.narwhals.plugin.ParryConfig;
import org.narwhals.plugin.ParryModCommand;
import org.narwhals.plugin.ParrySystem;
import org.narwhals.plugin.PlayerJoinParryAdder;
import org.narwhals.plugin.PvPConfig;
import org.narwhals.plugin.StunComponent;
import org.narwhals.plugin.StunSystem;
import org.narwhals.plugin.StunUtil;

public class PerfectParries
extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final File CONFIG_DIR = new File("mods/Perfect_Parries");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ComponentType<EntityStore, ParryComponent> parryComponentType;
    private static ComponentType<EntityStore, EntityStaminaComponent> staminaComponentType;
    private static ComponentType<EntityStore, StunComponent> stunComponentType;

    public PerfectParries(@Nonnull JavaPluginInit init) {
        super(init);
    }

    public static ComponentType<EntityStore, ParryComponent> getParryComponentType() {
        return parryComponentType;
    }

    public static ComponentType<EntityStore, EntityStaminaComponent> getStaminaComponentType() {
        return staminaComponentType;
    }

    public static ComponentType<EntityStore, StunComponent> getStunComponentType() {
        return stunComponentType;
    }

    protected void setup() {
        ((HytaleLogger.Api)LOGGER.atInfo()).log("ParryMod: Setting Up");
        this.initConfigs();
        this.initComponents();
        this.initSystems();
        this.getCommandRegistry().registerCommand((AbstractCommand)new ParryModCommand());
        ((HytaleLogger.Api)LOGGER.atInfo()).log("ParryMod: Setup Successful");
    }

    private void initConfigs() {
        if (!CONFIG_DIR.exists() && !CONFIG_DIR.mkdirs()) {
            ((HytaleLogger.Api)LOGGER.atWarning()).log("ParryMod: Failed to create config directory");
        }
        ParryConfig.load(CONFIG_DIR, GSON);
        EntityStaminaConfig.load(CONFIG_DIR, GSON);
        PvPConfig.load(CONFIG_DIR, GSON);
    }

    private void initComponents() {
        ComponentRegistryProxy registry = this.getEntityStoreRegistry();
        parryComponentType = registry.registerComponent(ParryComponent.class, ParryComponent::new);
        staminaComponentType = registry.registerComponent(EntityStaminaComponent.class, EntityStaminaComponent::new);
        stunComponentType = registry.registerComponent(StunComponent.class, StunComponent::new);
        StunUtil.init(stunComponentType);
    }

    private void initSystems() {
        ComponentRegistryProxy registry = this.getEntityStoreRegistry();
        registry.registerSystem((ISystem)new PlayerJoinParryAdder(parryComponentType));
        registry.registerSystem((ISystem)new BlockTrackingSystem(parryComponentType));
        registry.registerSystem((ISystem)new ParrySystem(parryComponentType, staminaComponentType, stunComponentType));
        registry.registerSystem((ISystem)new CounterattackSystem(parryComponentType));
        registry.registerSystem((ISystem)new EntityStaminaDamageSystem(staminaComponentType, stunComponentType));
        registry.registerSystem((ISystem)new EntityStaminaRegenSystem(staminaComponentType, stunComponentType));
        registry.registerSystem((ISystem)new StunSystem(stunComponentType, staminaComponentType));
    }
}
