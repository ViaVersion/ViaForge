package de.enzaxd.viaforge;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import de.enzaxd.viaforge.loader.BackwardsLoader;
import de.enzaxd.viaforge.loader.LegacyLoader;
import de.enzaxd.viaforge.platform.Injector;
import de.enzaxd.viaforge.platform.Platform;
import de.enzaxd.viaforge.platform.ProviderLoader;
import de.enzaxd.viaforge.util.JLoggerToLog4j;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "viaforge", name = "ViaForge", version = "1.0.0")
public class ViaForge {

    public final static int SHARED_VERSION = 340;

    private static ViaForge instance = null;
    { instance = this; } // called when forge initializes the mod
    private final Logger jLogger = new JLoggerToLog4j(LogManager.getLogger("ViaForge"));
    private final CompletableFuture<Void> initFuture = new CompletableFuture<>();
    private ExecutorService asyncExecutor;
    private EventLoop eventLoop;
    private File file;
    private int version;
    private String lastServer;

    public static ViaForge getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaForge-%d").build();
        asyncExecutor = Executors.newFixedThreadPool(8, factory);

        eventLoop = new DefaultEventLoopGroup(1, factory).next();
        eventLoop.submit(initFuture::join);

        setVersion(SHARED_VERSION);
        this.file = new File("ViaForge");
        if (this.file.mkdir())
            this.getjLogger().info("Creating ViaForge Folder");

        Via.init(
                ViaManagerImpl.builder()
                        .injector(new Injector())
                        .loader(new ProviderLoader())
                        .platform(new Platform(file))
                        .build()
        );

        MappingDataLoader.enableMappingsCache();
        ((ViaManagerImpl) Via.getManager()).init();

        new BackwardsLoader(file);
        new LegacyLoader(file);

        initFuture.complete(null);
    }

    public Logger getjLogger() {
        return jLogger;
    }

    public CompletableFuture<Void> getInitFuture() {
        return initFuture;
    }

    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    public EventLoop getEventLoop() {
        return eventLoop;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLastServer() {
        return lastServer;
    }

    public void setLastServer(String lastServer) {
        this.lastServer = lastServer;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
