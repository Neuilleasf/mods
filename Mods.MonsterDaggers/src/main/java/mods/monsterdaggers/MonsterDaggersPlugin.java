package mods.monsterdaggers;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class MonsterDaggersPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String MONSTER_DAGGER_ITEM_ID = "Weapon_Monster_Dagger";
    private static final double LOCK_RANGE = 12.0;
    private static final double LOCK_CONE_DEGREES = 60.0;
    private static final double TELEPORT_RADIUS = 1.8;
    private static final long STEP_INTERVAL_MS = 170L;

    private final Map<UUID, UUID> lockOnTargets = new ConcurrentHashMap<>();
    private final Map<UUID, ComboState> activeCombos = new ConcurrentHashMap<>();

    private ScheduledFuture<?> updateTask;

    public MonsterDaggersPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("[MonsterDaggers] Setup...");
    }

    @Override
    public void start() {
        getEventRegistry().register(PlayerConnectEvent.class, this::onPlayerConnect);
        getEventRegistry().register(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        getEventRegistry().registerGlobal(PlayerInteractEvent.class, this::onPlayerInteract);

        startUpdateLoop();

        LOGGER.at(Level.INFO).log("[MonsterDaggers] Plugin started!");
    }

    @Override
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel(false);
        }
        lockOnTargets.clear();
        activeCombos.clear();
        LOGGER.at(Level.INFO).log("[MonsterDaggers] Plugin disabled!");
    }

    private void onPlayerConnect(PlayerConnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        playerRef.sendMessage(Message.raw("[MonsterDaggers] Combat prêt.").color("light_purple"));
    }

    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        UUID uuid = playerRef.getUuid();
        lockOnTargets.remove(uuid);
        activeCombos.remove(uuid);
    }

    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        ItemStack itemInHand = event.getItemInHand();
        if (!isMonsterDaggers(itemInHand)) {
            return;
        }

        String itemId = itemInHand != null ? itemInHand.getItemId() : "<none>";
        InteractionType actionType = event.getActionType();
        LOGGER.at(Level.INFO).log("[MonsterDaggers] Interact: player=%s item=%s action=%s", player.getUuid(), itemId, actionType);
        if (actionType == InteractionType.Primary) {
            Entity hitTarget = event.getTargetEntity();
            if (hitTarget != null && !hitTarget.wasRemoved()) {
                lockOnTargets.put(player.getUuid(), hitTarget.getUuid());
                LOGGER.at(Level.INFO).log("[MonsterDaggers] Primary hit lock: player=%s target=%s", player.getUuid(), hitTarget.getUuid());
            }
            return;
        }
        if (!isSignatureInteraction(actionType)) {
            return;
        }

        LOGGER.at(Level.INFO).log("[MonsterDaggers] Ability triggered: player=%s item=%s action=%s", player.getUuid(), itemId, actionType);

        World world = player.getWorld();
        if (world == null) {
            return;
        }

        Entity target = event.getTargetEntity();
        if (target == null || target.wasRemoved()) {
            UUID lockedTarget = lockOnTargets.get(player.getUuid());
            if (lockedTarget != null) {
                target = world.getEntity(lockedTarget);
            }
        }
        if (target == null || target.wasRemoved()) {
            target = findBestTargetInCone(player, LOCK_RANGE, LOCK_CONE_DEGREES);
        }

        if (target == null || target.wasRemoved()) {
            LOGGER.at(Level.INFO).log("[MonsterDaggers] No valid target for %s", player.getUuid());
            return;
        }

        UUID playerUuid = player.getUuid();
        UUID targetUuid = target.getUuid();

        LOGGER.at(Level.INFO).log("[MonsterDaggers] Target locked: %s", targetUuid);

        event.setCancelled(true);
        lockOnTargets.put(playerUuid, targetUuid);
        startCombo(playerUuid, targetUuid, world);
    }

    private void startUpdateLoop() {
        updateTask = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                World world = Universe.get().getDefaultWorld();
                if (world == null) return;
                world.execute(() -> updateLockOns(world));
            } catch (Exception e) {
                LOGGER.at(Level.WARNING).log("[MonsterDaggers] Update loop error: %s", e.getMessage());
            }
        }, 100, 50, TimeUnit.MILLISECONDS);
    }

    private void updateLockOns(World world) {
        for (Player player : world.getPlayers()) {
            UUID playerUuid = player.getUuid();
            UUID targetUuid = lockOnTargets.get(playerUuid);
            if (targetUuid == null) {
                continue;
            }
            Entity target = world.getEntity(targetUuid);
            if (target == null || target.wasRemoved()) {
                lockOnTargets.remove(playerUuid);
                continue;
            }
            faceEntity(player, target);
        }
    }

    private void startCombo(UUID playerUuid, UUID targetUuid, World world) {
        ComboState state = new ComboState(targetUuid);
        activeCombos.put(playerUuid, state);

        for (int step = 0; step < 4; step++) {
            final int stepIndex = step;
            HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
                World currentWorld = world;
                if (currentWorld == null || !currentWorld.isAlive()) {
                    return;
                }
                currentWorld.execute(() -> performComboStep(playerUuid, stepIndex, currentWorld));
            }, STEP_INTERVAL_MS * step, TimeUnit.MILLISECONDS);
        }

        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> activeCombos.remove(playerUuid), STEP_INTERVAL_MS * 4, TimeUnit.MILLISECONDS);
    }

    private void performComboStep(UUID playerUuid, int stepIndex, World world) {
        Entity playerEntity = world.getEntity(playerUuid);
        if (!(playerEntity instanceof Player player)) {
            return;
        }
        ComboState state = activeCombos.get(playerUuid);
        if (state == null) {
            return;
        }

        Entity target = world.getEntity(state.targetUuid);
        if (target == null || target.wasRemoved()) {
            activeCombos.remove(playerUuid);
            return;
        }

        TransformComponent targetTransform = target.getTransformComponent();
        TransformComponent playerTransform = player.getTransformComponent();
        if (targetTransform == null || playerTransform == null) {
            return;
        }

        Vector3d targetPos = targetTransform.getPosition();
        Vector3f targetRot = targetTransform.getRotation();
        Vector3d forward = computeForward(targetPos, targetRot, playerTransform.getPosition());
        Vector3d left = new Vector3d(forward.z, 0, -forward.x);

        Vector3d offset;
        switch (stepIndex) {
            case 0 -> offset = new Vector3d(-forward.x * TELEPORT_RADIUS, 0, -forward.z * TELEPORT_RADIUS); // behind
            case 1 -> offset = new Vector3d(-left.x * TELEPORT_RADIUS, 0, -left.z * TELEPORT_RADIUS);      // right
            case 2 -> offset = new Vector3d(left.x * TELEPORT_RADIUS, 0, left.z * TELEPORT_RADIUS);        // left
            default -> offset = new Vector3d(forward.x * TELEPORT_RADIUS, 0, forward.z * TELEPORT_RADIUS); // front
        }

        double newX = targetPos.x + offset.x;
        double newY = targetPos.y;
        double newZ = targetPos.z + offset.z;

        EntityStore store = world.getEntityStore();
        player.moveTo(player.getReference(), newX, newY, newZ, store.getStore());
        faceEntity(player, target);
    }

    private Vector3d computeForward(Vector3d targetPos, Vector3f targetRot, Vector3d fallbackFrom) {
        if (targetRot != null) {
            double yawRad = Math.toRadians(targetRot.y);
            return new Vector3d(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();
        }
        Vector3d dir = new Vector3d(targetPos.x - fallbackFrom.x, 0, targetPos.z - fallbackFrom.z);
        return dir.length() > 0.001 ? dir.normalize() : new Vector3d(0, 0, 1);
    }

    private void faceEntity(Player player, Entity target) {
        TransformComponent playerTransform = player.getTransformComponent();
        TransformComponent targetTransform = target.getTransformComponent();
        if (playerTransform == null || targetTransform == null) {
            return;
        }

        Vector3d playerPos = playerTransform.getPosition();
        Vector3d targetPos = targetTransform.getPosition();

        double dx = targetPos.getX() - playerPos.getX();
        double dy = targetPos.getY() - playerPos.getY();
        double dz = targetPos.getZ() - playerPos.getZ();

        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(Math.atan2(-dy, horizontalDist));

        playerTransform.setRotation(new Vector3f(pitch, yaw, 0));
    }

    private boolean isMonsterDaggers(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return false;
        }
        String itemId = itemStack.getItemId();
        if (itemId == null || itemId.isEmpty()) {
            return false;
        }
        return MONSTER_DAGGER_ITEM_ID.equals(itemId)
                || itemId.startsWith("Weapon_Daggers_")
                || itemId.startsWith("Weapon_Dagger");
    }

    private boolean isSignatureInteraction(InteractionType type) {
        if (type == null) {
            return false;
        }
        return type == InteractionType.Ability1
                || type == InteractionType.Ability2
                || type == InteractionType.Ability3;
    }

    private Entity findBestTargetInCone(Player searcher, double maxDistance, double coneDegrees) {
        World world = searcher.getWorld();
        if (world == null) {
            return null;
        }

        TransformComponent searcherTransform = searcher.getTransformComponent();
        if (searcherTransform == null) {
            return null;
        }

        Vector3d searcherPos = searcherTransform.getPosition();
        Vector3f searcherRot = searcherTransform.getRotation();
        Vector3d forward = computeLookDirection(searcherRot);

        Store<EntityStore> store = world.getEntityStore().getStore();

        final Entity[] best = { null };
        final double[] bestDot = { -1.0 };
        final double[] bestDist = { maxDistance };
        double cosHalfCone = Math.cos(Math.toRadians(coneDegrees * 0.5));

        Object lock = new Object();

        store.forEachEntityParallel((chunkIndex, chunk, commandBuffer) -> {
            int size = chunk.size();
            for (int i = 0; i < size; i++) {
                UUIDComponent uuidComponent = chunk.getComponent(i, UUIDComponent.getComponentType());
                TransformComponent targetTransform = chunk.getComponent(i, TransformComponent.getComponentType());
                if (uuidComponent == null || targetTransform == null) {
                    continue;
                }

                UUID targetUuid = uuidComponent.getUuid();
                if (targetUuid == null || targetUuid.equals(searcher.getUuid())) {
                    continue;
                }

                Vector3d targetPos = targetTransform.getPosition();
                double dx = targetPos.getX() - searcherPos.getX();
                double dy = targetPos.getY() - searcherPos.getY();
                double dz = targetPos.getZ() - searcherPos.getZ();
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist > maxDistance || dist < 0.001) {
                    continue;
                }

                Vector3d dir = new Vector3d(dx / dist, dy / dist, dz / dist);
                double dot = forward.x * dir.x + forward.y * dir.y + forward.z * dir.z;
                if (dot < cosHalfCone) {
                    continue;
                }

                Entity candidate = world.getEntity(targetUuid);
                if (candidate == null || candidate.wasRemoved()) {
                    continue;
                }

                synchronized (lock) {
                    if (dot > bestDot[0] || (Math.abs(dot - bestDot[0]) < 0.0001 && dist < bestDist[0])) {
                        best[0] = candidate;
                        bestDot[0] = dot;
                        bestDist[0] = dist;
                    }
                }
            }
        });

        return best[0];
    }

    private Vector3d computeLookDirection(Vector3f rotation) {
        if (rotation == null) {
            return new Vector3d(0, 0, 1);
        }
        double yawRad = Math.toRadians(rotation.y);
        double pitchRad = Math.toRadians(rotation.x);
        double cosPitch = Math.cos(pitchRad);
        return new Vector3d(
            -Math.sin(yawRad) * cosPitch,
            -Math.sin(pitchRad),
            Math.cos(yawRad) * cosPitch
        ).normalize();
    }

    private static final class ComboState {
        private final UUID targetUuid;

        private ComboState(UUID targetUuid) {
            this.targetUuid = targetUuid;
        }
    }
}
