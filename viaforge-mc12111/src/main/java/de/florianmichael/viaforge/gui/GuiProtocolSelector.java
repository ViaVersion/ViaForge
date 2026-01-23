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

package de.florianmichael.viaforge.gui;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.util.DumpUtil;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

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
    public boolean keyPressed(final KeyEvent p_423266_) {
        if (p_423266_.key() == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.setScreen(parent);
        }
        return super.keyPressed(p_423266_);
    }

    @Override
    public void render(GuiGraphics graphics, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        if (System.currentTimeMillis() - this.time >= 10_000) {
            this.status = null;
        }

        super.render(graphics, p_230430_2_, p_230430_3_, p_230430_4_);

        final var pose = graphics.pose();

        pose.pushMatrix();
        pose.scale(2.0F, 2.0F);
        graphics.drawCenteredString(font, ChatFormatting.GOLD + "ViaForge", width / 4, 3, -1);
        pose.popMatrix();

        graphics.drawCenteredString(font, "https://github.com/ViaVersion/ViaForge", width / 2, (font.lineHeight + 2) * 2 + 3, -1);
        graphics.drawString(font, status != null ? status : "Discord: florianmichael", 3, 3, -1);
    }

    class SlotList extends ObjectSelectionList<SlotList.SlotEntry> {

        public SlotList(Minecraft client, int width, int height, int top, int bottom, int slotHeight) {
            super(client, width, height - top - bottom, top, slotHeight);

            for (ProtocolVersion version : ProtocolVersion.getReversedProtocols()) {
                addEntry(new SlotEntry(version));
            }
        }

        public class SlotEntry extends Entry<SlotEntry> {

            private final ProtocolVersion ProtocolVersion;

            public SlotEntry(ProtocolVersion ProtocolVersion) {
                this.ProtocolVersion = ProtocolVersion;
            }

            @Override
            public boolean mouseClicked(final MouseButtonEvent p_429480_, final boolean p_425718_) {
                GuiProtocolSelector.this.finishedCallback.finished(ProtocolVersion, GuiProtocolSelector.this.parent);
                return super.mouseClicked(p_429480_, p_425718_);
            }

            @Override
            public Component getNarration() {
                return Component.literal(ProtocolVersion.getName());
            }

            @Override
            public void renderContent(final GuiGraphics guiGraphics, final int i, final int i1, final boolean b, final float v) {
                final ProtocolVersion targetVersion = ViaForgeCommon.getManager().getTargetVersion();

                String color;
                if (targetVersion == ProtocolVersion) {
                    color = GuiProtocolSelector.this.simple ? ChatFormatting.GOLD.toString() : ChatFormatting.GREEN.toString();
                } else {
                    color = GuiProtocolSelector.this.simple ? ChatFormatting.WHITE.toString() : ChatFormatting.DARK_RED.toString();
                }

                guiGraphics.drawCenteredString(Minecraft.getInstance().font, color + ProtocolVersion.getName(), getContentXMiddle(), getContentY(), -1);
            }
        }
    }

    public interface FinishedCallback {

        void finished(final ProtocolVersion version, final Screen parent);

    }

}
