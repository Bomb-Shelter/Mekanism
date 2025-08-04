package mekanism.common.registries;

import mekanism.common.Mekanism;
import mekanism.common.world.DisableableFeaturePlacement;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.registry.DeferredRegister;

public class MekanismPlacementModifiers {

    private MekanismPlacementModifiers() {
    }

    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, Mekanism.MODID);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<DisableableFeaturePlacement>> DISABLEABLE = PLACEMENT_MODIFIERS.register("disableable", () -> () -> DisableableFeaturePlacement.CODEC);
}