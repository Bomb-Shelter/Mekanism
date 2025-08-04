package mekanism.common.recipe.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import mekanism.api.SerializationConstants;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.HolderLookup;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.Nullable;

public record ModVersionLoadedCondition(String modid, String minVersion) implements ResourceCondition {

    @Override
    public boolean test(@Nullable HolderLookup.Provider registryLookup) {
        //They match or we are ahead of the min version
        Optional<? extends ModContainer> containerById = FabricLoader.getInstance().getModContainer(modid);
        if (containerById.isEmpty()) {
            return false;
        }
        ModContainer modContainer = containerById.get();
        return new ComparableVersion(minVersion).compareTo(new ComparableVersion(modContainer.getMetadata().getVersion().toString())) <= 0;
    }

    @Override
    public ResourceConditionType<? extends ResourceCondition> getType() {
        return MekanismRecipeConditions.MOD_VERSION_LOADED;
    }

    public static MapCodec<ModVersionLoadedCondition> makeCodec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
              Codec.STRING.fieldOf(SerializationConstants.MODID).forGetter(ModVersionLoadedCondition::modid),
              Codec.STRING.fieldOf(SerializationConstants.VERSION).forGetter(ModVersionLoadedCondition::minVersion)
        ).apply(instance, ModVersionLoadedCondition::new));
    }
}