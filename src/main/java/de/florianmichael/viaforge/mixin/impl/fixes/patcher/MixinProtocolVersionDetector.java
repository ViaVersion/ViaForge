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

package de.florianmichael.viaforge.mixin.impl.fixes.patcher;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "club.sk1er.patcher.util.status.ProtocolVersionDetector", remap = false)
public abstract class MixinProtocolVersionDetector {

    @SuppressWarnings("rawtypes, unchecked")
    @Dynamic
    @Inject(method = "isCompatibleWithVersion", at = @At("HEAD"), cancellable = true)
    private void viaforge$setCompatible(String ip, int version, CallbackInfoReturnable cir) {
        cir.setReturnValue(ViaForgeCommon.getManager().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_11));
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }

}
