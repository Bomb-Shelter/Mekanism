package mekanism.common.capabilities;

import mekanism.api.IAlloyInteraction;
import mekanism.api.IConfigCardAccess;
import mekanism.api.IConfigurable;
import mekanism.api.IEvaporationSolar;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.fabric.lookup.ICapabilityProvider;
import mekanism.api.fabric.transfer.fluids.IFluidHandler;
import mekanism.api.fabric.transfer.items.IItemHandler;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.lasers.ILaserDissipation;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.api.radiation.capability.IRadiationEntity;
import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.api.security.IBlockSecurityUtils;
import mekanism.api.security.IEntitySecurityUtils;
import mekanism.common.Mekanism;
import mekanism.common.entity.EntityRobit;
import mekanism.common.integration.computer.ComputerCapabilityHelper;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.lib.radiation.capability.RadiationEntity;
import mekanism.common.registries.MekanismEntityTypes;
import mekanism.common.tile.TileEntityBoundingBlock;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class Capabilities {

    private Capabilities() {
    }

    public static final ICapabilityProvider<?, ?, ?> SIMPLE_PROVIDER = (obj, context) -> obj;

    private record FluidCapability(BlockApiLookup<IFluidHandler, @Nullable Direction> block,
                                   ItemApiLookup<IFluidHandlerItem, Void> item,
                                   EntityApiLookup<IFluidHandler, @Nullable Direction> entity) implements IMultiTypeCapability<IFluidHandler, IFluidHandlerItem> {
    }

    public static final MultiTypeCapability<IEnergyStorage> ENERGY = new MultiTypeCapability<>(EnergyStorage.BLOCK, EnergyStorage.ITEM, EnergyStorage.ENTITY);
    public static final IMultiTypeCapability<IFluidHandler, IFluidHandlerItem> FLUID = new FluidCapability(FluidHandler.BLOCK, FluidHandler.ITEM, FluidHandler.ENTITY);
    //Note: We intentionally don't use the entity automation capability, as we want to be able to target player inventories and the like
    public static final MultiTypeCapability<IItemHandler> ITEM = new MultiTypeCapability<>(ItemHandler.BLOCK, ItemHandler.ITEM, ItemHandler.ENTITY);

    public static final MultiTypeCapability<IChemicalHandler> CHEMICAL = new MultiTypeCapability<>(Mekanism.rl("chemical_handler"), IChemicalHandler.class);

    public static final BlockApiLookup<IHeatHandler, @Nullable Direction> HEAT = BlockApiLookup.get(Mekanism.rl("heat_handler"), IHeatHandler.class, Direction.class);

    public static final MultiTypeCapability<IStrictEnergyHandler> STRICT_ENERGY = new MultiTypeCapability<>(Mekanism.rl("strict_energy_handler"), IStrictEnergyHandler.class);

    public static final BlockApiLookup<IConfigurable, @Nullable Direction> CONFIGURABLE = BlockApiLookup.get(Mekanism.rl("configurable"), IConfigurable.class, Direction.class);

    public static final BlockApiLookup<IAlloyInteraction, @Nullable Direction> ALLOY_INTERACTION = BlockApiLookup.get(Mekanism.rl("alloy_interaction"), IAlloyInteraction.class, Direction.class);

    public static final BlockApiLookup<IConfigCardAccess, @Nullable Direction> CONFIG_CARD = BlockApiLookup.get(Mekanism.rl("config_card"), IConfigCardAccess.class, Direction.class);

    public static final BlockApiLookup<IEvaporationSolar, Void> EVAPORATION_SOLAR = BlockApiLookup.get(Mekanism.rl("evaporation_solar"), IEvaporationSolar.class, void.class);

    public static final BlockApiLookup<ILaserReceptor, @Nullable Direction> LASER_RECEPTOR = BlockApiLookup.get(Mekanism.rl("laser_receptor"), ILaserReceptor.class, Direction.class);

    public static final ItemApiLookup<ILaserDissipation, Void> LASER_DISSIPATION = ItemApiLookup.get(Mekanism.rl("laser_dissipation"), ILaserDissipation.class, void.class);

    public static final ItemApiLookup<IRadiationShielding, Void> RADIATION_SHIELDING = ItemApiLookup.get(Mekanism.rl("radiation_shielding"), IRadiationShielding.class, void.class);

    public static final EntityApiLookup<IRadiationEntity, Void> RADIATION_ENTITY = EntityApiLookup.get(Mekanism.rl("radiation"), IRadiationEntity.class, void.class);

    public static final ResourceLocation OWNER_OBJECT_NAME = Mekanism.rl("owner_object");
    public static final ResourceLocation SECURITY_OBJECT_NAME = Mekanism.rl("security_object");

    public static void registerProxyableCapabilities(RegisterCapabilitiesEvent event) {
        event.setProxyable(CHEMICAL.block());
        event.setProxyable(STRICT_ENERGY.block());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        Mekanism.hooks.hookCapabilityRegistration(event);

        EntityType<EntityRobit> robitEntityType = MekanismEntityTypes.ROBIT.get();
        event.registerEntity(IEntitySecurityUtils.INSTANCE.ownerCapability(), robitEntityType, (robit, ctx) -> robit);
        event.registerEntity(IEntitySecurityUtils.INSTANCE.securityCapability(), robitEntityType, (robit, ctx) -> robit);
        EnergyCompatUtils.registerEntityCapabilities(event, robitEntityType, (robit, ctx) -> robit);

        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
            //Note: The jvm will reuse the lambda between types
            event.registerEntity(RADIATION_ENTITY, entityType, (entity, ctx) -> entity instanceof LivingEntity living ? new RadiationEntity(living) : null);
        }

        //Register bounding block proxies
        TileEntityBoundingBlock.alwaysProxyCapability(event, CONFIG_CARD);
        TileEntityBoundingBlock.alwaysProxyCapability(event, IBlockSecurityUtils.INSTANCE.ownerCapability());
        TileEntityBoundingBlock.alwaysProxyCapability(event, IBlockSecurityUtils.INSTANCE.securityCapability());
        //Capabilities we need to proxy because some sub implementations use them
        ComputerCapabilityHelper.addBoundingComputerCapabilities(event);
        TileEntityBoundingBlock.proxyCapability(event, ITEM.block());
        for (BlockCapability<?, @Nullable Direction> capability : EnergyCompatUtils.getLoadedEnergyCapabilities()) {
            TileEntityBoundingBlock.proxyCapability(event, capability);
        }
        //Note: Common caps we may eventually want to proxy but currently have no use for doing so
        TileEntityBoundingBlock.proxyCapability(event, FluidHandler.BLOCK);
        TileEntityBoundingBlock.proxyCapability(event, CHEMICAL.block());
        TileEntityBoundingBlock.proxyCapability(event, HEAT);
    }
}