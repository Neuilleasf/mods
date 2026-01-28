package com.nakai.animesword;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.logger.HytaleLogger;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * AnimeSwordCombat Plugin
 * 
 * Provides anime-style sword combat with:
 * - Lock-on target system for Omnislash signature ability
 * - Enhanced visual feedback
 * 
 * @author nakai
 */
public class AnimeSwordPlugin extends JavaPlugin {
    
    private static AnimeSwordPlugin instance;
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    
    // Lock-on targets per player (player UUID -> target entity UUID)
    private final Map<UUID, UUID> lockOnTargets = new ConcurrentHashMap<>();
    
    // Active players
    private final Map<UUID, PlayerRef> connectedPlayers = new ConcurrentHashMap<>();
    
    private ScheduledFuture<?> updateTask;
    
    public AnimeSwordPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }
    
    public static AnimeSwordPlugin getInstance() {
        return instance;
    }
    
    @Override
    public void setup() {
        LOGGER.at(Level.INFO).log("[AnimeSword] Setup...");
    }
    
    @Override
    public void start() {
        LOGGER.at(Level.INFO).log("=================================");
        LOGGER.at(Level.INFO).log("  AnimeSwordCombat v1.0.0");
        LOGGER.at(Level.INFO).log("  Anime-style sword combat!");
        LOGGER.at(Level.INFO).log("=================================");
        
        // Register events
        getEventRegistry().register(PlayerConnectEvent.class, this::onPlayerConnect);
        getEventRegistry().register(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        
        // Start update loop for lock-on tracking
        startUpdateLoop();
        
        LOGGER.at(Level.INFO).log("[AnimeSword] Plugin started!");
    }
    
    @Override
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel(false);
        }
        lockOnTargets.clear();
        connectedPlayers.clear();
        LOGGER.at(Level.INFO).log("[AnimeSword] Plugin disabled!");
    }
    
    private void startUpdateLoop() {
        updateTask = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                World world = Universe.get().getDefaultWorld();
                if (world == null) return;
                
                world.execute(() -> {
                    updateLockOns(world);
                });
            } catch (Exception e) {
                LOGGER.at(Level.WARNING).log("[AnimeSword] Error in update loop: %s", e.getMessage());
            }
        }, 100, 50, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Update lock-on targets - make players face their locked targets
     */
    private void updateLockOns(World world) {
        List<Player> players = world.getPlayers();
        
        for (Player player : players) {
            UUID playerUuid = player.getUuid();
            UUID targetUuid = lockOnTargets.get(playerUuid);
            
            if (targetUuid != null) {
                Entity target = world.getEntity(targetUuid);
                if (target != null && !target.wasRemoved()) {
                    // Make player face the target
                    faceEntity(player, target);
                } else {
                    // Target died or removed, clear lock
                    lockOnTargets.remove(playerUuid);
                }
            }
        }
    }
    
    /**
     * Make a player face an entity
     */
    private void faceEntity(Player player, Entity target) {
        TransformComponent playerTransform = player.getTransformComponent();
        TransformComponent targetTransform = target.getTransformComponent();
        
        if (playerTransform == null || targetTransform == null) return;
        
        Vector3d playerPos = playerTransform.getPosition();
        Vector3d targetPos = targetTransform.getPosition();
        
        double dx = targetPos.getX() - playerPos.getX();
        double dy = targetPos.getY() - playerPos.getY();
        double dz = targetPos.getZ() - playerPos.getZ();
        
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(Math.atan2(-dy, horizontalDist));
        
        // Set player rotation to face target
        playerTransform.setRotation(new Vector3f(pitch, yaw, 0));
    }
    
    /**
     * Find nearest player to lock onto (for PvP)
     */
    public Player findLockOnTarget(Player searcher, double maxDistance) {
        World world = searcher.getWorld();
        if (world == null) return null;
        
        TransformComponent searcherTransform = searcher.getTransformComponent();
        if (searcherTransform == null) return null;
        
        Vector3d searcherPos = searcherTransform.getPosition();
        
        Player nearest = null;
        double nearestDist = maxDistance;
        
        for (Player player : world.getPlayers()) {
            if (player.getUuid().equals(searcher.getUuid())) continue;
            
            TransformComponent targetTransform = player.getTransformComponent();
            if (targetTransform == null) continue;
            
            Vector3d targetPos = targetTransform.getPosition();
            double dx = targetPos.getX() - searcherPos.getX();
            double dy = targetPos.getY() - searcherPos.getY();
            double dz = targetPos.getZ() - searcherPos.getZ();
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            
            if (dist < nearestDist) {
                nearest = player;
                nearestDist = dist;
            }
        }
        
        return nearest;
    }
    
    /**
     * Toggle lock-on for a player
     */
    public void toggleLockOn(PlayerRef playerRef, Player player) {
        UUID uuid = playerRef.getUuid();
        
        if (lockOnTargets.containsKey(uuid)) {
            // Remove lock-on
            lockOnTargets.remove(uuid);
            playerRef.sendMessage(Message.raw("✖ Lock-on désactivé").color("red"));
        } else {
            // Find target to lock onto
            Player target = findLockOnTarget(player, 15.0);
            if (target != null) {
                lockOnTargets.put(uuid, target.getUuid());
                playerRef.sendMessage(Message.raw("🎯 Lock-on activé!").color("green"));
            } else {
                playerRef.sendMessage(Message.raw("Aucune cible à proximité").color("gray"));
            }
        }
    }
    
    public boolean hasLockOn(UUID uuid) {
        return lockOnTargets.containsKey(uuid);
    }
    
    public UUID getLockedTargetUuid(UUID uuid) {
        return lockOnTargets.get(uuid);
    }
    
    private void onPlayerConnect(PlayerConnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        connectedPlayers.put(playerRef.getUuid(), playerRef);
        playerRef.sendMessage(Message.raw("[AnimeSword] Combat anime activé!").color("light_purple"));
    }
    
    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        UUID uuid = playerRef.getUuid();
        lockOnTargets.remove(uuid);
        connectedPlayers.remove(uuid);
    }
}
