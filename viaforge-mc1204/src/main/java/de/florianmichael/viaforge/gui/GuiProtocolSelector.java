/*
 * This file is part of ViaForge - https://github.com/FlorianMichael/ViaForge
 * Copyright (C) 2021-2025 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and contributors
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

import com.viaversion.vialoader.util.ProtocolVersionList;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.util.DumpUtil;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GuiProtocolSelector extends Screen {

    public final Screen parent;
    public final boolean simple;
    public final FinishedCallback finishedCallback;

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
        super(Component.literal("ViaForge Protocol Selector"));
        this.parent = parent;
        this.simple = simple;
        this.finishedCallback = finishedCallback;
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("<-"), b -> minecraft.setScreen(parent)).bounds(5, height - 25, 20, 20).build());
        if (!this.simple) {
            addRenderableWidget(Button.builder(Component.literal("Create dump"), b -> {
                try {
                    minecraft.keyboardHandler.setClipboard(DumpUtil.postDump(UUID.randomUUID()).get());
                    setStatus(ChatFormatting.GREEN + "Dump created and copied to clipboard");
                } catch (InterruptedException | ExecutionException e) {
                    setStatus(ChatFormatting.RED + "Failed to create dump: " + e.getMessage());
                }
            }).bounds(width - 105, 5, 100, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Reload configs"), b -> Via.getManager().getConfigurationProvider().reloadConfigs()).bounds(width - 105, height - 25, 100, 20).build());
        }

        addRenderableWidget(new SlotList(minecraft, width, height, 3 + 3 /* start offset */ + (font.lineHeight + 2) * 3 /* title is 2 */, 30, font.lineHeight + 2));
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
    public void render(GuiGraphics graphics, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        if (System.currentTimeMillis() - this.time >= 10_000) {
            this.status = null;
        }

        super.render(graphics, p_230430_2_, p_230430_3_, p_230430_4_);

        final var pose = graphics.pose();

        pose.pushPose();
        pose.scale(2.0F, 2.0F, 2.0F);
        graphics.drawCenteredString(font, ChatFormatting.GOLD + "ViaForge", width / 4, 3, 16777215);
        pose.popPose();

        graphics.drawCenteredString(font, "https://github.com/ViaVersion/ViaForge", width / 2, (font.lineHeight + 2) * 2 + 3, -1);
        graphics.drawString(font, status != null ? status : "Discord: florianmichael", 3, 3, -1);
    }

    class SlotList extends ObjectSelectionList<SlotList.SlotEntry> {

        public SlotList(Minecraft client, int width, int height, int top, int bottom, int slotHeight) {
            super(client, width, height - top - bottom, top, slotHeight);

            for (ProtocolVersion version : ProtocolVersionList.getProtocolsNewToOld()) {
                addEntry(new SlotEntry(version));
            }
        }

        public class SlotEntry extends Entry<SlotEntry> {

            private final ProtocolVersion ProtocolVersion;

            public SlotEntry(ProtocolVersion ProtocolVersion) {
                this.ProtocolVersion = ProtocolVersion;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                GuiProtocolSelector.this.finishedCallback.finished(ProtocolVersion, GuiProtocolSelector.this.parent);
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public Component getNarration() {
                return Component.literal(ProtocolVersion.getName());
            }

            @Override
            public void render(GuiGraphics graphics, int p_93524_, int y, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_) {
                final ProtocolVersion targetVersion = ViaForgeCommon.getManager().getTargetVersion();

                String color;
                if (targetVersion == ProtocolVersion) {
                    color = GuiProtocolSelector.this.simple ? ChatFormatting.GOLD.toString() : ChatFormatting.GREEN.toString();
                } else {
                    color = GuiProtocolSelector.this.simple ? ChatFormatting.WHITE.toString() : ChatFormatting.DARK_RED.toString();
                }

                graphics.drawCenteredString(Minecraft.getInstance().font, color + ProtocolVersion.getName(), width / 2, y, -1);
            }
        }
    }

    public interface FinishedCallback {

        void finished(final ProtocolVersion version, final Screen parent);

    }

}
