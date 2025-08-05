package mekanism.common.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import mekanism.common.Mekanism;
import io.github.fabricators_of_create.porting_lib.config.IConfigSpec;
import io.github.fabricators_of_create.porting_lib.config.ModConfigEvent;

public class MekanismConfig {

    private MekanismConfig() {
    }

    private static final Map<IConfigSpec, IMekanismConfig> KNOWN_CONFIGS = new HashMap<>();
    public static final ClientConfig client = new ClientConfig();
    public static final CommonConfig common = new CommonConfig();
    public static final GeneralConfig general = new GeneralConfig();
    public static final GearConfig gear = new GearConfig();
    public static final MekanismStartupConfig startup = new MekanismStartupConfig();
    public static final StorageConfig storage = new StorageConfig();
    public static final TierConfig tiers = new TierConfig();
    public static final UsageConfig usage = new UsageConfig();
    public static final WorldConfig world = new WorldConfig();

    public static void registerConfigs(String modId) {
        MekanismConfigHelper.registerConfig(KNOWN_CONFIGS, modId, client);
        MekanismConfigHelper.registerConfig(KNOWN_CONFIGS, modId, common);
        MekanismConfigHelper.registerConfig(KNOWN_CONFIGS, modId, general);
        MekanismConfigHelper.registerConfig(KNOWN_CONFIGS, modId, gear);
        MekanismConfigHelper.registerConfig(KNOWN_CONFIGS, modId, startup);
        MekanismConfigHelper.registerConfig(KNOWN_CONFIGS, modId, storage);
        MekanismConfigHelper.registerConfig(KNOWN_CONFIGS, modId, tiers);
        MekanismConfigHelper.registerConfig(KNOWN_CONFIGS, modId, usage);
        MekanismConfigHelper.registerConfig(KNOWN_CONFIGS, modId, world);
    }

    public static void onConfigLoad(ModConfigEvent configEvent) {
        MekanismConfigHelper.onConfigLoad(configEvent, Mekanism.MODID, KNOWN_CONFIGS);
    }

    public static Collection<IMekanismConfig> getConfigs() {
        return Collections.unmodifiableCollection(KNOWN_CONFIGS.values());
    }
}