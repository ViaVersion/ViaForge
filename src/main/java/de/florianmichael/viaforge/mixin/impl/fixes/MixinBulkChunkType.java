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

package de.florianmichael.viaforge.mixin.impl.fixes;

import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.types.BulkChunkType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.ByteArrayOutputStream;

@Mixin(value = BulkChunkType.class, remap = false)
public class MixinBulkChunkType {

    @Redirect(
        method = "write(Lio/netty/buffer/ByteBuf;[Lcom/viaversion/viaversion/api/minecraft/chunks/Chunk;)V",
        at = @At(value = "INVOKE", target = "Ljava/io/ByteArrayOutputStream;writeBytes([B)V")
    )
    public void write(ByteArrayOutputStream instance, byte[] b) {
        instance.write(b, 0, b.length);
    }
}
