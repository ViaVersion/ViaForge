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

package de.florianmichael.viaforge;

import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.platform.VFPlatform;
import de.florianmichael.viaforge.provider.ViaForgeGameProfileFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;

import java.io.File;
import java.util.function.Supplier;

@Mod("viaforge")
public class ViaForge116 implements VFPlatform {

    public ViaForge116() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInit);
    }

    public void onInit(FMLCommonSetupEvent event) {
        ViaForgeCommon.init(this);
    }

    @Override
    public int getGameVersion() {
        return SharedConstants.getProtocolVersion();
    }

    @Override
    public Supplier<Boolean> isSingleplayer() {
        return () -> Minecraft.getInstance().hasSingleplayerServer();
    }

    @Override
    public File getLeadingDirectory() {
        return Minecraft.getInstance().gameDirectory;
    }

    @Override
    public void joinServer(String serverId) throws Throwable {
        final Session session = Minecraft.getInstance().getUser();

        Minecraft.getInstance().getMinecraftSessionService().joinServer(session.getGameProfile(), session.getAccessToken(), serverId);
    }

    @Override
    public GameProfileFetcher getGameProfileFetcher() {
        return new ViaForgeGameProfileFetcher();
    }

}
