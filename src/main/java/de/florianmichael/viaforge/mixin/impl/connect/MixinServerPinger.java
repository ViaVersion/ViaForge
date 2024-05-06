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

package de.florianmichael.viaforge.mixin.impl.connect;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.gui.ExtendedServerData;
import de.florianmichael.viaforge.common.platform.VersionTracker;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetAddress;

@Mixin(OldServerPinger.class)
public class MixinServerPinger {

    @Unique
    private ServerData viaForge$serverData;

    @Inject(method = "ping", at = @At("HEAD"))
    public void trackServerData(ServerData server, CallbackInfo ci) {
        viaForge$serverData = server;
    }

    @Redirect(method = "ping", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;createNetworkManagerAndConnect(Ljava/net/InetAddress;IZ)Lnet/minecraft/network/NetworkManager;"))
    public NetworkManager trackVersion(InetAddress address, int i, boolean b) {
        ProtocolVersion version = ((ExtendedServerData) viaForge$serverData).viaForge$getVersion();
        if (version == null) {
            version = ViaForgeCommon.getManager().getTargetVersion();
        }
        VersionTracker.storeServerProtocolVersion(address, version);
        viaForge$serverData = null;

        return NetworkManager.createNetworkManagerAndConnect(address, i, b);
    }

}
