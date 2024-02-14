/*
 * This file is part of ViaForge - https://github.com/FlorianMichael/ViaForge
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viaforge.mixin;

import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
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
        final ProtocolVersion version = ViaForgeCommon.getManager().getTargetVersion();

        if (common.getConfig().isShowProtocolVersionInF3() && version != common.getNativeVersion() && !common.getPlatform().isSingleplayer().get()) {
            cir.getReturnValue().add("");

            int protocolVersion = version.getVersion();
            if (version.olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
                // Older versions (<= 1.6.4) are using fake ids in ViaLegacy to prevent version duplications / mismatches
                // So we need to unmap the version to get the real protocol version id
                protocolVersion = LegacyProtocolVersion.getRealProtocolVersion(protocolVersion);
            }

            cir.getReturnValue().add("ViaForge: " + version.getName() + " (" + protocolVersion + ")");
        }
    }

}
