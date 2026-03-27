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

package com.viaversion.viaforge.platform;

import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8;
import com.viaversion.viaforge.common.platform.ViaForgeProtocolBase;

public final class ViaForgeProtocol extends ViaForgeProtocolBase<ClientboundPackets1_8, ClientboundPackets1_8, ServerboundPackets1_8, ServerboundPackets1_8> {

    public static final ViaForgeProtocol INSTANCE = new ViaForgeProtocol();

    public ViaForgeProtocol() {
        super(ClientboundPackets1_8.class, ClientboundPackets1_8.class, ServerboundPackets1_8.class, ServerboundPackets1_8.class);
    }

}
