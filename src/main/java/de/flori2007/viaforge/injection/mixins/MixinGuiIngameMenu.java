package de.flori2007.viaforge.injection.mixins;

import com.github.creeper123123321.viafabric.ViaFabric;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public abstract class MixinGuiIngameMenu extends GuiScreen {

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void injectDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (ViaFabric.clientSideVersion != ViaFabric.nativeVersion) {
            String text = ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "ViaForge is active! Selected Version: " + ViaFabric.nativeVersion;
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, width / 2 -
                    (Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2), 1, -1);
        }
    }
}