package de.florianmichael.viaforge.mixin.impl.fixes;

import com.mojang.authlib.GameProfile;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Unique
    private boolean viaForge$prevOnGround;

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V", ordinal = 7))
    public void emulateIdlePacket(NetHandlerPlayClient instance, Packet p_addToSendQueue_1_) {
        if (ViaForgeCommon.getManager().getTargetVersion().isNewerThan(VersionEnum.r1_8)) {
            // <= 1.8 spams the idle packet instead of only sending it when the ground state changes
            if (this.viaForge$prevOnGround == this.onGround) {
                return;
            }
        }
        instance.addToSendQueue(p_addToSendQueue_1_);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    public void saveGroundState(CallbackInfo ci) {
        this.viaForge$prevOnGround = this.onGround;
    }
}
