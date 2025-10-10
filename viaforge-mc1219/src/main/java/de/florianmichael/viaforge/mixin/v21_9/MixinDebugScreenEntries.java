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

package de.florianmichael.viaforge.mixin.v21_9;

import de.florianmichael.viaforge.gui.VFDebugScreenEntry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenEntries.class)
public abstract class MixinDebugScreenEntries {

    @Mutable
    @Shadow
    @Final
    public static Map<DebugScreenProfile, Map<ResourceLocation, DebugScreenEntryStatus>> PROFILES;

    @Shadow
    public static ResourceLocation register(final ResourceLocation p_430307_, final DebugScreenEntry p_424237_) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void addViaForgeEntry(CallbackInfo ci) {
        final ResourceLocation id = register(VFDebugScreenEntry.ID, new VFDebugScreenEntry());
        final Map<DebugScreenProfile, Map<ResourceLocation, DebugScreenEntryStatus>> profiles = new HashMap<>();
        for (Map.Entry<DebugScreenProfile, Map<ResourceLocation, DebugScreenEntryStatus>> entry : PROFILES.entrySet()) {
            final Map<ResourceLocation, DebugScreenEntryStatus> entries = new HashMap<>(entry.getValue());
            if (entry.getKey() == DebugScreenProfile.DEFAULT) {
                entries.put(id, DebugScreenEntryStatus.IN_F3);
            }
            profiles.put(entry.getKey(), entries);
        }
        PROFILES = profiles;
    }

}
