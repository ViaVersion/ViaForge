package de.enzaxd.viaforge.injection.mixins;

import de.enzaxd.viaforge.ViaForge;
import de.enzaxd.viaforge.gui.GuiProtocolSelector;
import de.enzaxd.viaforge.protocol.ProtocolCollection;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDisconnected.class)
public abstract class MixinGuiDisconnected extends GuiScreen {

    @Inject(method = "initGui", at = @At("RETURN"))
    public void injectInitGui(CallbackInfo ci) {
        buttonList.add(new GuiButton(1337, 5, 6, 98, 20,
                ProtocolCollection.getProtocolById(ViaForge.getInstance().getVersion()).getName()));
        buttonList.add(new GuiButton(1338, 5, 28, 98, 20, "Reconnect"));
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void injectActionPerformed(GuiButton p_actionPerformed_1_, CallbackInfo ci) {
        if (p_actionPerformed_1_.id == 1337)
            mc.displayGuiScreen(new GuiProtocolSelector(this));
        else if (p_actionPerformed_1_.id == 1338)
            mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new GuiMainMenu()), mc,
                    new ServerData(ViaForge.getInstance().getLastServer(), ViaForge.getInstance().getLastServer(),
                            false)));
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void injectDrawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_, CallbackInfo ci) {
        mc.fontRendererObj.drawStringWithShadow("<-- Current Version",
                104, 13, -1);
    }
}
