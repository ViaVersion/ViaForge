package de.florianmichael.viaforge.common.protocolhack.netty;

import net.raphimc.vialoader.util.VersionEnum;

public interface VFNetworkManager {

    /**
     * API method to setup the decryption side of the pipeline.
     * This method is called by the {@link de.florianmichael.viaforge.common.protocolhack.provider.ViaForgeEncryptionProvider} class.
     */
    void viaForge$setupPreNettyDecryption();

    /**
     * @return the target version of the connection
     */
    VersionEnum viaForge$getTrackedVersion();

    /**
     * Sets the target version of the connection.
     *
     * @param version the target version
     */
    void viaForge$setTrackedVersion(final VersionEnum version);
}
