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

package de.florianmichael.viaforge.mixin.impl.connect;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.protocoltranslator.netty.VFNetworkManager;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.NetworkManager;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.storage.ProtocolMetadataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("DataFlowIssue")
@Mixin(NetHandlerLoginClient.class)
public class MixinNetHandlerLoginClient {

    @Shadow @Final private NetworkManager networkManager;

    @Redirect(method = "handleEncryptionRequest", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/minecraft/MinecraftSessionService;joinServer(Lcom/mojang/authlib/GameProfile;Ljava/lang/String;Ljava/lang/String;)V", remap = false))
    public void onlyJoinServerIfPremium(MinecraftSessionService instance, GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        final VFNetworkManager mixinNetworkManager = (VFNetworkManager) networkManager;
        if (mixinNetworkManager.viaForge$getTrackedVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            final UserConnection user = networkManager.channel().attr(ViaForgeCommon.VF_VIA_USER).get();
            if (user != null && user.has(ProtocolMetadataStorage.class) && !user.get(ProtocolMetadataStorage.class).authenticate) {
                // We are in the 1.7 -> 1.6 protocol, so we need to skip the joinServer call
                // if the server is in offline mode, due the packet changes <-> networking changes
                // Minecraft's networking code is bad for us.
                return;
            }
        }
        instance.joinServer(profile, authenticationToken, serverId);
    }

}
