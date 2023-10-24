package de.florianmichael.viaforge.common.protocolhack.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.providers.EncryptionProvider;

public class ViaForgeEncryptionProvider extends EncryptionProvider {

    @Override
    public void enableDecryption(UserConnection user) {
        user.getChannel().attr(ViaForgeCommon.ENCRYPTION_SETUP).getAndRemove().viaforge_setupPreNettyDecryption();
    }
}
