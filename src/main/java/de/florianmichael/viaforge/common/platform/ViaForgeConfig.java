package de.florianmichael.viaforge.common.platform;

import com.viaversion.viaversion.util.Config;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViaForgeConfig extends Config {
    public final static String CLIENT_SIDE_VERSION = "client-side-version";

    public final static String SHOW_MAIN_MENU_BUTTON = "show-main-menu-button";
    public final static String SHOW_MULTIPLAYER_BUTTON = "show-multiplayer-button";
    public final static String SHOW_DIRECT_CONNECT_BUTTON = "show-direct-connect-button";

    /**
     * @param configFile The location of where the config is loaded/saved.
     */
    public ViaForgeConfig(File configFile) {
        super(configFile);
        reload();
    }

    @Override
    public URL getDefaultConfigURL() {
        return getClass().getClassLoader().getResource("assets/viaforge/config.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> config) {
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return Collections.emptyList();
    }

    public int getClientSideVersion() {
        return getInt(CLIENT_SIDE_VERSION, 0);
    }

    public void setClientSideVersion(final int version) {
        set(CLIENT_SIDE_VERSION, version);
        save();
    }

    public boolean isShowMainMenuButton() {
        return getBoolean(SHOW_MAIN_MENU_BUTTON, true);
    }

    public boolean isShowMultiplayerButton() {
        return getBoolean(SHOW_MULTIPLAYER_BUTTON, true);
    }

    public boolean isShowDirectConnectButton() {
        return getBoolean(SHOW_DIRECT_CONNECT_BUTTON, true);
    }
}
