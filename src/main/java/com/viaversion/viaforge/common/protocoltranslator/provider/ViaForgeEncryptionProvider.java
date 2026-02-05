/*
 * This file is part of ViaForge - https://github.com/ViaVersion/ViaForge
 * Copyright (C) 2021-2026 Florian Reuth <git@florianreuth.de> and contributors
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

package com.viaversion.viaforge.common.protocoltranslator.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaforge.common.ViaForgeCommon;
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.provider.EncryptionProvider;

public class ViaForgeEncryptionProvider extends EncryptionProvider {

    @Override
    public void enableDecryption(UserConnection user) {
        user.getChannel().attr(ViaForgeCommon.VF_NETWORK_MANAGER).getAndRemove().viaForge$setupPreNettyDecryption();
    }

}
