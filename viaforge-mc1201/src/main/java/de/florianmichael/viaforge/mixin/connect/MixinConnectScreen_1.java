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
import de.florianmichael.viaforge.common.gui.ExtendedServerData;
import de.florianmichael.viaforge.common.platform.VersionTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.Optional;

@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Unique
    private ServerData viaforge$server;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(ConnectScreen this$0, String arg0, ServerAddress par3, Minecraft par4, ServerData par5, CallbackInfo ci) {
        viaforge$server = par5;
    }

    @SuppressWarnings("all")
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"))
    public Object trackServerVersion(Optional instance) {
        final InetSocketAddress address = (InetSocketAddress) instance.get();
        ProtocolVersion version = ((ExtendedServerData) viaforge$server).viaForge$getVersion();
        if (version == null) {
            version = ViaForgeCommon.getManager().getTargetVersion();
        }
        VersionTracker.storeServerProtocolVersion(address.getAddress(), version);
        return address;
    }

}
