package de.florianmichael.viaforge.common.protocolhack.netty;

import net.raphimc.vialoader.util.VersionEnum;

public interface VFNetworkManager {

    /**
     * API method to setup the decryption side of the pipeline.
     * This method is called by the {@link de.florianmichael.viaforge.common.protocolhack.provider.ViaForgeEncryptionProvider} class.
     */
    void viaforge_setupPreNettyDecryption();

    VersionEnum viaforge_getTrackedVersion();

    void viaforge_setTrackedVersion(final VersionEnum version);
}
