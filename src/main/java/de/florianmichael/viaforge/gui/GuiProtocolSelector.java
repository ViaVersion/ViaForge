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

import com.mojang.realmsclient.gui.ChatFormatting;
import de.florianmichael.viaforge.ViaForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.raphimc.vialoader.util.VersionEnum;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiProtocolSelector extends GuiScreen {

    private final GuiScreen parent;

    public SlotList list;

    public GuiProtocolSelector(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, width / 2 - 100, height - 27, 200,
                20, "Back"));

        list = new SlotList(mc, width, height, 32, height - 32, 10);
    }

    @Override
    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException {
        list.actionPerformed(p_actionPerformed_1_);

        if (p_actionPerformed_1_.id == 1)
            mc.displayGuiScreen(parent);
    }

    @Override
    protected void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_) throws IOException {
        if (p_keyTyped_2_ == 1) //esc key
           this.mc.displayGuiScreen(parent);
    }

    @Override
    public void handleMouseInput() throws IOException {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        list.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);

        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        this.drawCenteredString(this.fontRendererObj, ChatFormatting.GOLD + "ViaForge", this.width / 4, 6, 16777215);
        GL11.glPopMatrix();

        drawString(this.fontRendererObj, "https://github.com/FlorianMichael/ViaForge", 1, 1, -1);
        drawString(this.fontRendererObj, "Discord: EnZaXD#6257", 1, 11, -1);

        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    class SlotList extends GuiSlot {

        public SlotList(Minecraft p_i1052_1_, int p_i1052_2_, int p_i1052_3_, int p_i1052_4_, int p_i1052_5_, int p_i1052_6_) {
            super(p_i1052_1_, p_i1052_2_, p_i1052_3_, p_i1052_4_, p_i1052_5_, p_i1052_6_);
        }

        @Override
        protected int getSize() {
            return VersionEnum.SORTED_VERSIONS.size();
        }

        @Override
        protected void elementClicked(int i, boolean b, int i1, int i2) {
            ViaForge.targetVersion = VersionEnum.SORTED_VERSIONS.get(i);
        }

        @Override
        protected boolean isSelected(int i) {
            return false;
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5) {
            final VersionEnum version = VersionEnum.SORTED_VERSIONS.get(i);

            drawCenteredString(mc.fontRendererObj, (ViaForge.targetVersion.getVersion() == version.getVersion() ? ChatFormatting.GREEN.toString() : ChatFormatting.DARK_RED.toString()) + version.getName(), width / 2, i2, -1);
        }
    }
}
