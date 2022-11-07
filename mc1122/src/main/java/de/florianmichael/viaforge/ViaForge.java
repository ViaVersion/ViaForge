package de.florianmichael.viaforge;

import com.viaversion.viaversion.libs.gson.JsonObject;
import de.florianmichael.viaprotocolhack.INativeProvider;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

@Mod(modid = "viaforge", name = "ViaForge", version = "1.0.0")
public class ViaForge implements INativeProvider {

    public static int targetVersion = RealmsSharedConstants.NETWORK_PROTOCOL_VERSION;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) throws Exception {
        ViaProtocolHack.instance().init(this, () -> System.out.println("ViaProtocolHack loaded successfully"));
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
        return Minecraft.getMinecraft().gameDir;
    }

    @Override
    public JsonObject createDump() {
        return new JsonObject();
    }

    @Override
    public EventLoop eventLoop(ThreadFactory threadFactory, ExecutorService executorService) {
        return new DefaultEventLoop(executorService);
    }
}
