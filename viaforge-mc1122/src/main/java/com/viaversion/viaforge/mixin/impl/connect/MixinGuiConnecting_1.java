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

package com.viaversion.viaforge.mixin.impl.connect;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaforge.common.ViaForgeCommon;
import com.viaversion.viaforge.common.extended.ExtendedServerData;
import com.viaversion.viaforge.common.platform.VersionTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Mixin(targets = "net.minecraft.client.multiplayer.GuiConnecting$1")
public class MixinGuiConnecting_1 {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetAddress;getByName(Ljava/lang/String;)Ljava/net/InetAddress;"))
    public InetAddress trackServerVersion(String s) throws UnknownHostException {
        final InetAddress address = InetAddress.getByName(s);
        ProtocolVersion version = ((ExtendedServerData) Minecraft.getMinecraft().getCurrentServerData()).viaForge$getVersion();
        if (version == null) {
            version = ViaForgeCommon.getManager().getTargetVersion();
        }
        VersionTracker.storeServerProtocolVersion(address, version);
        return address;
    }

}
