/*
 * This file is part of ViaForge - https://github.com/FlorianMichael/ViaForge
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
import de.florianmichael.viaforge.common.gui.ExtendedServerData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connectToServer(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/network/Connection;"))
    public Connection trackVersion(InetSocketAddress oclass, boolean lazyloadedvalue) {
        // We need to track the version of the server we are connecting to, so we can later
        // use it to determine the protocol version to use.
        // We hope that the current server data is not null
        if (Minecraft.getInstance().getCurrentServer() instanceof ExtendedServerData) {
            final VersionEnum version = ((ExtendedServerData) Minecraft.getInstance().getCurrentServer()).viaForge$getVersion();
            if (version != null) {
                ViaForgeCommon.getManager().setTargetVersionSilent(version);
            } else {
                // If the server data does not contain a version, we need to restore the version
                // we had before, so we don't use the wrong version.
                ViaForgeCommon.getManager().restoreVersion();
            }
        }

        return Connection.connectToServer(oclass, lazyloadedvalue);
    }

}
