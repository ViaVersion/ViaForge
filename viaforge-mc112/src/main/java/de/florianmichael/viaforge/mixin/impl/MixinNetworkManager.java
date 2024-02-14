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

package de.florianmichael.viaforge.mixin.impl;

import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.protocolhack.netty.VFNetworkManager;
import io.netty.channel.Channel;
import net.minecraft.network.NettyEncryptingDecoder;
import net.minecraft.network.NettyEncryptingEncoder;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.CryptManager;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.text.ITextComponent;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialoader.netty.VLLegacyPipeline;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.net.InetAddress;

@Mixin(NetworkManager.class)
public class MixinNetworkManager implements VFNetworkManager {

    @Shadow private Channel channel;

    @Unique
    private Cipher viaForge$decryptionCipher;

    @Unique
    private ProtocolVersion viaForge$targetVersion;

    @Inject(method = "createNetworkManagerAndConnect", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;group(Lio/netty/channel/EventLoopGroup;)Lio/netty/bootstrap/AbstractBootstrap;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void trackSelfTarget(InetAddress address, int serverPort, boolean useNativeTransport, CallbackInfoReturnable<NetworkManager> cir, NetworkManager networkmanager, Class oclass, LazyLoadBase lazyloadbase) {
        // The connecting screen and server pinger are setting the main target version when a specific version for a server is set,
        // This works for joining perfect since we can simply restore the version when the server doesn't have a specific one set,
        // but for the server pinger we need to store the target version and force the pinging to use the target version.
        // Due to the fact that the server pinger is being called multiple times.
        ((VFNetworkManager) networkmanager).viaForge$setTrackedVersion(ViaForgeCommon.getManager().getTargetVersion());
    }

    @Inject(method = "enableEncryption", at = @At("HEAD"), cancellable = true)
    private void storeEncryptionCiphers(SecretKey key, CallbackInfo ci) {
        if (ViaForgeCommon.getManager().getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            // Minecraft's encryption code is bad for us, we need to reorder the pipeline
            ci.cancel();

            // Minecraft 1.6.4 supports tile encryption which means the server can only disable one side of the encryption
            // So we only enable the encryption side and later enable the decryption side if the 1.7 -> 1.6 protocol
            // tells us to do, therefore we need to store the cipher instance.
            this.viaForge$decryptionCipher = CryptManager.createNetCipherInstance(2, key);

            // Enabling the encryption side
            this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
        }
    }

    @Inject(method = "closeChannel", at = @At("HEAD"))
    public void restoreTargetVersion(ITextComponent message, CallbackInfo ci) {
        // If the previous server forced a version, we need to restore the version to the default one.
        ViaForgeCommon.getManager().restoreVersion();
    }

    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    public void reorderPipeline(int p_setCompressionTreshold_1_, CallbackInfo ci) {
        ViaForgeCommon.getManager().reorderCompression(channel);
    }

    @Override
    public void viaForge$setupPreNettyDecryption() {
        // Enabling the decryption side for 1.6.4 if the 1.7 -> 1.6 protocol tells us to do
        this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "decrypt", new NettyEncryptingDecoder(this.viaForge$decryptionCipher));
    }

    @Override
    public ProtocolVersion viaForge$getTrackedVersion() {
        return viaForge$targetVersion;
    }

    @Override
    public void viaForge$setTrackedVersion(ProtocolVersion version) {
        viaForge$targetVersion = version;
    }

}
