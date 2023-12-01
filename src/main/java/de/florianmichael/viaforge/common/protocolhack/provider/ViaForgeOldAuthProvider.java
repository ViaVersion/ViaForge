package de.florianmichael.viaforge.common.protocolhack.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;

public class ViaForgeOldAuthProvider extends OldAuthProvider {

    @Override
    public void sendAuthRequest(UserConnection user, String serverId) throws Throwable {
        final ViaForgeCommon common = ViaForgeCommon.getManager();
        if (!common.getConfig().isVerifySessionInOldVersions()) {
            return;
        }

        common.getPlatform().joinServer(serverId);
    }
}
