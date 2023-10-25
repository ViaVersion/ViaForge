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

import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.gui.ExtendedServerData;
import de.florianmichael.viaforge.common.platform.ViaForgeConfig;
import de.florianmichael.viaforge.gui.GuiProtocolSelector;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.ITextComponent;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public class MixinAddServerScreen extends Screen {

    @Shadow @Final private ServerData serverData;

    public MixinAddServerScreen(ITextComponent title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void initGui(CallbackInfo ci) {
        final ViaForgeConfig config = ViaForgeCommon.getManager().getConfig();

        if (config.isShowAddServerButton()) {
            final Pair<Integer, Integer> pos = config.getAddServerScreenButtonPosition().getPosition(this.width, this.height);

            final VersionEnum target = ((ExtendedServerData) serverData).viaforge_getVersion();
           addButton(new Button(pos.key(), pos.value(), 100, 20, target != null ? target.getName() : "Set Version", b -> {
               minecraft.setScreen(new GuiProtocolSelector(this, true, (version, parent) -> {
                   // Set version and go back to the parent screen.
                   ((ExtendedServerData) serverData).viaforge_setVersion(version);
                   minecraft.setScreen(parent);
               }));
           }));
        }
    }
}
