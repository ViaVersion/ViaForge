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

package com.viaversion.viaforge.mixin.impl.javadowngrader;

import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.types.ChunkType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.ByteArrayOutputStream;

@Mixin(value = ChunkType.class, remap = false)
public class MixinChunkType {

    @Redirect(method = "serialize", at = @At(value = "INVOKE", target = "Ljava/io/ByteArrayOutputStream;writeBytes([B)V"))
    private static void serialize(ByteArrayOutputStream instance, byte[] b) {
        instance.write(b, 0, b.length);
    }

}
