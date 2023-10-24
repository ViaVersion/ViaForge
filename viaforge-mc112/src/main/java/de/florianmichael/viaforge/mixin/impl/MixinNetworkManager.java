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
import de.florianmichael.viaforge.common.protocolhack.netty.IEncryptionSetup;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NettyEncryptingDecoder;
import net.minecraft.network.NettyEncryptingEncoder;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.CryptManager;
import net.raphimc.vialoader.netty.VLLegacyPipeline;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

@Mixin(NetworkManager.class)
public class MixinNetworkManager implements IEncryptionSetup {

    @Shadow private Channel channel;

    @Unique
    private Cipher viaforge_decryptionCipher;

    @Inject(method = "channelActive", at = @At("RETURN"))
    public void trackThisClass(ChannelHandlerContext p_channelActive_1_, CallbackInfo ci) {
        // We need to access this class later to call the viaforge_setupPreNettyDecryption method.
        // In one of the ViaLegacy's required providers, so we track this class instance as an own
        // attribute in the connection and later access it from there and remove it.
        // Counterpart in {@link java/de/florianmichael/viaforge/common/protocolhack/provider/ViaForgeEncryptionProvider.java}
        channel.attr(ViaForgeCommon.ENCRYPTION_SETUP).set(this);
    }

    @Inject(method = "enableEncryption", at = @At("HEAD"), cancellable = true)
    private void storeEncryptionCiphers(SecretKey key, CallbackInfo ci) {
        if (ViaForgeCommon.getManager().getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
            // Minecraft's encryption code is bad for us, we need to reorder the pipeline
            ci.cancel();

            // Minecraft 1.6.4 supports tile encryption which means the server can only disable one side of the encryption
            // So we only enable the encryption side and later enable the decryption side if the 1.7 -> 1.6 protocol
            // tells us to do, therefore we need to store the cipher instance.
            this.viaforge_decryptionCipher = CryptManager.createNetCipherInstance(2, key);

            // Enabling the encryption side
            this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
        }
    }

    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    public void reorderPipeline(int p_setCompressionTreshold_1_, CallbackInfo ci) {
        // When Minecraft enables compression, we need to reorder the pipeline
        // to match the counterparts of via-decoder <-> encoder and via-encoder <-> encoder
        ViaForgeCommon.getManager().reorderCompression(channel);
    }

    @Override
    public void viaforge_setupPreNettyDecryption() {
        // Enabling the decryption side for 1.6.4 if the 1.7 -> 1.6 protocol tells us to do
        this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "decrypt", new NettyEncryptingDecoder(this.viaforge_decryptionCipher));
    }
}
