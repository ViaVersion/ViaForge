/*
 * This file is part of ViaForge - https://github.com/ViaVersion/ViaForge
 * Copyright (C) 2021-2026 Florian Reuth <git@florianreuth.de> and contributors
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

package com.viaversion.viaforge.mixin.impl;

import com.viaversion.viaforge.common.ViaForgeCommon;
import com.viaversion.viaforge.mixin.impl.interfaces.IS08;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.IOException;

@Mixin(S08PacketPlayerPosLook.class)
public class MixinS08PacketPlayerPosLook implements IS08 {

    @Unique
    private int teleportId;

    @Inject(method = "readPacketData", at = @At("RETURN"))
    public void onReadPacketData(PacketBuffer buf, CallbackInfo ci) throws IOException {
        if (ViaForgeCommon.getManager().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_9)) {
            this.teleportId = buf.readVarIntFromBuffer();
        }
    }

    @Override
    public int getTeleportId() {
        return this.teleportId;
    }
}
