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
import de.florianmichael.viaforge.common.platform.VersionTracker;
import de.florianmichael.viaforge.common.protocoltranslator.platform.netty.VFNetworkManager;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import java.net.InetSocketAddress;
import javax.crypto.Cipher;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.Connection;
import net.minecraft.util.LazyLoadedValue;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.netty.PreNettyLengthRemover;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Connection.class)
public class MixinConnection implements VFNetworkManager {

    @Shadow
    private Channel channel;

    @Shadow
    private boolean encrypted;
    @Unique
    private Cipher viaForge$decryptionCipher;

    @Unique
    private ProtocolVersion viaForge$targetVersion;

    @Inject(method = "setupCompression", at = @At("RETURN"))
    public void reorderPipeline(int p_129485_, boolean p_182682_, CallbackInfo ci) {
        ViaForgeCommon.getManager().reorderCompression(channel.pipeline());
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
            this.channel.pipeline().addBefore(PreNettyLengthRemover.NAME, "encrypt", new CipherEncoder(p_244777_2_));
        }
    }

    @Inject(method = "connectToServer", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;group(Lio/netty/channel/EventLoopGroup;)Lio/netty/bootstrap/AbstractBootstrap;", remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void setTargetVersion(InetSocketAddress p_178301_, boolean p_178302_, CallbackInfoReturnable<Connection> cir, Connection connection, Class<? extends SocketChannel> oclass, LazyLoadedValue<? extends EventLoopGroup> lazyloadedvalue) {
        final VFNetworkManager mixinConnection = (VFNetworkManager) connection;
        mixinConnection.viaForge$setTrackedVersion(VersionTracker.getServerProtocolVersion(p_178301_.getAddress()));
    }

    @Override
    public void viaForge$setupPreNettyDecryption() {
        // Enabling the decryption side for 1.6.4 if the 1.7 -> 1.6 protocol tells us to do
        this.channel.pipeline().addBefore(PreNettyLengthRemover.NAME, "decrypt", new CipherDecoder(this.viaForge$decryptionCipher));
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
