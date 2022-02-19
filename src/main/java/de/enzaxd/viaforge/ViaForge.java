package de.enzaxd.viaforge;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.enzaxd.viaforge.loader.BackwardsLoader;
import de.enzaxd.viaforge.loader.RewindLoader;
import de.enzaxd.viaforge.platform.Injector;
import de.enzaxd.viaforge.platform.Platform;
import de.enzaxd.viaforge.platform.ProviderLoader;
import de.enzaxd.viaforge.util.JLoggerToLog4j;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaForge {

    public final static ProtocolVersion SHARED_PROTOCOL = ProtocolVersion.v1_8;
    public final static int SHARED_VERSION = SHARED_PROTOCOL.getVersion();

    private static final ViaForge instance = new ViaForge();

    public static ViaForge getInstance() {
        return instance;
    }

    private final Logger jLogger = new JLoggerToLog4j(LogManager.getLogger("ViaForge"));
    private final CompletableFuture<Void> initFuture = new CompletableFuture<>();

    private ExecutorService asyncExecutor;
    private EventLoop eventLoop;

    private File file;
    private ProtocolVersion protocol;
    private String lastServer;

    /**
     * [ProtocolVersion.getProtocols()] will create an unmodifiable list every time called and will cause performance issues.
     */
    private List<ProtocolVersion> protocolVersions;

    public void start() {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaForge-%d").build();
        asyncExecutor = Executors.newFixedThreadPool(8, factory);

        eventLoop = new LocalEventLoopGroup(1, factory).next();
        eventLoop.submit(initFuture::join);

        setProtocol(SHARED_PROTOCOL);
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
        new RewindLoader(file);

        initFuture.complete(null);

        protocolVersions = new ArrayList<>(ProtocolVersion.getProtocols());
        protocolVersions.remove(ProtocolVersion.unknown); // remove unknown protocol
        protocolVersions.sort((o1, o2) -> o2.getVersion() - o1.getVersion());
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

    public String getLastServer() {
        return lastServer;
    }

    public ProtocolVersion getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolVersion protocolIn) {
        this.protocol = protocolIn;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setLastServer(String lastServer) {
        this.lastServer = lastServer;
    }

    public List<ProtocolVersion> getProtocols() {
        return protocolVersions;
    }
}