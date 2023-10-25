package de.florianmichael.viaforge.mixin.impl;

import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.gui.ExtendedServerData;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.network.NetworkManager;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetAddress;

@Mixin(ServerPinger.class)
public class MixinServerPinger {

    @Unique
    private ServerData viaforge_serverData;

    @Inject(method = "ping", at = @At("HEAD"))
    public void trackServerData(ServerData server, CallbackInfo ci) {
        viaforge_serverData = server;
    }

    @Redirect(method = "ping", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;createNetworkManagerAndConnect(Ljava/net/InetAddress;IZ)Lnet/minecraft/network/NetworkManager;"))
    public NetworkManager trackVersion(InetAddress address, int i, boolean b) {
        // We need to track the version of the server we are connecting to, so we can later
        // use it to determine the protocol version to use.
        // We hope that the current server data is not null

        if (viaforge_serverData instanceof ExtendedServerData) {
            final VersionEnum version = ((ExtendedServerData) viaforge_serverData).viaforge_getVersion();
            if (version != null) {
                ViaForgeCommon.getManager().setTargetVersionSilent(version);
            } else {
                // If the server data does not contain a version, we need to restore the version
                // we had before, so we don't use the wrong version.
                ViaForgeCommon.getManager().restoreVersion();
            }

            viaforge_serverData = null;
        }

        return NetworkManager.createNetworkManagerAndConnect(address, i, b);
    }
}
