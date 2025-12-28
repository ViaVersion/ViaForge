/*
 * This file is part of ViaForge - https://github.com/FlorianMichael/ViaForge
 * Copyright (C) 2021-2026 FlorianMichael/EnZaXD <git@florianmichael.de> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viaforge.provider;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.response.NameAndId;
import com.viaversion.viaversion.api.minecraft.GameProfile;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.provider.GameProfileFetcher;

@SuppressWarnings("JavaReflectionMemberAccess")
public class ViaForgeGameProfileFetcher extends GameProfileFetcher {

    private static final HttpAuthenticationService AUTHENTICATION_SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
    private static final MinecraftSessionService SESSION_SERVICE = AUTHENTICATION_SERVICE.createMinecraftSessionService();
    private static final GameProfileRepository GAME_PROFILE_REPOSITORY = AUTHENTICATION_SERVICE.createProfileRepository();

    private static Method getMinecraftSessionService;
    private static Method getId;
    private static Method getName;
    private static Method getProperties;

    @Override
    public UUID loadMojangUuid(String playerName) {
        if (SharedConstants.getProtocolVersion() < 773) {
            final CompletableFuture<com.mojang.authlib.GameProfile> future = new CompletableFuture<>();
            GAME_PROFILE_REPOSITORY.findProfilesByNames(new String[]{playerName}, new ProfileLookupCallback() {

                public void onProfileLookupSucceeded(com.mojang.authlib.GameProfile profile) {
                    future.complete(profile);
                }

                public void onProfileLookupSucceeded(final String profileName, final UUID profileId) {
                    this.onProfileLookupSucceeded(new com.mojang.authlib.GameProfile(profileId, profileName));
                }

                @Override
                public void onProfileLookupFailed(String profileName, Exception exception) {
                    future.completeExceptionally(exception);
                }
            });
            if (!future.isDone()) {
                future.completeExceptionally(new ProfileNotFoundException());
            }
            com.mojang.authlib.GameProfile profile;
            try {
                profile = future.get();
            } catch (Exception e) {
                throw new ProfileNotFoundException(e);
            }
            return getId(profile);
        } else {
            final Optional<NameAndId> nameAndId = GAME_PROFILE_REPOSITORY.findProfileByName(playerName);
            if (nameAndId.isEmpty()) {
                throw new ProfileNotFoundException();
            }
            return nameAndId.get().id();
        }
    }

    @Override
    public GameProfile loadGameProfile(UUID uuid) {
        final ProfileResult result = SESSION_SERVICE.fetchProfile(uuid, true);
        if (result == null) {
            throw new ProfileNotFoundException();
        }
        final com.mojang.authlib.GameProfile mojangProfile = result.profile();
        final GameProfile.Property[] properties = new GameProfile.Property[getProperties(mojangProfile).size()];
        int i = 0;
        for (final Map.Entry<String, Property> entry : getProperties(mojangProfile).entries()) {
            properties[i++] = new GameProfile.Property(entry.getValue().name(), entry.getValue().value(), entry.getValue().signature());
        }
        return new GameProfile(getName(mojangProfile), getId(mojangProfile), properties);
    }

    public static void onJoinServer(String serverId) throws Throwable {
        final Minecraft mc = Minecraft.getInstance();
        final User session = mc.getUser();
        MinecraftSessionService service;
        if (SharedConstants.getProtocolVersion() < 773) {
            if (getMinecraftSessionService == null) {
                getMinecraftSessionService = Minecraft.class.getDeclaredMethod("getMinecraftSessionService");
            }
            service = (MinecraftSessionService) getMinecraftSessionService.invoke(mc);
        } else {
            service = mc.services().sessionService();
        }
        service.joinServer(session.getProfileId(), session.getAccessToken(), serverId);
    }

    public static UUID getId(com.mojang.authlib.GameProfile mojangProfile) {
        if (SharedConstants.getProtocolVersion() < 773) {
            try {
                if (getId == null) {
                    getId = GameProfile.class.getDeclaredMethod("getId");
                }
                return (UUID) getId.invoke(mojangProfile);
            } catch (Exception e) {
                throw new ProfileNotFoundException(e);
            }
        } else {
            return mojangProfile.id();
        }
    }

    public static String getName(com.mojang.authlib.GameProfile mojangProfile) {
        if (SharedConstants.getProtocolVersion() < 773) {
            try {
                if (getName == null) {
                    getName = GameProfile.class.getDeclaredMethod("getName");
                }
                return getName.invoke(mojangProfile).toString();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            return mojangProfile.name();
        }
    }

    public static PropertyMap getProperties(com.mojang.authlib.GameProfile mojangProfile) {
        if (SharedConstants.getProtocolVersion() < 773) {
            try {
                if (getProperties == null) {
                    getProperties = GameProfile.class.getDeclaredMethod("getProperties");
                }
                return (PropertyMap) getProperties.invoke(mojangProfile);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            return mojangProfile.properties();
        }
    }
}
