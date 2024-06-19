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

package de.florianmichael.viaforge.common.protocoltranslator.provider;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.provider.OldAuthProvider;
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.storage.HandshakeStorage;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.Scanner;
import java.util.logging.Level;

public class ViaForgeClassicMPPassProvider extends ClassicMPPassProvider {

    @Override
    public String getMpPass(UserConnection user) {
        if (ViaForgeCommon.getManager().getConfig().isAllowBetacraftAuthentication()) {
            final HandshakeStorage handshakeStorage = user.get(HandshakeStorage.class);

            return getBetacraftMpPass(user, user.getProtocolInfo().getUsername(), handshakeStorage.getHostname(), handshakeStorage.getPort());
        }
        return super.getMpPass(user);
    }

    /*
    Stolen from https://github.com/FlorianMichael/Classic4J/blob/main/src/main/java/de/florianmichael/classic4j/BetaCraftHandler.java :tm:
     */

    private static String getBetacraftMpPass(final UserConnection user, final String username, final String ip, final int port) {
        try {
            final String server = InetAddress.getByName(ip).getHostAddress() + ":" + port;

            Via.getManager().getProviders().get(OldAuthProvider.class).sendAuthRequest(user, sha1(server.getBytes()));

            final InputStream connection = new URL("https://api.betacraft.uk/getmppass.jsp?user=" + username + "&server=" + server).openStream();
            Scanner scanner = new Scanner(connection);
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.next());
            }
            connection.close();

            if (response.toString().contains("FAILED") || response.toString().contains("SERVER NOT FOUND")) return "0";

            return response.toString();
        } catch (Throwable t) {
            Via.getPlatform().getLogger().log(Level.WARNING, "An unknown error occurred while authenticating with BetaCraft", t);
            return "0";
        }
    }

    private static String sha1(final byte[] input) {
        try {
            Formatter formatter = new Formatter();
            final byte[] hash = MessageDigest.getInstance("SHA-1").digest(input);
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        } catch (Exception e) {
            return null;
        }
    }

}
