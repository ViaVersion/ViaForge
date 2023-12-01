package de.florianmichael.viaforge.mixin.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viaforge.common.ViaForgeCommon;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.NetworkManager;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.storage.ProtocolMetadataStorage;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("DataFlowIssue")
@Mixin(NetHandlerLoginClient.class)
public class MixinNetHandlerLoginClient {

    @Shadow @Final private NetworkManager networkManager;

    @Redirect(method = "handleEncryptionRequest", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/minecraft/MinecraftSessionService;joinServer(Lcom/mojang/authlib/GameProfile;Ljava/lang/String;Ljava/lang/String;)V"))
    public void onlyJoinServerIfPremium(MinecraftSessionService instance, GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        if (ViaForgeCommon.getManager().getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
            final UserConnection user = networkManager.channel().attr(ViaForgeCommon.LOCAL_VIA_USER).get();
            if (user != null && user.has(ProtocolMetadataStorage.class) && !user.get(ProtocolMetadataStorage.class).authenticate) {
                // We are in the 1.7 -> 1.6 protocol, so we need to skip the joinServer call
                // if the server is in offline mode, due the packet changes <-> networking changes
                // Minecraft's networking code is bad for us.
                return;
            }
        }
        instance.joinServer(profile, authenticationToken, serverId);
    }
}
