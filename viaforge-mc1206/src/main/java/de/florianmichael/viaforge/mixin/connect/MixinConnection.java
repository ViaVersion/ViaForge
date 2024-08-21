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

package de.florianmichael.viaforge.mixin.connect;

import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.platform.VersionTracker;
import de.florianmichael.viaforge.common.protocoltranslator.netty.VFNetworkManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.Connection;
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

import javax.crypto.Cipher;
import java.net.InetSocketAddress;

@Mixin(Connection.class)
public class MixinConnection implements VFNetworkManager {

    @Shadow private Channel channel;

    @Shadow private boolean encrypted;
    @Unique
    private Cipher viaForge$decryptionCipher;

    @Unique
    private ProtocolVersion viaForge$targetVersion;

    @Inject(method = "setupCompression", at = @At("RETURN"))
    public void reorderPipeline(int p_129485_, boolean p_182682_, CallbackInfo ci) {
        ViaForgeCommon.getManager().reorderCompression(channel);
    }

    @Inject(method = "setEncryptionKey", at = @At("HEAD"), cancellable = true)
    private void storeEncryptionCiphers(Cipher p_244777_1_, Cipher p_244777_2_, CallbackInfo ci) {
        if (viaForge$targetVersion != null && viaForge$targetVersion.olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            // Minecraft's encryption code is bad for us, we need to reorder the pipeline
            ci.cancel();

            // Minecraft 1.6.4 supports tile encryption which means the server can only enable one side of the encryption
            // So we only enable the encryption side and later enable the decryption side if the 1.7 -> 1.6 protocol
            // tells us to do, therefore we need to store the cipher instance.
            this.viaForge$decryptionCipher = p_244777_1_;

            // Enabling the encryption side
            this.encrypted = true;
            this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "encrypt", new CipherEncoder(p_244777_2_));
        }
    }

    @Inject(method = "connect", at = @At("HEAD"))
    private static void setTargetVersion(InetSocketAddress p_290034_, boolean p_290035_, Connection p_290031_, CallbackInfoReturnable<ChannelFuture> cir) {
        final VFNetworkManager mixinConnection = (VFNetworkManager) p_290031_;
        mixinConnection.viaForge$setTrackedVersion(VersionTracker.getServerProtocolVersion(p_290034_.getAddress()));
    }

    @Override
    public void viaForge$setupPreNettyDecryption() {
        // Enabling the decryption side for 1.6.4 if the 1.7 -> 1.6 protocol tells us to do
        this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "decrypt", new CipherDecoder(this.viaForge$decryptionCipher));
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
