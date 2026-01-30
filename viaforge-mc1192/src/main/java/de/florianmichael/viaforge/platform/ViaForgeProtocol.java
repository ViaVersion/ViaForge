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

package de.florianmichael.viaforge.platform;

import com.viaversion.viaversion.protocols.v1_19to1_19_1.packet.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.v1_19to1_19_1.packet.ServerboundPackets1_19_1;
import de.florianmichael.viaforge.common.platform.ViaForgeProtocolBase;

public final class ViaForgeProtocol extends ViaForgeProtocolBase<ClientboundPackets1_19_1, ClientboundPackets1_19_1, ServerboundPackets1_19_1, ServerboundPackets1_19_1> {

    public static final ViaForgeProtocol INSTANCE = new ViaForgeProtocol();

    public ViaForgeProtocol() {
        super(ClientboundPackets1_19_1.class, ClientboundPackets1_19_1.class, ServerboundPackets1_19_1.class, ServerboundPackets1_19_1.class);
    }

}
