package de.florianmichael.viaforge.mixin.fixes;

import com.mojang.authlib.GameProfile;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer extends AbstractClientPlayer {

    @Shadow private boolean lastOnGround;

    public MixinLocalPlayer(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

    @Redirect(method = "sendPosition", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;lastOnGround:Z", ordinal = 0))
    public boolean emulateIdlePacket(LocalPlayer instance) {
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
