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
package de.florianmichael.viaforge.common;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.viaforge.common.protocolhack.ViaForgeVLInjector;
import de.florianmichael.viaforge.common.protocolhack.ViaForgeVLLegacyPipeline;
import de.florianmichael.viaforge.common.protocolhack.ViaForgeVLLoader;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.raphimc.vialoader.ViaLoader;
import net.raphimc.vialoader.impl.platform.ViaBackwardsPlatformImpl;
import net.raphimc.vialoader.impl.platform.ViaRewindPlatformImpl;
import net.raphimc.vialoader.netty.CompressionReorderEvent;
import net.raphimc.vialoader.util.VersionEnum;

public class ViaForgeCommon {
    private static ViaForgeCommon manager;

    private final PlatformFields platform;
    private VersionEnum targetVersion;

    public ViaForgeCommon(PlatformFields platform) {
        this.platform = platform;
    }

    public static void init(final PlatformFields platform) {
        if (manager != null) {
            return; // Already initialized, ignore it then :tm:
        }
        final VersionEnum version = VersionEnum.fromProtocolId(platform.getGameVersion());
        if (version == VersionEnum.UNKNOWN) {
            throw new IllegalArgumentException("Unknown version " + platform.getGameVersion());
        }

        manager = new ViaForgeCommon(platform);
        manager.setTargetVersion(version);

        ViaLoader.init(null, new ViaForgeVLLoader(), new ViaForgeVLInjector(), null, ViaBackwardsPlatformImpl::new, ViaRewindPlatformImpl::new);
    }

    public void inject(final Channel channel) {
        if (channel instanceof SocketChannel && targetVersion != getNativeVersion()) {
            final UserConnection user = new UserConnectionImpl(channel, true);
            new ProtocolPipelineImpl(user);

            channel.pipeline().addLast(new ViaForgeVLLegacyPipeline(user, targetVersion));
        }
    }

    public void reorderCompression(final Channel channel) {
        channel.pipeline().fireUserEventTriggered(CompressionReorderEvent.INSTANCE);
    }

    public VersionEnum getNativeVersion() {
        return VersionEnum.fromProtocolId(platform.getGameVersion());
    }

    public VersionEnum getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(VersionEnum targetVersion) {
        this.targetVersion = targetVersion;
    }

    public PlatformFields getPlatform() {
        return platform;
    }

    public static ViaForgeCommon getManager() {
        return manager;
    }
}
