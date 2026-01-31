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

package de.florianmichael.viaforge.gui;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public final class VFDebugScreenEntry implements DebugScreenEntry {

    public static final Identifier ID = Identifier.tryBuild("viaforge", "viaforge");

    @Override
    public void display(final DebugScreenDisplayer p_427172_, @Nullable final Level p_427695_, @Nullable final LevelChunk p_423462_, @Nullable final LevelChunk p_426762_) {
        final ViaForgeCommon common = ViaForgeCommon.getManager();
        final ProtocolVersion version = ViaForgeCommon.getManager().getTargetVersion();

        if (common.getConfig().isShowProtocolVersionInF3() && version != common.getNativeVersion() && !common.getPlatform().isSingleplayer()) {
            p_427172_.addLine("ViaForge: " + version.toString());
        }
    }

    @Override
    public boolean isAllowed(final boolean p_424604_) {
        return true;
    }

}
