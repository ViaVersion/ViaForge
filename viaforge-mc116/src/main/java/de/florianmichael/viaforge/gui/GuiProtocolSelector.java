/*
 * This file is part of ViaForge - https://github.com/FlorianMichael/ViaForge
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and contributors
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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.util.DumpUtil;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.raphimc.vialoader.util.VersionEnum;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GuiProtocolSelector extends Screen {

    public final Screen parent;
    public final boolean simple;
    public final FinishedCallback finishedCallback;

    private SlotList list;

    private String status;
    private long time;

    public static void open(final Minecraft minecraft) { // Bypass for some weird bytecode instructions errors in Forge
        minecraft.setScreen(new GuiProtocolSelector(minecraft.screen));
    }

    public GuiProtocolSelector(final Screen parent) {
        this(parent, false, (version, unused) -> {
            // Default action is to set the target version and go back to the parent screen.
            ViaForgeCommon.getManager().setTargetVersion(version);
        });
    }

    public GuiProtocolSelector(final Screen parent, final boolean simple, final FinishedCallback finishedCallback) {
        super(new StringTextComponent("ViaForge Protocol Selector"));
        this.parent = parent;
        this.simple = simple;
        this.finishedCallback = finishedCallback;
    }

    @Override
    public void init() {
        super.init();
        addButton(new Button(5, height - 25, 20, 20, new StringTextComponent("<-"), b -> minecraft.setScreen(parent)));
        if (!this.simple) {
            addButton(new Button(width - 105, 5, 100, 20, new StringTextComponent("Create dump"), b -> {
                try {
                    minecraft.keyboardHandler.setClipboard(DumpUtil.postDump(UUID.randomUUID()).get());
                    setStatus(TextFormatting.GREEN + "Dump created and copied to clipboard");
                } catch (InterruptedException | ExecutionException e) {
                    setStatus(TextFormatting.RED + "Failed to create dump: " + e.getMessage());
                }
            }));
            addButton(new Button(width - 105, height - 25, 100, 20, new StringTextComponent("Reload configs"), b -> Via.getManager().getConfigurationProvider().reloadConfigs()));
        }

        addWidget(list = new SlotList(minecraft, width, height, 3 + 3 /* start offset */ + (font.lineHeight + 2) * 3 /* title is 2 */, height - 30, font.lineHeight + 2));
    }

    public void setStatus(final String status) {
        this.status = status;
        this.time = System.currentTimeMillis();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int actions) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.setScreen(parent);
        }
        return super.keyPressed(keyCode, scanCode, actions);
    }

    @Override
    public void render(MatrixStack matrices, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        if (System.currentTimeMillis() - this.time >= 10_000) {
            this.status = null;
        }

        renderBackground(matrices);
        list.render(matrices, p_230430_2_, p_230430_3_, p_230430_4_);

        super.render(matrices, p_230430_2_, p_230430_3_, p_230430_4_);

        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(matrices, font, TextFormatting.GOLD + "ViaForge", width / 4, 3, 16777215);
        GL11.glPopMatrix();

        drawCenteredString(matrices, font, "https://github.com/ViaVersion/ViaForge", width / 2, (font.lineHeight + 2) * 2 + 3, -1);
        drawString(matrices, font, status != null ? status : "Discord: florianmichael", 3, 3, -1);
    }

    class SlotList extends ExtendedList<SlotList.SlotEntry> {

        public SlotList(Minecraft client, int width, int height, int top, int bottom, int slotHeight) {
            super(client, width, height, top, bottom, slotHeight);

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
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                GuiProtocolSelector.this.finishedCallback.finished(versionEnum, GuiProtocolSelector.this.parent);
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public void render(MatrixStack matrices, int p_230432_2_, int y, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
                final VersionEnum targetVersion = ViaForgeCommon.getManager().getTargetVersion();

                String color;
                if (targetVersion == versionEnum) {
                    color = GuiProtocolSelector.this.simple ? TextFormatting.GOLD.toString() : TextFormatting.GREEN.toString();
                } else {
                    color = GuiProtocolSelector.this.simple ? TextFormatting.WHITE.toString() : TextFormatting.DARK_RED.toString();
                }

                drawCenteredString(matrices, Minecraft.getInstance().font, color + versionEnum.getName(), width / 2, y, -1);
            }
        }
    }

    public interface FinishedCallback {

        void finished(final VersionEnum version, final Screen parent);

    }

}
