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

package de.florianmichael.viaforge.common.protocoltranslator.platform;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.platform.UserConnectionViaVersionPlatform;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.platform.ViaForgePlatform;
import de.florianmichael.viaforge.common.platform.ViaForgeProtocolBase;
import de.florianmichael.viaforge.common.protocoltranslator.util.JLoggerToSLF4J;
import java.io.File;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

public final class ViaForgeViaVersionPlatform extends UserConnectionViaVersionPlatform {

    public ViaForgeViaVersionPlatform(final File rootFolder) {
        super(rootFolder);
    }

    @Override
    public Logger createLogger(final String name) {
        return new JLoggerToSLF4J(LoggerFactory.getLogger(name));
    }

    @Override
    public String getPlatformName() {
        return "ViaForge";
    }

    @Override
    public String getPlatformVersion() {
        return ViaForgePlatform.VERSION;
    }

    @Override
    public void sendCustomPayload(UserConnection connection, String channel, byte[] message) {
        final ViaForgeProtocolBase<?, ?, ?, ?> protocol = ViaForgeCommon.getManager().getPlatform().getCustomProtocol();
        final PacketWrapper customPayload = PacketWrapper.create(protocol.getCustomPayloadPacketType(), connection);
        customPayload.write(Types.STRING, channel);
        customPayload.write(Types.REMAINING_BYTES, message);
        customPayload.scheduleSendToServer(protocol.getClass());
    }

    @Override
    public void sendCustomPayloadToClient(final UserConnection connection, final String channel, final byte[] message) {
        final ViaForgeProtocolBase<?, ?, ?, ?> protocol = ViaForgeCommon.getManager().getPlatform().getCustomProtocol();
        final PacketWrapper customPayload = PacketWrapper.create(protocol.getClientboundCustomPayloadPacketType(), connection);
        customPayload.write(Types.STRING, channel);
        customPayload.write(Types.REMAINING_BYTES, message);
        customPayload.scheduleSend(protocol.getClass());
    }

    @Override
    public JsonObject getDump() {
        final JsonObject platformDump = new JsonObject();
        platformDump.addProperty("native_version", ViaForgeCommon.getManager().getNativeVersion().toString());
        platformDump.addProperty("target_version", ViaForgeCommon.getManager().getTargetVersion().toString());
        return platformDump;
    }

}
