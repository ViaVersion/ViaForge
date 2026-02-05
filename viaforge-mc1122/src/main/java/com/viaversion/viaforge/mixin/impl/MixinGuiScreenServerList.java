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

package com.viaversion.viaforge.mixin.impl;

import com.viaversion.viaversion.util.Pair;
import com.viaversion.viaforge.common.ViaForgeCommon;
import com.viaversion.viaforge.common.platform.ViaForgeConfig;
import com.viaversion.viaforge.gui.GuiProtocolSelector;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenServerList.class)
public class MixinGuiScreenServerList extends GuiScreen {

    @Inject(method = "initGui", at = @At("RETURN"))
    public void hookViaForgeButton(CallbackInfo ci) {
        final ViaForgeConfig config = ViaForgeCommon.getManager().getConfig();
        if (config.isShowDirectConnectButton()) {
            final Pair<Integer, Integer> pos = config.getViaForgeButtonPosition().getPosition(this.width, this.height);

            buttonList.add(new GuiButton(1_000_000_000, pos.key(), pos.value(), 100, 20, "ViaForge"));
        }
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void handleViaForgeButtonClicking(GuiButton p_actionPerformed_1_, CallbackInfo ci) {
        if (ViaForgeCommon.getManager().getConfig().isShowDirectConnectButton()) {
            if (p_actionPerformed_1_.id == 1_000_000_000) {
                mc.displayGuiScreen(new GuiProtocolSelector(this));
            }
        }
    }

}
