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

package com.viaversion.viaforge.mixin;

import com.viaversion.viaforge.common.extended.ExtendedServerData;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerData.class)
public class MixinServerData implements ExtendedServerData {

    @Unique
    private ProtocolVersion viaForge$version;

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putString(Ljava/lang/String;Ljava/lang/String;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void saveVersion(CallbackInfoReturnable<CompoundTag> cir, CompoundTag compoundtag) {
        if (viaForge$version != null) {
            compoundtag.putString("viaForge$version", viaForge$version.getName());
        }
    }

    @Inject(method = "read", at = @At(value = "TAIL"))
    private static void getVersion(CompoundTag compoundnbt, CallbackInfoReturnable<ServerData> cir) {
        if (compoundnbt.contains("viaForge$version")) {
            ((ExtendedServerData) cir.getReturnValue()).viaForge$setVersion(ProtocolVersion.getClosest(compoundnbt.getStringOr("viaForge$version", "")));
        }
    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    public void track(ServerData serverDataIn, CallbackInfo ci) {
        if (serverDataIn instanceof ExtendedServerData) {
            viaForge$version = ((ExtendedServerData) serverDataIn).viaForge$getVersion();
        }
    }

    @Override
    public ProtocolVersion viaForge$getVersion() {
        return viaForge$version;
    }

    @Override
    public void viaForge$setVersion(ProtocolVersion version) {
        viaForge$version = version;
    }

}

