package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.TrackNodeLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrackNodeLocation.class, remap = false)
public class MixinTrackNodeLocation implements IHasTrackMaterial {

    private TrackMaterial trackMaterial = TrackMaterial.ANDESITE;

    @Override
    public TrackMaterial getMaterial() {
        return trackMaterial;
    }

    @Override
    public void setMaterial(TrackMaterial trackMaterial) {
        this.trackMaterial = trackMaterial;
    }

    @Inject(method = "equalsIgnoreDim", at = @At("HEAD"), cancellable = true)
    public void equalsIgnoreDim(Object pOther, CallbackInfoReturnable<Boolean> cir) {
        if (pOther instanceof IHasTrackMaterial hasMaterial && hasMaterial.getMaterial().trackType != this.trackMaterial.trackType) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeMaterial(DimensionPalette dimensions, CallbackInfoReturnable<CompoundTag> cir) {
        cir.getReturnValue().putString("M", trackMaterial.resName());
    }

    @Inject(method = "read", at = @At("HEAD"))
    private static void readMaterial(CompoundTag tag, DimensionPalette dimensions, CallbackInfoReturnable<TrackNodeLocation> cir) {
        if (tag.contains("M")) {
            TrackMaterial material = TrackMaterial.deserialize(tag.getString("M"));
            if (material != null) {
                ((IHasTrackMaterial) cir.getReturnValue()).setMaterial(material);
            }
        }
    }

    @Inject(method = "send", at = @At("TAIL"))
    private void sendMaterial(FriendlyByteBuf buffer, DimensionPalette dimensions, CallbackInfo ci) {
        buffer.writeUtf(trackMaterial.resName());
    }

    @Inject(method = "receive", at = @At("TAIL"))
    private static void receiveMaterial(FriendlyByteBuf buffer, DimensionPalette dimensions, CallbackInfoReturnable<TrackNodeLocation> cir) {
        TrackMaterial material = TrackMaterial.deserialize(buffer.readUtf());
        if (material != null) {
            ((IHasTrackMaterial) cir.getReturnValue()).setMaterial(material);
        }
    }
}
