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
import de.florianmichael.viaforge.common.protocolhack.netty.VFNetworkManager;
import io.netty.channel.Channel;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.raphimc.vialoader.netty.VLLegacyPipeline;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.crypto.Cipher;
import java.net.InetSocketAddress;

@Mixin(Connection.class)
public class MixinConnection implements VFNetworkManager {

    @Shadow private Channel channel;

    @Unique
    private Cipher viaforge_decryptionCipher;

    @Unique
    private VersionEnum viaforge_targetVersion;

    @Inject(method = "connectToServer", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;group(Lio/netty/channel/EventLoopGroup;)Lio/netty/bootstrap/AbstractBootstrap;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void trackSelfTarget(InetSocketAddress p_178301_, boolean p_178302_, CallbackInfoReturnable<Connection> cir, final Connection connection) {
        // The connecting screen and server pinger are setting the main target version when a specific version for a server is set,
        // This works for joining perfect since we can simply restore the version when the server doesn't have a specific one set,
        // but for the server pinger we need to store the target version and force the pinging to use the target version.
        // Due to the fact that the server pinger is being called multiple times.
        ((VFNetworkManager) connection).viaforge_setTrackedVersion(ViaForgeCommon.getManager().getTargetVersion());
    }

    @Inject(method = "setEncryptionKey", at = @At("HEAD"), cancellable = true)
    private void storeEncryptionCiphers(Cipher p_244777_1_, Cipher p_244777_2_, CallbackInfo ci) {
        if (ViaForgeCommon.getManager().getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
            // Minecraft's encryption code is bad for us, we need to reorder the pipeline
            ci.cancel();

            // Minecraft 1.6.4 supports tile encryption which means the server can only disable one side of the encryption
            // So we only enable the encryption side and later enable the decryption side if the 1.7 -> 1.6 protocol
            // tells us to do, therefore we need to store the cipher instance.
            this.viaforge_decryptionCipher = p_244777_1_;

            // Enabling the encryption side
            this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "encrypt", new CipherEncoder(p_244777_2_));
        }
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    public void restoreTargetVersion(Component p_129508_, CallbackInfo ci) {
        // If the previous server forced a version, we need to restore the version to the default one.
        ViaForgeCommon.getManager().restoreVersion();
    }

    @Inject(method = "setupCompression", at = @At("RETURN"))
    public void reorderPipeline(int p_129485_, boolean p_182682_, CallbackInfo ci) {
        ViaForgeCommon.getManager().reorderCompression(channel);
    }

    @Override
    public void viaforge_setupPreNettyDecryption() {
        // Enabling the decryption side for 1.6.4 if the 1.7 -> 1.6 protocol tells us to do
        this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "decrypt", new CipherDecoder(this.viaforge_decryptionCipher));
    }

    @Override
    public VersionEnum viaforge_getTrackedVersion() {
        return viaforge_targetVersion;
    }

    @Override
    public void viaforge_setTrackedVersion(VersionEnum version) {
        viaforge_targetVersion = version;
    }
}
