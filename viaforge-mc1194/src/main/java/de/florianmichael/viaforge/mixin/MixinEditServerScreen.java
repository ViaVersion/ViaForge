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

import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.gui.ExtendedServerData;
import de.florianmichael.viaforge.common.platform.ViaForgeConfig;
import de.florianmichael.viaforge.gui.GuiProtocolSelector;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditServerScreen.class)
public class MixinEditServerScreen extends Screen {

    @Shadow
    @Final
    private ServerData serverData;

    public MixinEditServerScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void initGui(CallbackInfo ci) {
        final ViaForgeConfig config = ViaForgeCommon.getManager().getConfig();

        if (config.isShowAddServerButton()) {
            final Pair<Integer, Integer> pos = config.getAddServerScreenButtonPosition().getPosition(this.width, this.height);

            final ProtocolVersion target = ((ExtendedServerData) serverData).viaForge$getVersion();
            addRenderableWidget(Button.builder(Component.literal(target != null ? target.getName() : "Set Version"), b -> {
                minecraft.setScreen(new GuiProtocolSelector(this, true, (version, parent) -> {
                    // Set version and go back to the parent screen.
                    ((ExtendedServerData) serverData).viaForge$setVersion(version);
                    minecraft.setScreen(parent);
                }));
            }).bounds(pos.key(), pos.value(), 100, 20).build());
        }
    }

}
