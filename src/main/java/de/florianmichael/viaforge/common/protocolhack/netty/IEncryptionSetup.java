package de.florianmichael.viaforge.common.protocolhack.netty;

public interface IEncryptionSetup {

    /**
     * API method to setup the decryption side of the pipeline.
     * This method is called by the {@link de.florianmichael.viaforge.common.protocolhack.provider.ViaForgeEncryptionProvider} class.
     */
    void viaforge_setupPreNettyDecryption();
}
