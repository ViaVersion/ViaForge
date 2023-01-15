package de.enzaxd.viaforge.loader;

import net.raphimc.vialegacy.platform.ViaLegacyPlatform;
import de.enzaxd.viaforge.ViaForge;

import java.io.File;
import java.util.logging.Logger;

public class LegacyLoader implements ViaLegacyPlatform {
    private final File file;

    public LegacyLoader(final File file) {
        this.init(this.file = new File(file, "ViaLegacy"));
    }

    @Override
    public Logger getLogger() {
        return ViaForge.getInstance().getjLogger();
    }

    @Override
    public File getDataFolder() {
        return new File(this.file, "config.yml");
    }
}
