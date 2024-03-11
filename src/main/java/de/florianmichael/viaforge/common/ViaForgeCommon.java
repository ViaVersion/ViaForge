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

package de.florianmichael.viaforge.common;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.viaforge.common.platform.VFPlatform;
import de.florianmichael.viaforge.common.platform.ViaForgeConfig;
import de.florianmichael.viaforge.common.protocoltranslator.ViaForgeVLInjector;
import de.florianmichael.viaforge.common.protocoltranslator.netty.VFNetworkManager;
import de.florianmichael.viaforge.common.protocoltranslator.netty.ViaForgeVLLegacyPipeline;
import de.florianmichael.viaforge.common.protocoltranslator.ViaForgeVLLoader;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import net.raphimc.vialoader.ViaLoader;
import net.raphimc.vialoader.impl.platform.*;
import net.raphimc.vialoader.netty.CompressionReorderEvent;

import java.io.File;

/**
 * This class is used to manage the common code between the different ViaForge versions.
 * It is used to inject the ViaVersion pipeline into the netty pipeline. It also manages the target version.
 */
public class ViaForgeCommon {

    public static final AttributeKey<UserConnection> LOCAL_VIA_USER = AttributeKey.valueOf("local_via_user");
    public static final AttributeKey<VFNetworkManager> VF_NETWORK_MANAGER = AttributeKey.valueOf("encryption_setup");

    private static ViaForgeCommon manager;

    private final VFPlatform platform;
    private ProtocolVersion targetVersion;

    private ViaForgeConfig config;

    public ViaForgeCommon(VFPlatform platform) {
        this.platform = platform;
    }

    /**
     * Initializes the manager.
     *
     * @param platform the platform fields
     */
    public static void init(final VFPlatform platform) {
        if (manager != null) {
            return; // Already initialized, ignore it then :tm:
        }
        final ProtocolVersion version = ProtocolVersion.getProtocol(platform.getGameVersion()); // ViaForge will only load on post-netty versions
        if (version == ProtocolVersion.unknown) {
            throw new IllegalArgumentException("Unknown version " + platform.getGameVersion());
        }

        manager = new ViaForgeCommon(platform);

        final File mainFolder = new File(platform.getLeadingDirectory(), "ViaForge");

        ViaLoader.init(new ViaVersionPlatformImpl(mainFolder), new ViaForgeVLLoader(platform), new ViaForgeVLInjector(), null, ViaBackwardsPlatformImpl::new, ViaRewindPlatformImpl::new, ViaLegacyPlatformImpl::new, ViaAprilFoolsPlatformImpl::new);
        manager.config = new ViaForgeConfig(new File(mainFolder, "viaforge.yml"));

        final ProtocolVersion configVersion = ProtocolVersion.getClosest(manager.config.getClientSideVersion());
        if (configVersion != null) {
            manager.setTargetVersion(configVersion);
        } else {
            manager.setTargetVersion(version);
        }
    }

    /**
     * Injects the ViaVersion pipeline into the netty pipeline.
     *
     * @param channel the channel to inject the pipeline into
     */
    public void inject(final Channel channel, final VFNetworkManager networkManager) {
        if (channel instanceof SocketChannel) {
            final UserConnection user = new UserConnectionImpl(channel, true);
            new ProtocolPipelineImpl(user);

            channel.attr(LOCAL_VIA_USER).set(user);
            channel.attr(VF_NETWORK_MANAGER).set(networkManager);

            channel.pipeline().addLast(new ViaForgeVLLegacyPipeline(user, targetVersion));
        }
    }

    /**
     * Reorders the compression channel.
     *
     * @param channel the channel to reorder the compression for
     */
    public void reorderCompression(final Channel channel) {
        // When Minecraft enables compression, we need to reorder the pipeline
        // to match the counterparts of via-decoder <-> encoder and via-encoder <-> encoder
        channel.pipeline().fireUserEventTriggered(CompressionReorderEvent.INSTANCE);
    }

    public ProtocolVersion getNativeVersion() {
        return ProtocolVersion.getProtocol(platform.getGameVersion());
    }

    public ProtocolVersion getTargetVersion() {
        return targetVersion;
    }

    public void restoreVersion() {
        this.targetVersion = ProtocolVersion.getClosest(config.getClientSideVersion());
    }

    public void setTargetVersionSilent(final ProtocolVersion targetVersion) {
        this.targetVersion = targetVersion;
    }

    public void setTargetVersion(final ProtocolVersion targetVersion) {
        this.targetVersion = targetVersion;
        config.setClientSideVersion(targetVersion.getName());
    }

    public VFPlatform getPlatform() {
        return platform;
    }

    public ViaForgeConfig getConfig() {
        return config;
    }

    public static ViaForgeCommon getManager() {
        return manager;
    }

}