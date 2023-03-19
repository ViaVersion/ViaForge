package de.florianmichael.viaforge;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import io.netty.channel.DefaultEventLoop;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "viaforge", name = "ViaForge", version = "3.0.0")
public class ViaForge {

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        ViaLoadingBase.ViaLoadingBaseBuilder.
                create().
                runDirectory(Minecraft.getMinecraft().gameDir).
                nativeVersion(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION).
                forceNativeVersionCondition(() -> Minecraft.getMinecraft().isSingleplayer()).
                build();
    }
}
