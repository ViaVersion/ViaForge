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
package de.florianmichael.viaforge.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.raphimc.vialoader.util.VersionEnum;
import org.lwjgl.opengl.GL11;

public class GuiProtocolSelector extends Screen {

    private final Screen parent;

    public static void open(final Minecraft minecraft) { // Bypass for some weird bytecode instructions errors in Forge
        minecraft.setScreen(new GuiProtocolSelector(minecraft.screen));
    }

    private SlotList slotList;

    public GuiProtocolSelector(Screen parent) {
        super(new TextComponent("ViaForge Protocol Selector"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        addWidget(slotList = new SlotList(minecraft, width, height, 32, height - 32, 10));
        addRenderableWidget(new Button(width / 2 - 100, height - 27, 200, 20, new TextComponent("Back"), b -> minecraft.setScreen(parent)));
    }

    @Override
    public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        renderBackground(p_230430_1_);
        this.slotList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(p_230430_1_, this.font, new TextComponent(ChatFormatting.GOLD + "ViaForge"), this.width / 4, 6, 16777215);
        GL11.glPopMatrix();

        drawString(p_230430_1_, this.font, "by https://github.com/ViaVersion/ViaForge", 1, 1, -1);
        drawString(p_230430_1_, this.font, "Discord: florianmichael", 1, 11, -1);
    }

    static class SlotList extends ObjectSelectionList<SlotList.SlotEntry> {

        public SlotList(Minecraft p_i51146_1_, int p_i51146_2_, int p_i51146_3_, int p_i51146_4_, int p_i51146_5_, int p_i51146_6_) {
            super(p_i51146_1_, p_i51146_2_, p_i51146_3_, p_i51146_4_, p_i51146_5_, p_i51146_6_);

            for (VersionEnum version : VersionEnum.SORTED_VERSIONS) {
                addEntry(new SlotEntry(version));
            }
        }

        public class SlotEntry extends ObjectSelectionList.Entry<SlotEntry> {

            private final VersionEnum versionEnum;

            public SlotEntry(VersionEnum versionEnum) {
                this.versionEnum = versionEnum;
            }

            @Override
            public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
                ViaForgeCommon.getManager().setTargetVersion(versionEnum);
                return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
            }

            @Override
            public void render(PoseStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
                drawCenteredString(p_230432_1_, Minecraft.getInstance().font,
                        (ViaForgeCommon.getManager().getTargetVersion() == versionEnum ? ChatFormatting.GREEN.toString() : ChatFormatting.DARK_RED.toString()) + versionEnum.getName(), width / 2, p_230432_3_, -1);
            }

            @Override
            public Component getNarration() {
                return new TextComponent(versionEnum.getName());
            }
        }
    }
}
