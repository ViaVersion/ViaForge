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

package de.florianmichael.viaforge.mixin;

import net.neoforged.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    private static boolean is21_9;

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("21_8")) {
            return !is21_9;
        } else if (mixinClassName.contains("21_9")) {
            return is21_9;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String name, ClassNode targetClass, String mixin, IMixinInfo iMixinInfo) {}

    @Override
    public void postApply(String name, ClassNode targetClass, String mixin, IMixinInfo iMixinInfo) {}

    static {
        String version;
        boolean lex = false;
        try {
            Class<?> neo = Class.forName("net.neoforged.fml.loading.FMLLoader");
        } catch (ClassNotFoundException ignored) {
            lex = true;
        }
        if (lex) {
            version = net.minecraftforge.fml.loading.FMLLoader.versionInfo().mcVersion();
            LogManager.getLogger("ViaForge").info("Detected: {}", version);
            String[] parts = version.split("\\.");
            if (parts.length > 2) {
                is21_9 = Integer.parseInt(parts[2]) > 8;
            }
        } else {
            Method current = null;
            try {
                current = FMLLoader.class.getDeclaredMethod("getCurrent");
            } catch (Exception ignored) {}
            if (current != null) {
                is21_9 = true;
            }
        }
    }
}
