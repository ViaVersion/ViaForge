package de.florianmichael.viaforge.mixin;

import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class MixinDebugScreenOverlay {

    @Inject(method = "getSystemInformation", at = @At(value = "TAIL"))
    public void addViaForgeVersion(CallbackInfoReturnable<List<String>> cir) {
        final ViaForgeCommon common = ViaForgeCommon.getManager();
        final VersionEnum version = ViaForgeCommon.getManager().getTargetVersion();

        if (common.getConfig().isShowProtocolVersionInF3() && version != common.getNativeVersion() && !common.getPlatform().isSingleplayer().get()) {
            cir.getReturnValue().add("");

            int protocolVersion = version.getVersion();
            if (version.isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
                // Older versions (<= 1.6.4) are using fake ids in ViaLegacy to prevent version duplications / mismatches
                // So we need to unmap the version to get the real protocol version id
                protocolVersion = LegacyProtocolVersion.getRealProtocolVersion(protocolVersion);
            }

            cir.getReturnValue().add("ViaForge: " + version.getName() + " (" + protocolVersion + ")");
        }
    }
}
