package de.florianmichael.viaforge.provider;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.model.GameProfile;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;

import java.net.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ViaForgeGameProfileFetcher extends GameProfileFetcher {
    public final static HttpAuthenticationService AUTHENTICATION_SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
    public final static MinecraftSessionService SESSION_SERVICE = AUTHENTICATION_SERVICE.createMinecraftSessionService();
    public final static GameProfileRepository GAME_PROFILE_REPOSITORY = AUTHENTICATION_SERVICE.createProfileRepository();

    @Override
    public UUID loadMojangUUID(String playerName) throws Exception {
        final CompletableFuture<com.mojang.authlib.GameProfile> future = new CompletableFuture<>();
        GAME_PROFILE_REPOSITORY.findProfilesByNames(new String[]{playerName}, new ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(com.mojang.authlib.GameProfile profile) {
                future.complete(profile);
            }

            @Override
            public void onProfileLookupFailed(String profileName, Exception exception) {
                future.completeExceptionally(exception);
            }
        });
        if (!future.isDone()) {
            future.completeExceptionally(new ProfileNotFoundException());
        }
        return future.get().getId();
    }

    @Override
    public GameProfile loadGameProfile(UUID uuid) throws Exception {
        final var result = SESSION_SERVICE.fetchProfile(uuid, true);
        if (result == null) throw new ProfileNotFoundException();

        final var profile = result.profile();
        final var gameProfile = new GameProfile(profile.getName(), profile.getId());

        for (final var entry : profile.getProperties().entries()) {
            final Property prop = entry.getValue();
            gameProfile.addProperty(new GameProfile.Property(prop.name(), prop.value(), prop.signature()));
        }
        return gameProfile;
    }
}
