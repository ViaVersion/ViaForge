package de.florianmichael.viaforge.mixin.impl.fixes;

import com.mojang.authlib.GameProfile;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {

    @Shadow private boolean prevOnGround;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;prevOnGround:Z", ordinal = 0))
    public boolean emulateIdlePacket(EntityPlayerSP instance) {
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
        return prevOnGround;
    }
}
