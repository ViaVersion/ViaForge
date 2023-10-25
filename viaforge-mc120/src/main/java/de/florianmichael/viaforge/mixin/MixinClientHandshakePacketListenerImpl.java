package de.florianmichael.viaforge.mixin;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.storage.ProtocolMetadataStorage;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientHandshakePacketListenerImpl.class)
public class MixinClientHandshakePacketListenerImpl {

    @Shadow @Final private Connection connection;

    @Redirect(method = "authenticateServer", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/minecraft/MinecraftSessionService;joinServer(Ljava/util/UUID;Ljava/lang/String;Ljava/lang/String;)V"))
    public void onlyJoinServerIfPremium(MinecraftSessionService instance, UUID uuid, String authenticationToken, String serverId) throws AuthenticationException {
        if (ViaForgeCommon.getManager().getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
            final UserConnection user = connection.channel().attr(ViaForgeCommon.LOCAL_VIA_USER).get();
            if (user != null && user.has(ProtocolMetadataStorage.class) && !user.get(ProtocolMetadataStorage.class).authenticate) {
                // We are in the 1.7 -> 1.6 protocol, so we need to skip the joinServer call
                // if the server is in offline mode, due the packet changes <-> networking changes
                // Minecraft's networking code is bad for us.
                return;
            }
        }
        instance.joinServer(uuid, authenticationToken, serverId);
    }
}
