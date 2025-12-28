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

import com.viaversion.vialoader.impl.platform.ViaVersionPlatformImpl;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.platform.VFPlatform;
import java.io.File;

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
    public JsonObject getDump() {
        final JsonObject platformDump = new JsonObject();
        platformDump.addProperty("native_version", ViaForgeCommon.getManager().getNativeVersion().toString());
        platformDump.addProperty("target_version", ViaForgeCommon.getManager().getTargetVersion().toString());
        return platformDump;
    }

}
