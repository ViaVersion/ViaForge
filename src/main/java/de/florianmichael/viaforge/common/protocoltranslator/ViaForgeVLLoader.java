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

package de.florianmichael.viaforge.common.protocoltranslator;

import com.viaversion.vialoader.impl.viaversion.VLLoader;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.MovementTransmitterProvider;
import de.florianmichael.viaforge.common.platform.VFPlatform;
import de.florianmichael.viaforge.common.protocoltranslator.provider.*;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.provider.OldAuthProvider;
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.provider.EncryptionProvider;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.provider.GameProfileFetcher;

public class ViaForgeVLLoader extends VLLoader {

    private final VFPlatform platform;

    public ViaForgeVLLoader(VFPlatform platform) {
        this.platform = platform;
    }

    @Override
    public void load() {
        super.load();

        final ViaProviders providers = Via.getManager().getProviders();

        providers.use(VersionProvider.class, new ViaForgeVersionProvider());
        providers.use(MovementTransmitterProvider.class, new ViaForgeMovementTransmitterProvider());
        providers.use(OldAuthProvider.class, new ViaForgeOldAuthProvider());
        providers.use(GameProfileFetcher.class, platform.getGameProfileFetcher());
        providers.use(EncryptionProvider.class, new ViaForgeEncryptionProvider());
        providers.use(ClassicMPPassProvider.class, new ViaForgeClassicMPPassProvider());
    }

}
