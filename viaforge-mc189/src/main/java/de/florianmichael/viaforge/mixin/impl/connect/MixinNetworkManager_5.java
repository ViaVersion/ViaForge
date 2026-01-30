/*
 * This file is part of ViaForge - https://github.com/FlorianMichael/ViaForge
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <git@florianmichael.de> and contributors
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

import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.extended.ExtendedNetworkManager;
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
    private void hookViaPipeline(Channel channel, CallbackInfo ci) {
        ViaForgeCommon.getManager().inject(channel, (ExtendedNetworkManager) val$networkmanager);
    }
    
}
