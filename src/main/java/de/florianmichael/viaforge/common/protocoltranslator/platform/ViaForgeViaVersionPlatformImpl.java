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

package de.florianmichael.viaforge.common.protocoltranslator.platform;

import com.viaversion.vialoader.impl.platform.ViaVersionPlatformImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.base.InitialBaseProtocol;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.platform.VFPlatform;
import java.io.File;
import java.util.UUID;

public final class ViaForgeViaVersionPlatformImpl extends ViaVersionPlatformImpl {

    public ViaForgeViaVersionPlatformImpl(final File rootFolder) {
        super(rootFolder);
    }

    @Override
    public String getPlatformName() {
        return "ViaForge";
    }

    @Override
    public String getPlatformVersion() {
        return VFPlatform.VERSION;
    }

    @Override
    public void sendCustomPayload(final UUID uuid, final String channel, final String message) {
        final ServerboundPacketType packet = ViaForgeCommon.getManager().getPlatform().getCustomPayloadPacketType();
        final UserConnection connection = Via.getManager().getConnectionManager().getConnections().stream().findFirst().orElse(null);

        final PacketWrapper payload = PacketWrapper.create(packet, connection);
        payload.write(Types.STRING, channel);
        payload.write(Types.REMAINING_BYTES, message.getBytes());
        payload.sendToServer(InitialBaseProtocol.class);
    }

    @Override
    public JsonObject getDump() {
        final JsonObject platformDump = new JsonObject();
        platformDump.addProperty("native_version", ViaForgeCommon.getManager().getNativeVersion().toString());
        platformDump.addProperty("target_version", ViaForgeCommon.getManager().getTargetVersion().toString());
        return platformDump;
    }

}
