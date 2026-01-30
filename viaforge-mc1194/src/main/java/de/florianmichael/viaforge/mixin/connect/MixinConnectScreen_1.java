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

import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.extended.ExtendedServerData;
import de.florianmichael.viaforge.common.platform.VersionTracker;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;
import java.util.Optional;

@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Shadow @Final ServerData val$p_252078_;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"))
    public Object trackServerVersion(Optional instance) {
        final InetSocketAddress address = (InetSocketAddress) instance.get();
        ProtocolVersion version = ((ExtendedServerData) val$p_252078_).viaForge$getVersion();
        if (version == null) {
            version = ViaForgeCommon.getManager().getTargetVersion();
        }
        VersionTracker.storeServerProtocolVersion(address.getAddress(), version);
        return address;
    }

}
