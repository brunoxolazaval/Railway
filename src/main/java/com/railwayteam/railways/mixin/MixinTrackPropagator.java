package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.*;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalPropagator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = TrackPropagator.class, remap = false)
public class MixinTrackPropagator {
    @Inject(method = "isValidGraphNodeLocation", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/trains/TrackNodeLocation$DiscoveredLocation;getLocation()Lnet/minecraft/world/phys/Vec3;"), cancellable = true)
    private static void disconnectDifferentTrackTypes(TrackNodeLocation.DiscoveredLocation location, Collection<TrackNodeLocation.DiscoveredLocation> next, boolean first, CallbackInfoReturnable<Boolean> cir) {
        /*if (next.stream().anyMatch(otherLoc -> {
            IHasTrackMaterial myMat = (IHasTrackMaterial) location;
            IHasTrackMaterial otherMat = (IHasTrackMaterial) otherLoc;
            return myMat.getMaterial().trackType != otherMat.getMaterial().trackType;
        })) {
            cir.setReturnValue(true);
        }*/
    }
}
