/*
 * This file is part of ViaMCP - https://github.com/FlorianMichael/ViaMCP
 * Copyright (C) 2020-2023 FlorianMichael/EnZaXD and contributors
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

import de.florianmichael.viaforge.ViaForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.Collections;
import java.util.List;

public class AsyncVersionSlider extends GuiButton {
    private float dragValue = calculateDragValue(ViaForge.targetVersion);

    private final List<VersionEnum> values;
    private float sliderValue;
    public boolean dragging;

    public AsyncVersionSlider(int buttonId, int x, int y, int widthIn, int heightIn) {
        super(buttonId, x, y, Math.max(widthIn, 110), heightIn, "");
        this.values = VersionEnum.SORTED_VERSIONS;
        Collections.reverse(values);
        this.sliderValue = dragValue;
        this.displayString = getSliderVersion().getName();
    }

    /**
     * Calculates and returns the dragValue for the provided version.
     */
    public float calculateDragValue(VersionEnum version) {
        int size = VersionEnum.SORTED_VERSIONS.size();
        return (size - VersionEnum.SORTED_VERSIONS.indexOf(version)) / size;
    }

    /**
     * Finds and returns the currently selected version from the slider.
     */
    public VersionEnum getSliderVersion() {
        return values.get((int) (this.sliderValue * (values.size() - 1)));
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this
     * button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of
     * MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
                this.dragValue = sliderValue;
                this.displayString = getSliderVersion().getName();
                ViaForge.targetVersion = getSliderVersion();
            }

            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)),
                    this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)) + 4,
                    this.yPosition, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of
     * MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            this.dragValue = sliderValue;
            this.displayString = getSliderVersion().getName();
            ViaForge.targetVersion = getSliderVersion();
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of
     * MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}
