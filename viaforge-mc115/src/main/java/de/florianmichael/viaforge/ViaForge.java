/*
 * This file is part of ViaForge - https://github.com/FlorianMichael/ViaForge
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viaforge;

import de.florianmichael.viaforge.protocolhack.ViaForgeVLInjector;
import de.florianmichael.viaforge.protocolhack.ViaForgeVLLoader;
import net.minecraftforge.fml.common.Mod;
import net.raphimc.vialoader.ViaLoader;
import net.raphimc.vialoader.impl.platform.ViaBackwardsPlatformImpl;
import net.raphimc.vialoader.impl.platform.ViaRewindPlatformImpl;
import net.raphimc.vialoader.util.VersionEnum;

@Mod("viaforge")
public class ViaForge {
    public final static VersionEnum NATIVE_VERSION = VersionEnum.r1_15_2;

    public static VersionEnum targetVersion = VersionEnum.r1_15_2;

    private static boolean loaded;

    public static void initViaVersion() {
        if (loaded) return;

        ViaLoader.init(null, new ViaForgeVLLoader(), new ViaForgeVLInjector(), null, ViaBackwardsPlatformImpl::new, ViaRewindPlatformImpl::new);
        loaded = true;
    }
}
