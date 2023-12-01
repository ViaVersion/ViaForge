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
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.util.DumpUtil;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.raphimc.vialoader.util.VersionEnum;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GuiProtocolSelector extends GuiScreen {

    private final GuiScreen parent;
    private final boolean simple;
    private final FinishedCallback finishedCallback;

    private SlotList list;

    private String status;
    private long time;

    public GuiProtocolSelector(final GuiScreen parent) {
        this(parent, false, (version, unused) -> {
            // Default action is to set the target version and go back to the parent screen.
            ViaForgeCommon.getManager().setTargetVersion(version);
        });
    }

    public GuiProtocolSelector(final GuiScreen parent, final boolean simple, final FinishedCallback finishedCallback) {
        this.parent = parent;
        this.simple = simple;
        this.finishedCallback = finishedCallback;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(1, 5, height - 25, 20, 20, "<-"));
        if (!this.simple) {
            buttonList.add(new GuiButton(2, width - 105, 5, 100, 20, "Create dump"));
            buttonList.add(new GuiButton(3, width - 105, height - 25, 100, 20, "Reload configs"));
        }

        list = new SlotList(mc, width, height, 3 + 3 /* start offset */ + (fontRendererObj.FONT_HEIGHT + 2) * 3 /* title is 2 */, height - 30, fontRendererObj.FONT_HEIGHT + 2);
    }

    public void setStatus(final String status) {
        this.status = status;
        this.time = System.currentTimeMillis();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        list.actionPerformed(button);

        if (button.id == 1) {
            mc.displayGuiScreen(parent);
        } else if (button.id == 2) {
            try {
                GuiScreen.setClipboardString(DumpUtil.postDump(UUID.randomUUID()).get());
                setStatus(ChatFormatting.GREEN + "Dump created and copied to clipboard");
            } catch (InterruptedException | ExecutionException e) {
                setStatus(ChatFormatting.RED + "Failed to create dump: " + e.getMessage());
            }
        } else {
            Via.getManager().getConfigurationProvider().reloadConfigs();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (System.currentTimeMillis() - this.time >= 10_000) {
            this.status = null;
        }

        list.drawScreen(mouseX, mouseY, partialTicks);

        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(fontRendererObj, ChatFormatting.GOLD + "ViaForge", width / 4, 3, 16777215);
        GL11.glPopMatrix();

        drawCenteredString(fontRendererObj, "https://github.com/ViaVersion/ViaForge", width / 2, (fontRendererObj.FONT_HEIGHT + 2) * 2 + 3, -1);
        drawString(fontRendererObj, status != null ? status : "Discord: florianmichael", 3, 3, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class SlotList extends GuiSlot {

        public SlotList(Minecraft client, int width, int height, int top, int bottom, int slotHeight) {
            super(client, width, height, top, bottom, slotHeight);
        }

        @Override
        protected int getSize() {
            return VersionEnum.SORTED_VERSIONS.size();
        }

        @Override
        protected void elementClicked(int index, boolean b, int i1, int i2) {
            finishedCallback.finished(VersionEnum.SORTED_VERSIONS.get(index), parent);
        }

        @Override
        protected boolean isSelected(int index) {
            return false;
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int index, int x, int y, int slotHeight, int mouseX, int mouseY) {
            final VersionEnum targetVersion = ViaForgeCommon.getManager().getTargetVersion();
            final VersionEnum version = VersionEnum.SORTED_VERSIONS.get(index);

            String color;
            if (targetVersion == version) {
                color = GuiProtocolSelector.this.simple ? ChatFormatting.GOLD.toString() : ChatFormatting.GREEN.toString();
            } else {
                color = GuiProtocolSelector.this.simple ? ChatFormatting.WHITE.toString() : ChatFormatting.DARK_RED.toString();
            }

            drawCenteredString(mc.fontRendererObj,(color) + version.getName(), width / 2, y, -1);
        }
    }

    public interface FinishedCallback {

        void finished(final VersionEnum version, final GuiScreen parent);
    }
}
