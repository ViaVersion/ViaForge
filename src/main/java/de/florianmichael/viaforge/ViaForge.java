package de.florianmichael.viaforge;

import com.viaversion.viaversion.libs.gson.JsonObject;
import de.florianmichael.viaprotocolhack.INativeProvider;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsSharedConstants;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class ViaForge implements INativeProvider {

    public static int targetVersion = RealmsSharedConstants.NETWORK_PROTOCOL_VERSION;

    public static void start() {
        try {
            ViaProtocolHack.instance().init(new ViaForge(), () -> System.out.println("ViaProtocolHack loaded successfully"));
        } catch (Exception ignored) {
        }
    }
    
    @Override
    public boolean isSinglePlayer() {
        return Minecraft.getMinecraft().isSingleplayer();
    }

    @Override
    public int nativeVersion() {
        return RealmsSharedConstants.NETWORK_PROTOCOL_VERSION;
    }

    @Override
    public int targetVersion() {
        return targetVersion;
    }

    @Override
    public String[] nettyOrder() {
        return new String[] {
                "decompress",
                "compress"
        };
    }

    @Override
    public File run() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public JsonObject createDump() {
        return new JsonObject();
    }

    @Override
    public EventLoop eventLoop(ThreadFactory threadFactory, ExecutorService executorService) {
        return new LocalEventLoopGroup(1, threadFactory).next();
    }
}
