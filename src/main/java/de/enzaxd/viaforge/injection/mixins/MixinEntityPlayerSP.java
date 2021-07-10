package de.enzaxd.viaforge.injection.mixins;

import com.mojang.authlib.GameProfile;
import de.enzaxd.viaforge.ViaForge;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    @Shadow protected abstract boolean isCurrentViewEntity();

    @Unique
    private boolean lastOnGround;

    public MixinEntityPlayerSP(World p_i45074_1_, GameProfile p_i45074_2_) {
        super(p_i45074_1_, p_i45074_2_);
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V"))
    public void redirectOnUpdateWalkingPlayer(NetHandlerPlayClient netHandlerPlayClient, Packet p_addToSendQueue_1_) {
        if (p_addToSendQueue_1_ instanceof C03PacketPlayer && ViaForge.getInstance().getVersion() !=
        ViaForge.SHARED_VERSION) {
            if (lastOnGround != onGround)
                netHandlerPlayClient.addToSendQueue(p_addToSendQueue_1_);
        } else
            netHandlerPlayClient.addToSendQueue(p_addToSendQueue_1_);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    public void injectOnUpdateWalkingPlayer(CallbackInfo ci) {
        if (this.isCurrentViewEntity())
            lastOnGround = onGround;
    }
}
