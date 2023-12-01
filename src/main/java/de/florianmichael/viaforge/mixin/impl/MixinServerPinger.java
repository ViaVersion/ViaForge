package de.florianmichael.viaforge.mixin.impl;

import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.gui.ExtendedServerData;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.network.NetworkManager;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetAddress;

@Mixin(OldServerPinger.class)
public class MixinServerPinger {

    @Unique
    private ServerData viaForge$serverData;

    @Inject(method = "ping", at = @At("HEAD"))
    public void trackServerData(ServerData server, CallbackInfo ci) {
        viaForge$serverData = server;
    }

    @Redirect(method = "ping", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;func_181124_a(Ljava/net/InetAddress;IZ)Lnet/minecraft/network/NetworkManager;"))
    public NetworkManager trackVersion(InetAddress address, int i, boolean b) {
        // We need to track the version of the server we are connecting to, so we can later
        // use it to determine the protocol version to use.
        // We hope that the current server data is not null

        if (viaForge$serverData instanceof ExtendedServerData) {
            final VersionEnum version = ((ExtendedServerData) viaForge$serverData).viaForge$getVersion();
            if (version != null) {
                ViaForgeCommon.getManager().setTargetVersionSilent(version);
            } else {
                // If the server data does not contain a version, we need to restore the version
                // we had before, so we don't use the wrong version.
                ViaForgeCommon.getManager().restoreVersion();
            }

            viaForge$serverData = null;
        }

        return NetworkManager.func_181124_a(address, i, b);
    }
}
