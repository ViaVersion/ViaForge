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

import de.florianmichael.viaforge.ViaForge115;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.raphimc.vialoader.util.VersionEnum;
import org.lwjgl.opengl.GL11;

public class GuiProtocolSelector extends Screen {

    private final Screen parent;

    public static void open(final Minecraft minecraft) { // Bypass for some weird bytecode instructions errors in Forge
        minecraft.setScreen(new GuiProtocolSelector(minecraft.screen));
    }

    private SlotList slotList;

    public GuiProtocolSelector(Screen parent) {
        super(new StringTextComponent("ViaForge Protocol Selector"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        slotList = new SlotList(minecraft, width, height, 32, height - 32, 10);
        addButton(new Button(width / 2 - 100, height - 27, 200, 20, "Back", b -> minecraft.setScreen(parent)));
    }

    @Override
    public void render(int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        renderBackground();
        this.slotList.render(p_230430_2_, p_230430_3_, p_230430_4_);

        super.render(p_230430_2_, p_230430_3_, p_230430_4_);

        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(this.font, TextFormatting.GOLD + "ViaForge", this.width / 4, 6, 16777215);
        GL11.glPopMatrix();

        drawString(this.font, "by https://github.com/ViaVersion/ViaForge", 1, 1, -1);
        drawString(this.font, "Discord: florianmichael", 1, 11, -1);
    }

    static class SlotList extends ExtendedList<SlotList.SlotEntry> {

        public SlotList(Minecraft p_i51146_1_, int p_i51146_2_, int p_i51146_3_, int p_i51146_4_, int p_i51146_5_, int p_i51146_6_) {
            super(p_i51146_1_, p_i51146_2_, p_i51146_3_, p_i51146_4_, p_i51146_5_, p_i51146_6_);

            for (VersionEnum version : VersionEnum.SORTED_VERSIONS) {
                addEntry(new SlotEntry(version));
            }
        }

        public class SlotEntry extends AbstractList.AbstractListEntry<SlotEntry> {

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
            public void render(int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
                drawCenteredString(Minecraft.getInstance().font,
                        (ViaForgeCommon.getManager().getTargetVersion() == versionEnum ? TextFormatting.GREEN.toString() : TextFormatting.DARK_RED.toString()) + versionEnum.getName(), width / 2, p_230432_3_, -1);
            }
        }
    }
}
