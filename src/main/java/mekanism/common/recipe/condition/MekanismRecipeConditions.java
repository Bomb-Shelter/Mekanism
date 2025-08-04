package mekanism.common.recipe.condition;

import com.mojang.serialization.MapCodec;
import mekanism.common.Mekanism;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

import java.util.function.Supplier;

public class MekanismRecipeConditions {

    public static final ResourceConditionType<ConditionExistsCondition> CONDITION_EXISTS = register("condition_exists", ConditionExistsCondition::makeCodec);
    public static final ResourceConditionType<ModVersionLoadedCondition> MOD_VERSION_LOADED = register("mod_version_loaded", ModVersionLoadedCondition::makeCodec);

    private static <T extends ResourceCondition> ResourceConditionType<T> register(String name, Supplier<MapCodec<T>> codec) {
        return ResourceConditionType.create(Mekanism.rl(name), codec.get());
    }

    public static void register() {
        ResourceConditions.register(CONDITION_EXISTS);
    }
}
