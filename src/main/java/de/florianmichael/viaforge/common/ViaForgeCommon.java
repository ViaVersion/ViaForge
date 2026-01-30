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

package de.florianmichael.viaforge.common;

import com.viaversion.viaaprilfools.ViaAprilFoolsPlatformImpl;
import com.viaversion.viabackwards.ViaBackwardsPlatformImpl;
import com.viaversion.viarewind.ViaRewindPlatformImpl;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.connection.ConnectionDetails;
import com.viaversion.viaversion.platform.NoopInjector;
import com.viaversion.viaversion.platform.ViaChannelInitializer;
import com.viaversion.viaversion.platform.ViaDecodeHandler;
import com.viaversion.viaversion.platform.ViaEncodeHandler;
import de.florianmichael.viaforge.common.platform.ViaForgePlatform;
import de.florianmichael.viaforge.common.platform.ViaForgeConfig;
import de.florianmichael.viaforge.common.protocoltranslator.platform.ViaForgePlatformLoader;
import de.florianmichael.viaforge.common.extended.ExtendedNetworkManager;
import de.florianmichael.viaforge.common.protocoltranslator.platform.ViaForgeViaVersionPlatform;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;
import java.io.File;
import net.raphimc.vialegacy.ViaLegacyPlatformImpl;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.netty.PreNettyLengthPrepender;
import net.raphimc.vialegacy.netty.PreNettyLengthRemover;

/**
 * This class is used to manage the common code between the different ViaForge versions.
 * It is used to inject the ViaVersion pipeline into the netty pipeline. It also manages the target version.
 */
public class ViaForgeCommon {

    public static final AttributeKey<UserConnection> VF_VIA_USER = AttributeKey.valueOf("viaforge_via_user");
    public static final AttributeKey<ExtendedNetworkManager> VF_NETWORK_MANAGER = AttributeKey.valueOf("viaforge_network_manager");

    private static ViaForgeCommon manager;

    private final ViaForgePlatform platform;
    private ProtocolVersion targetVersion;
    private ProtocolVersion previousVersion;

    private ViaForgeConfig config;

    public ViaForgeCommon(ViaForgePlatform platform) {
        this.platform = platform;
    }

    /**
     * Initializes the manager.
     *
     * @param platform the platform fields
     */
    public static void init(final ViaForgePlatform platform) {
        final ProtocolVersion version = ProtocolVersion.getProtocol(platform.getGameVersion()); // ViaForge will only load on post-netty versions
        if (version == ProtocolVersion.unknown) {
            throw new IllegalArgumentException("Unknown version " + platform.getGameVersion());
        }

        manager = new ViaForgeCommon(platform);

        final File mainFolder = new File(platform.getLeadingDirectory(), "ViaForge");

        ViaManagerImpl.initAndLoad(
            new ViaForgeViaVersionPlatform(mainFolder),
            new NoopInjector(),
            new ViaCommandHandler(false),
            new ViaForgePlatformLoader(platform),
            () -> {
                new ViaBackwardsPlatformImpl();
                new ViaRewindPlatformImpl();
                new ViaLegacyPlatformImpl();
                new ViaAprilFoolsPlatformImpl();
            }
        );
        manager.config = new ViaForgeConfig(new File(mainFolder, "viaforge.yml"), Via.getPlatform().getLogger());

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
    public void inject(final Channel channel, final ExtendedNetworkManager networkManager) {
        if (networkManager.viaForge$getTrackedVersion().equals(getNativeVersion())) {
            return; // Don't inject ViaVersion into pipeline if there is nothing to translate anyway
        }

        final UserConnection user = ViaChannelInitializer.createUserConnection(channel, true);

        channel.attr(VF_VIA_USER).set(user);
        channel.attr(VF_NETWORK_MANAGER).set(networkManager);

        final ChannelPipeline pipeline = channel.pipeline();

        // ViaVersion
        pipeline.addBefore(platform.getDecodeHandlerName(), ViaDecodeHandler.NAME, new ViaDecodeHandler(user));
        pipeline.addBefore("encoder", ViaEncodeHandler.NAME, new ViaEncodeHandler(user));

        if (networkManager.viaForge$getTrackedVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            // ViaLegacy
            pipeline.addBefore("splitter", PreNettyLengthPrepender.NAME, new PreNettyLengthPrepender(user));
            pipeline.addBefore("prepender", PreNettyLengthRemover.NAME, new PreNettyLengthRemover(user));
        }

        channel.closeFuture().addListener(future -> {
            if (previousVersion != null) {
                restoreVersion();
            }
        });
    }

    public void sendConnectionDetails(final Channel channel) {
        if (!config.isSendConnectionDetails() || !channel.hasAttr(VF_VIA_USER)) {
            return;
        }

        ConnectionDetails.sendConnectionDetails(channel.attr(VF_VIA_USER).get(), ConnectionDetails.MOD_CHANNEL);
    }

    /**
     * Reorders the compression channel.
     *
     * @param pipeline the pipeline to reorder the compression in
     */
    public void reorderCompression(final ChannelPipeline pipeline) {
        final int decoderIndex = pipeline.names().indexOf("decompress");
        if (decoderIndex == -1) {
            return;
        }

        if (decoderIndex > pipeline.names().indexOf(ViaDecodeHandler.NAME)) {
            final ChannelHandler decoderHandler = pipeline.get(ViaDecodeHandler.NAME);
            final ChannelHandler encoderHandler = pipeline.get(ViaEncodeHandler.NAME);

            pipeline.remove(decoderHandler);
            pipeline.remove(encoderHandler);

            pipeline.addAfter("decompress", ViaDecodeHandler.NAME, decoderHandler);
            pipeline.addAfter("compress", ViaEncodeHandler.NAME, encoderHandler);
        }
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
        if (targetVersion == null) {
            throw new IllegalArgumentException("Target version cannot be null");
        }
        final ProtocolVersion oldVersion = this.targetVersion;
        this.targetVersion = targetVersion;
        if (oldVersion != targetVersion) {
            previousVersion = oldVersion;
        }
    }

    public void setTargetVersion(final ProtocolVersion targetVersion) {
        if (targetVersion == null) {
            throw new IllegalArgumentException("Target version cannot be null");
        }
        this.targetVersion = targetVersion;
        config.setClientSideVersion(targetVersion.getName());
    }

    public ViaForgePlatform getPlatform() {
        return platform;
    }

    public ViaForgeConfig getConfig() {
        return config;
    }

    public static ViaForgeCommon getManager() {
        return manager;
    }

}
