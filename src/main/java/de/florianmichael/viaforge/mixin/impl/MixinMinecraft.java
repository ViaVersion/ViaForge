package de.florianmichael.viaforge.mixin.impl;

import de.florianmichael.viaforge.ViaForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void startVia(GameConfiguration p_i45547_1_, CallbackInfo ci) {
        ViaForge.start();
    }
}
