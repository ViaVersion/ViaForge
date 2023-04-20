package de.florianmichael.viaforge.mixin.impl;

import de.florianmichael.viaprotocolhack.event.PipelineReorderEvent;
import io.netty.channel.Channel;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Shadow private Channel channel;

    @Inject(method = "setCompressionTreshold", at = @At("RETURN"))
    public void reOrderPipeline(int p_setCompressionTreshold_1_, CallbackInfo ci) {
        channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
    }
}
