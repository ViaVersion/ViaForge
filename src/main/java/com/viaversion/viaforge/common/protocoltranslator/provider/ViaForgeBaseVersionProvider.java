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
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocol.version.BaseVersionProvider;
import com.viaversion.viaforge.common.ViaForgeCommon;

public class ViaForgeBaseVersionProvider extends BaseVersionProvider {

    @Override
    public ProtocolVersion getClosestServerProtocol(UserConnection connection) throws Exception {
        if (connection.isClientSide() && !ViaForgeCommon.getManager().getPlatform().isSingleplayer()) {
            return connection.getChannel().attr(ViaForgeCommon.VF_NETWORK_MANAGER).get().viaForge$getTrackedVersion();
        } else {
            return super.getClosestServerProtocol(connection);
        }
    }

}
