/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.query.Query
 *  com.hypixel.hytale.component.system.tick.EntityTickingSystem
 *  com.hypixel.hytale.server.core.entity.EntityUtils
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package org.zuxaw.plugin.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.zuxaw.plugin.RPGLevelingPlugin;
import org.zuxaw.plugin.components.PlayerLevelData;
import org.zuxaw.plugin.config.LevelingConfig;

public class LevelProgressHudSystem
extends EntityTickingSystem<EntityStore> {
    @Nonnull
    private final Query<EntityStore> query;
    private static final long HUD_POLL_INTERVAL_NANOS = 100000000L;
    private final Map<UUID, Long> lastHudPollNanos = new HashMap<UUID, Long>();

    public LevelProgressHudSystem() {
        this.query = Query.and((Query[])new Query[]{Player.getComponentType(), PlayerRef.getComponentType()});
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Long last;
        Holder holder = EntityUtils.toHolder((int)index, archetypeChunk);
        Player player = (Player)holder.getComponent(Player.getComponentType());
        PlayerRef playerRef = (PlayerRef)holder.getComponent(PlayerRef.getComponentType());
        if (player == null || playerRef == null) {
            return;
        }
        RPGLevelingPlugin plugin = RPGLevelingPlugin.get();
        if (plugin == null || plugin.getHudManager() == null) {
            return;
        }
        if (!((LevelingConfig)plugin.getConfig().get()).isEnableHUD()) {
            plugin.getHudManager().hideHud(player, playerRef);
            return;
        }
        boolean statsGuiOpen = plugin.isStatsGuiOpen(playerRef.getUuid());
        if (statsGuiOpen) {
            if (plugin.getHudManager().isHudVisible(playerRef)) {
                plugin.getHudManager().hideHud(player, playerRef);
            }
            return;
        }
        if (plugin.getHudManager().hasHud(playerRef) && !plugin.getHudManager().isHudVisible(playerRef)) {
            plugin.getHudManager().showHud(player, playerRef);
        }
        UUID playerId = playerRef.getUuid();
        long now = System.nanoTime();
        if (plugin.getHudManager().hasHud(playerRef) && (last = this.lastHudPollNanos.get(playerId)) != null && now - last < 100000000L) {
            return;
        }
        this.lastHudPollNanos.put(playerId, now);
        PlayerLevelData data = (PlayerLevelData)holder.getComponent(plugin.getLevelingService().getPlayerLevelDataType());
        if (data == null) {
            return;
        }
        plugin.getHudManager().updateHud(player, playerRef, data);
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }
}
