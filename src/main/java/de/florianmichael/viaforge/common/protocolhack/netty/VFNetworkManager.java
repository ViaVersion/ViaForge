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

package de.florianmichael.viaforge.common.protocolhack.netty;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public interface VFNetworkManager {

    /**
     * API method to setup the decryption side of the pipeline.
     * This method is called by the {@link de.florianmichael.viaforge.common.protocolhack.provider.ViaForgeEncryptionProvider} class.
     */
    void viaForge$setupPreNettyDecryption();

    /**
     * @return the target version of the connection
     */
    ProtocolVersion viaForge$getTrackedVersion();

    /**
     * Sets the target version of the connection.
     *
     * @param version the target version
     */
    void viaForge$setTrackedVersion(final ProtocolVersion version);

}
