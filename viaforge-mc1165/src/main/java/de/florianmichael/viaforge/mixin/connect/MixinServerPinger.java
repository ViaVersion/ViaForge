/*
 * This file is part of ViaForge - https://github.com/FlorianMichael/ViaForge
 * Copyright (C) 2021-2026 FlorianMichael/EnZaXD <git@florianmichael.de> and contributors
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

package de.florianmichael.viaforge.mixin.connect;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.extended.ExtendedServerData;
import de.florianmichael.viaforge.common.platform.VersionTracker;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPinger.class)
public class MixinServerPinger {

    @Unique
    private ServerData viaForge$serverData;

    @Inject(method = "pingServer", at = @At("HEAD"))
    public void trackServerData(ServerData server, Runnable p_147224_2_, CallbackInfo ci) {
        viaForge$serverData = server;
    }

    @Inject(method = "pingServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;connectToServer(Ljava/net/InetAddress;IZ)Lnet/minecraft/network/NetworkManager;"))
    public void trackVersion(ServerData p_147224_1_, Runnable p_147224_2_, CallbackInfo ci) throws UnknownHostException {
        ProtocolVersion version = ((ExtendedServerData) viaForge$serverData).viaForge$getVersion();
        if (version == null) {
            version = ViaForgeCommon.getManager().getTargetVersion();
        }
        // Workaround for GH-240 where another mod is also redirecting the connect function
        VersionTracker.storeServerProtocolVersion(InetAddress.getByName(ServerAddress.parseString(p_147224_1_.ip).getHost()), version);
        viaForge$serverData = null;
    }

}
