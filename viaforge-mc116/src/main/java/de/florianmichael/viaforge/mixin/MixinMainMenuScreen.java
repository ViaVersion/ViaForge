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
package de.florianmichael.viaforge.mixin;

import de.florianmichael.viaforge.ViaForge116;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.gui.GuiProtocolSelector;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MainMenuScreen.class)
public class MixinMainMenuScreen extends Screen {

    public MixinMainMenuScreen(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void hookViaForgeButton(CallbackInfo ci) {
        addButton(new Button(5, 6, 98, 20, new StringTextComponent("ViaForge"), b -> GuiProtocolSelector.open(minecraft)));

        ViaForgeCommon.init(ViaForge116.PLATFORM);
    }
}
