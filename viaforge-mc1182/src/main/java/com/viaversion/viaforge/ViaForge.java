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

package com.viaversion.viaforge;

import com.viaversion.viaforge.common.ViaForgeCommon;
import com.viaversion.viaforge.common.platform.ViaForgePlatform;
import com.viaversion.viaforge.common.platform.ViaForgeProtocolBase;
import com.viaversion.viaforge.platform.ViaForgeGameProfileFetcher;
import com.viaversion.viaforge.platform.ViaForgeProtocol;
import java.io.File;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.provider.GameProfileFetcher;

@Mod("viaforge")
public class ViaForge implements ViaForgePlatform {

    public ViaForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInit);
    }

    private void onInit(FMLCommonSetupEvent event) {
        ViaForgeCommon.init(this);
    }

    @Override
    public int getGameVersion() {
        return SharedConstants.getProtocolVersion();
    }

    @Override
    public boolean isSingleplayer() {
        return Minecraft.getInstance().hasSingleplayerServer();
    }

    @Override
    public File getLeadingDirectory() {
        return Minecraft.getInstance().gameDirectory;
    }

    @Override
    public void joinServer(String serverId) throws Throwable {
        final User session = Minecraft.getInstance().getUser();

        Minecraft.getInstance().getMinecraftSessionService().joinServer(session.getGameProfile(), session.getAccessToken(), serverId);
    }

    @Override
    public GameProfileFetcher getGameProfileFetcher() {
        return new ViaForgeGameProfileFetcher();
    }

    @Override
    public String getDecodeHandlerName() {
        return "decoder";
    }

    @Override
    public ViaForgeProtocolBase<?, ?, ?, ?> getCustomProtocol() {
        return ViaForgeProtocol.INSTANCE;
    }

}
