/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.CommandBuffer
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.component.spatial.SpatialResource
 *  com.hypixel.hytale.math.util.TrigMathUtil
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.math.vector.Vector4d
 *  com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle
 *  com.hypixel.hytale.server.core.modules.entity.EntityModule
 *  com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$EntitySource
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$Source
 *  com.hypixel.hytale.server.core.universe.world.ParticleUtil
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  it.unimi.dsi.fastutil.objects.ObjectList
 */
package org.narwhals.plugin;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.List;

public class EffectUtil {
    public static void spawnCombatParticle(WorldParticle particle, Damage damage, Vector3d tempTargetPos, TransformComponent victimTransform, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        TransformComponent sourceTransform;
        Damage.EntitySource entitySource;
        Ref attackerRef;
        Vector4d hitLocation = (Vector4d)damage.getIfPresentMetaObject(Damage.HIT_LOCATION);
        if (hitLocation != null) {
            tempTargetPos.x = hitLocation.x;
            tempTargetPos.y = hitLocation.y;
            tempTargetPos.z = hitLocation.z;
        } else if (victimTransform != null) {
            Vector3d transformPos = victimTransform.getPosition();
            tempTargetPos.x = transformPos.x;
            tempTargetPos.y = transformPos.y;
            tempTargetPos.z = transformPos.z;
        } else {
            return;
        }
        float angleBetween = 0.0f;
        Damage.Source source = damage.getSource();
        if (source instanceof Damage.EntitySource && (attackerRef = (entitySource = (Damage.EntitySource)source).getRef()).isValid() && (sourceTransform = (TransformComponent)commandBuffer.getComponent(attackerRef, TransformComponent.getComponentType())) != null) {
            Vector3d sourcePos = sourceTransform.getPosition();
            angleBetween = TrigMathUtil.atan2((double)(sourcePos.x - tempTargetPos.x), (double)(sourcePos.z - tempTargetPos.z));
        }
        SpatialResource playerSpatialResource = (SpatialResource)store.getResource(EntityModule.get().getPlayerSpatialResourceType());
        ObjectList viewers = SpatialResource.getThreadLocalReferenceList();
        playerSpatialResource.getSpatialStructure().collect(tempTargetPos, 32.0, (List)viewers);
        ParticleUtil.spawnParticleEffect((WorldParticle)particle, (Vector3d)tempTargetPos, (float)angleBetween, (float)1.0f, (float)1.0f, null, (List)viewers, commandBuffer);
    }
}
