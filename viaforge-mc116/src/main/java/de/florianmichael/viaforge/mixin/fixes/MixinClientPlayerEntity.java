package de.florianmichael.viaforge.mixin.fixes;

import com.mojang.authlib.GameProfile;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    @Shadow private boolean lastOnGround;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "sendPosition", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;lastOnGround:Z", ordinal = 0))
    public boolean emulateIdlePacket(ClientPlayerEntity instance) {
        if (ViaForgeCommon.getManager().getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            // <= 1.8 spams the idle packet instead of only sending it when the ground state changes
            // So we invert the original logic:
            // if (prevOnGround != onGround) sendPacket
            // To be like:
            // if (!onGround != onGround) sendPacket
            // Which is the same as:
            // if (true) sendPacket
            return !onGround;
        }
        return lastOnGround;
    }
}
