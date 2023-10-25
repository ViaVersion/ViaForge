package de.florianmichael.viaforge.mixin;

import de.florianmichael.viaforge.common.gui.ExtendedServerData;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundNBT;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerData.class)
public class MixinServerData implements ExtendedServerData {

    @Unique
    private VersionEnum viaforge_version;

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundNBT;putString(Ljava/lang/String;Ljava/lang/String;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void saveVersion(CallbackInfoReturnable<CompoundNBT> cir, CompoundNBT compoundnbt) {
        if (viaforge_version != null) {
            compoundnbt.putInt("viaforge_version", viaforge_version.getVersion());
        }
    }

    @Inject(method = "read", at = @At(value = "TAIL"))
    private static void getVersion(CompoundNBT compoundnbt, CallbackInfoReturnable<ServerData> cir) {
        if (compoundnbt.contains("viaforge_version")) {
            ((ExtendedServerData) cir.getReturnValue()).viaforge_setVersion(VersionEnum.fromProtocolId(compoundnbt.getInt("viaforge_version")));
        }
    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    public void track(ServerData serverDataIn, CallbackInfo ci) {
        if (serverDataIn instanceof ExtendedServerData) {
            viaforge_version = ((ExtendedServerData) serverDataIn).viaforge_getVersion();
        }
    }

    @Override
    public VersionEnum viaforge_getVersion() {
        return viaforge_version;
    }

    @Override
    public void viaforge_setVersion(VersionEnum version) {
        viaforge_version = version;
    }
}
