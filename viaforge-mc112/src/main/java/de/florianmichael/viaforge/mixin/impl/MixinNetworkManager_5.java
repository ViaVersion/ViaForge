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
package de.florianmichael.viaforge.mixin.impl;

import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.protocolhack.netty.VFNetworkManager;
import io.netty.channel.Channel;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.NetworkManager$5")
public class MixinNetworkManager_5 {

    @Final
    @Mutable
    NetworkManager val$networkmanager;

    @Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        // We need to access this class later to call the viaforge_setupPreNettyDecryption method.
        // In one of the ViaLegacy's required providers, so we track this class instance as an own
        // attribute in the connection and later access it from there and remove it.
        // Counterpart in {@link java/de/florianmichael/viaforge/common/protocolhack/provider/ViaForgeEncryptionProvider.java}
        channel.attr(ViaForgeCommon.VF_NETWORK_MANAGER).set((VFNetworkManager) val$networkmanager);

        ViaForgeCommon.getManager().inject(channel);
    }
}
