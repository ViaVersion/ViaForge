package com.github.creeper123123321.viafabric.injection.mixins;

import com.github.creeper123123321.viafabric.ViaFabric;
import com.github.creeper123123321.viafabric.util.ProtocolUtils;
import de.flori2007.viaforge.gui.GuiProtocolSelector;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenAddServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenAddServer.class)
public abstract class MixinGuiScreenAddServer extends GuiScreen {

    @Inject(method = "initGui", at = @At("RETURN"))
    public void injectInitGui(CallbackInfo ci) {
        buttonList.add(new GuiButton(1337, 5, 6, 98, 20,
                ProtocolUtils.getProtocolName(ViaFabric.clientSideVersion)));
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void injectActionPerformed(GuiButton p_actionPerformed_1_, CallbackInfo ci) {
        if (p_actionPerformed_1_.id == 1337)
            mc.displayGuiScreen(new GuiProtocolSelector(this));
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void injectDrawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_, CallbackInfo ci) {
        mc.fontRendererObj.drawStringWithShadow("<-- Current Version",
                104, 13, -1);
    }
}
