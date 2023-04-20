package de.florianmichael.viaforge;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsSharedConstants;

import java.io.File;

public class ViaForge {

    public static void start() {
        ViaLoadingBase.ViaLoadingBaseBuilder.
                create().
                runDirectory(Minecraft.getMinecraft().dataDir). // gameDir -> dataDir
                nativeVersion(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION).
                forceNativeVersionCondition(() -> Minecraft.getMinecraft().isSingleplayer()).
                build();
    }
}
