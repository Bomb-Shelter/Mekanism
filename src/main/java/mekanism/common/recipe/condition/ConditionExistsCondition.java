package mekanism.common.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.SerializationConstants;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

public record ConditionExistsCondition(@Nullable ResourceCondition condition) implements ResourceCondition {

    private static final ConditionExistsCondition DOES_NOT_EXIST = new ConditionExistsCondition(null);

    @Override
    public boolean test(@Nullable HolderLookup.Provider registryLookup) {
        return condition != null && condition.test(registryLookup);
    }

    @Override
    public String toString() {
        return "condition_exists(" + condition + ")";
    }

    @Override
    public ResourceConditionType<? extends ResourceCondition> getType() {
        return MekanismRecipeConditions.CONDITION_EXISTS;
    }

    public static MapCodec<ConditionExistsCondition> makeCodec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
              ResourceCondition.CODEC.fieldOf(SerializationConstants.CONDITION).orElse(DOES_NOT_EXIST).forGetter(ConditionExistsCondition::condition)
        ).apply(instance, ConditionExistsCondition::new));
    }
}