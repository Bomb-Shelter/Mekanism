package mekanism.generators.common;

import io.github.fabricators_of_create.porting_lib.config.ModConfigEvent;
import mekanism.api.MekanismIMC;
import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.command.builders.BuildCommand;
import mekanism.common.lib.Version;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.recipe.ClearConfigurationRecipe;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.fission.FissionReactorCache;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.content.fission.FissionReactorValidator;
import mekanism.generators.common.content.fusion.FusionReactorCache;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.content.fusion.FusionReactorValidator;
import mekanism.generators.common.content.turbine.TurbineCache;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.content.turbine.TurbineValidator;
import mekanism.generators.common.network.GeneratorsPacketHandler;
import mekanism.generators.common.registries.GeneratorsBlocks;
import mekanism.generators.common.registries.GeneratorsBuilders.FissionReactorBuilder;
import mekanism.generators.common.registries.GeneratorsBuilders.FusionReactorBuilder;
import mekanism.generators.common.registries.GeneratorsBuilders.TurbineBuilder;
import mekanism.generators.common.registries.GeneratorsChemicals;
import mekanism.generators.common.registries.GeneratorsContainerTypes;
import mekanism.generators.common.registries.GeneratorsCreativeTabs;
import mekanism.generators.common.registries.GeneratorsDataComponents;
import mekanism.generators.common.registries.GeneratorsFluids;
import mekanism.generators.common.registries.GeneratorsItems;
import mekanism.generators.common.registries.GeneratorsModules;
import mekanism.generators.common.registries.GeneratorsSounds;
import mekanism.generators.common.registries.GeneratorsTileEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resources.ResourceLocation;

public class MekanismGenerators implements IModModule, ModInitializer {

    public static final String MODID = "mekanismgenerators";

    public static MekanismGenerators instance;

    /**
     * MekanismGenerators version number
     */
    public final Version versionNumber;
    /**
     * Mekanism Generators Packet Pipeline
     */
    private final GeneratorsPacketHandler packetHandler;

    public static final MultiblockManager<TurbineMultiblockData> turbineManager = new MultiblockManager<>("industrialTurbine", TurbineCache::new, TurbineValidator::new);
    public static final MultiblockManager<FissionReactorMultiblockData> fissionReactorManager = new MultiblockManager<>("fissionReactor", FissionReactorCache::new, FissionReactorValidator::new);
    public static final MultiblockManager<FusionReactorMultiblockData> fusionReactorManager = new MultiblockManager<>("fusionReactor", FusionReactorCache::new, FusionReactorValidator::new);

    public MekanismGenerators() {
        Mekanism.addModule(instance = this);
        //Set our version number to match the neoforge.mods.toml file, which matches the one in our build.gradle
        ModContainer modContainer = FabricLoader.getInstance().getModContainer(MODID).orElseThrow();
        versionNumber = new Version(modContainer);
        MekanismGeneratorsConfig.registerConfigs(modContainer);

        packetHandler = new GeneratorsPacketHandler(versionNumber);
    }

    @Override
    public void onInitialize() {
        this.commonSetup();
        ModConfigEvent.Loading.EVENT.register(MekanismGeneratorsConfig::onConfigLoad);
        this.imcQueue();

        GeneratorsDataComponents.DATA_COMPONENTS.register();
        GeneratorsItems.ITEMS.register();
        GeneratorsBlocks.BLOCKS.register();
        GeneratorsFluids.FLUIDS.register();
        GeneratorsCreativeTabs.CREATIVE_TABS.register();
        GeneratorsSounds.SOUND_EVENTS.register();
        GeneratorsContainerTypes.CONTAINER_TYPES.register();
        GeneratorsTileEntityTypes.TILE_ENTITY_TYPES.register();
        GeneratorsChemicals.CHEMICALS.register();
        GeneratorsModules.MODULES.register();
    }

    public static GeneratorsPacketHandler packetHandler() {
        return instance.packetHandler;
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void commonSetup() {
        /*event.enqueueWork(() ->*/ {
            //Register dispenser behaviors
            GeneratorsFluids.FLUIDS.registerBucketDispenserBehavior();
            //Register extended build commands (in enqueue as it is not thread safe)
            BuildCommand.register("turbine", GeneratorsLang.TURBINE, new TurbineBuilder());
            BuildCommand.register("fission", GeneratorsLang.FISSION_REACTOR, new FissionReactorBuilder());
            BuildCommand.register("fusion", GeneratorsLang.FUSION_REACTOR, new FusionReactorBuilder());

            ClearConfigurationRecipe.addAttachments(GeneratorsDataComponents.FISSION_LOGIC_TYPE, GeneratorsDataComponents.FUSION_LOGIC_TYPE, GeneratorsDataComponents.ACTIVE_COOLED);
        }//);

        //Finalization
        Mekanism.logger.info("Loaded 'Mekanism: Generators' module.");
    }

    private void imcQueue() {
        MekanismIMC.addMekaSuitHelmetModules(GeneratorsModules.SOLAR_RECHARGING_UNIT);
        MekanismIMC.addMekaSuitPantsModules(GeneratorsModules.GEOTHERMAL_GENERATOR_UNIT);
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "Generators";
    }

    @Override
    public void resetClientDimensionChanged() {
        TurbineMultiblockData.clientRotationMap.clear();
    }
}
