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

package de.florianmichael.viaforge.common.protocolhack;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.florianmichael.viaforge.common.platform.VFPlatform;
import de.florianmichael.viaforge.common.protocolhack.provider.*;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.providers.EncryptionProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;
import net.raphimc.vialoader.impl.viaversion.VLLoader;

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
