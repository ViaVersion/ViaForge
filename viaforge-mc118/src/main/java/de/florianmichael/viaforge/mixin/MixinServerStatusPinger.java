package de.florianmichael.viaforge.mixin;

import de.florianmichael.viaforge.common.ViaForgeCommon;
import de.florianmichael.viaforge.common.gui.ExtendedServerData;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.network.Connection;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ServerStatusPinger.class)
public class MixinServerStatusPinger {

    @Unique
    private ServerData viaforge_serverData;

    @Inject(method = "pingServer", at = @At("HEAD"))
    public void trackServerData(ServerData server, Runnable p_147224_2_, CallbackInfo ci) {
        viaforge_serverData = server;
    }

    @Redirect(method = "pingServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connectToServer(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/network/Connection;"))
    public Connection trackVersion(InetSocketAddress oclass, boolean lazyloadedvalue) {
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

        return Connection.connectToServer(oclass, lazyloadedvalue);
    }
}
