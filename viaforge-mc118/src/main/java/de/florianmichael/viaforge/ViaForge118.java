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

import de.florianmichael.viaforge.common.PlatformFields;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod("viaforge")
public class ViaForge118 implements PlatformFields {

    public final static ViaForge118 PLATFORM = new ViaForge118();

    @Override
    public int getGameVersion() {
        return SharedConstants.getProtocolVersion();
    }

    @Override
    public Supplier<Boolean> isSingleplayer() {
        return () -> Minecraft.getInstance().hasSingleplayerServer();
    }
}
