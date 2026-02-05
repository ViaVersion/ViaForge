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

package com.viaversion.viaforge.mixin.connect;

import com.viaversion.viaforge.common.ViaForgeCommon;
import com.viaversion.viaforge.common.extended.ExtendedServerData;
import com.viaversion.viaforge.common.platform.VersionTracker;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.network.Connection;
import net.minecraft.util.SampleLogger;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ServerStatusPinger.class)
public class MixinServerStatusPinger {

    @Unique
    private ServerData viaForge$serverData;

    @Inject(method = "pingServer", at = @At("HEAD"))
    public void trackServerData(ServerData server, Runnable p_147224_2_, CallbackInfo ci) {
        viaForge$serverData = server;
    }

    @Redirect(method = "pingServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connectToServer(Ljava/net/InetSocketAddress;ZLnet/minecraft/util/SampleLogger;)Lnet/minecraft/network/Connection;"))
    public Connection trackVersion(InetSocketAddress address, boolean b, SampleLogger sampleLogger) {
        ProtocolVersion version = ((ExtendedServerData) viaForge$serverData).viaForge$getVersion();
        if (version == null) {
            version = ViaForgeCommon.getManager().getTargetVersion();
        }
        VersionTracker.storeServerProtocolVersion(address.getAddress(), version);
        viaForge$serverData = null;

        return Connection.connectToServer(address, b, sampleLogger);
    }

}
