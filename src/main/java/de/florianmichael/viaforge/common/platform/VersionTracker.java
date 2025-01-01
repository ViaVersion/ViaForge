/*
 * This file is part of ViaForge - https://github.com/FlorianMichael/ViaForge
 * Copyright (C) 2021-2025 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and contributors
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

package de.florianmichael.viaforge.common.platform;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaforge.common.ViaForgeCommon;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Dirty, but needed to store the server specific version until building the netty pipeline.
 */
public class VersionTracker {

    public static final Map<InetAddress, ProtocolVersion> SERVER_PROTOCOL_VERSIONS = new HashMap<>();

    public static void storeServerProtocolVersion(InetAddress address, ProtocolVersion version) {
        SERVER_PROTOCOL_VERSIONS.put(address, version);
        ViaForgeCommon.getManager().setTargetVersionSilent(version);
    }

    public static ProtocolVersion getServerProtocolVersion(InetAddress address) {
        return SERVER_PROTOCOL_VERSIONS.remove(address);
    }

}
