package mekanism.common.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import io.github.fabricators_of_create.porting_lib.config.ConfigRegistry;
import mekanism.common.Mekanism;
import net.fabricmc.loader.api.FabricLoader;
import io.github.fabricators_of_create.porting_lib.config.IConfigSpec;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import io.github.fabricators_of_create.porting_lib.config.ModConfigEvent;

public class MekanismConfigHelper {

    private MekanismConfigHelper() {
    }

    public static final Path CONFIG_DIR = getOrCreateGameRelativePath(FabricLoader.getInstance().getConfigDir().resolve(Mekanism.MOD_NAME));

    public static Path getOrCreateGameRelativePath(Path path) {
        Path gameFolderPath = FabricLoader.getInstance().getGameDir().resolve(path);

        if (!Files.isDirectory(gameFolderPath)) {
            try {
                Files.createDirectories(gameFolderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return gameFolderPath;
    }

    /**
     * Creates and register a mod config, and track it so that we can properly clear cached values.
     */
    public static void registerConfig(Map<IConfigSpec, IMekanismConfig> knownConfigs, String modid, IMekanismConfig config) {
        ConfigRegistry.registerConfig(modid, config.getConfigType(), config.getConfigSpec(), Mekanism.MOD_NAME + "/" + config.getFileName() + ".toml");
        knownConfigs.put(config.getConfigSpec(), config);
    }

    public static void onConfigLoad(ModConfigEvent event, String modid, Map<IConfigSpec, IMekanismConfig> knownConfigs) {
        //Note: We listen to both the initial load and the reload, to make sure that we fix any accidentally
        // cached values from calls before the initial loading
        ModConfig config = event.getConfig();
        //Make sure it is for the same modid as us
        if (config.getModId().equals(modid)) {
            IMekanismConfig mekanismConfig = knownConfigs.get(config.getSpec());
            if (mekanismConfig != null) {
                mekanismConfig.clearCache(event instanceof ModConfigEvent.Unloading);
            }
        }
    }
}