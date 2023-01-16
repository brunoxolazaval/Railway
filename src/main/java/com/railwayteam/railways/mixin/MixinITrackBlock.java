package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.ITrackBlock;
import com.simibubi.create.content.logistics.trains.TrackNodeLocation;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackShape;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(value = ITrackBlock.class, remap = false)
public interface MixinITrackBlock {
    @Shadow double getElevationAtCenter(BlockGetter world, BlockPos pos, BlockState state);

    @Shadow List<Vec3> getTrackAxes(BlockGetter world, BlockPos pos, BlockState state);

    @Shadow
    static void addToListIfConnected(@org.jetbrains.annotations.Nullable TrackNodeLocation fromEnd, Collection<TrackNodeLocation.DiscoveredLocation> list, BiFunction<Double, Boolean, Vec3> offsetFactory, Function<Boolean, Vec3> normalFactory, Function<Boolean, ResourceKey<Level>> dimensionFactory, Vec3 axis, BezierConnection viaTurn) {
        throw new AssertionError("Should have been mixed in");
    }

    /**
     * @author Railroads
     * @reason Prevent normal tracks and monorails from connecting, and you can't @Inject in interfaces :(
     */
    @Overwrite
    default Collection<TrackNodeLocation.DiscoveredLocation> getConnected(BlockGetter worldIn, BlockPos pos, BlockState state,
                                                                                 boolean linear, @Nullable TrackNodeLocation connectedTo) {
        BlockGetter world = connectedTo != null && worldIn instanceof ServerLevel sl ? sl.getServer()
            .getLevel(connectedTo.dimension) : worldIn;
        Vec3 center = Vec3.atBottomCenterOf(pos)
            .add(0, getElevationAtCenter(world, pos, state), 0);
        List<TrackNodeLocation.DiscoveredLocation> list = new ArrayList<>();
        TrackShape shape = state.getValue(TrackBlock.SHAPE);
        getTrackAxes(world, pos, state).forEach(axis -> {
            TrackMaterial material = TrackMaterial.ANDESITE;
            if (world != null && world.getBlockState(new BlockPos(center.add(axis))).getBlock() instanceof IHasTrackMaterial mat1) {
                material = mat1.getMaterial();
            }
            List<TrackNodeLocation.DiscoveredLocation> tmpList = new ArrayList<>();
            addToListIfConnected(connectedTo, tmpList, (d, b) -> axis.scale(b ? d : -d)
                    .add(center), b -> shape.getNormal(), b -> world instanceof Level l ? l.dimension() : Level.OVERWORLD,
                axis, null);

            TrackMaterial finalMaterial = material;
            tmpList.forEach(loc -> {
                ((IHasTrackMaterial) loc).setMaterial(finalMaterial);
            });
            list.addAll(tmpList);
        });

        return list;
    }
    /*default void doNotConnectToOtherType(TrackNodeLocation connectedTo, List<TrackNodeLocation.DiscoveredLocation> list, Vec3 center, TrackShape shape, BlockGetter world, Vec3 axis, CallbackInfo ci) {
        if (world.getBlockState(new BlockPos(center.add(axis))).getBlock() instanceof IHasTrackMaterial mat1 &&
            world.getBlockState(new BlockPos(center)).getBlock() instanceof IHasTrackMaterial mat2 &&
            mat1.getMaterial().trackType != mat2.getMaterial().trackType) {
            ci.cancel();
        }
    }*/
}
