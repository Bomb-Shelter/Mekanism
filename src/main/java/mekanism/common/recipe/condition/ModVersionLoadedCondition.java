package mekanism.common.recipe.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import mekanism.api.SerializationConstants;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

public record ModVersionLoadedCondition(String modid, Version minVersion) implements ResourceCondition {
    private static final Codec<Version> VERSION_CODEC = Codec.STRING.flatXmap(s -> {
        try {
            return DataResult.success(Version.parse(s));
        } catch (VersionParsingException e) {
            return DataResult.error(e::getMessage);
        }
    }, o -> DataResult.success(o.getFriendlyString()));

    @Override
    public boolean test(@Nullable HolderLookup.Provider registryLookup) {
        //They match or we are ahead of the min version
        Optional<? extends ModContainer> containerById = FabricLoader.getInstance().getModContainer(modid);
        if (containerById.isEmpty()) {
            return false;
        }
        ModContainer modContainer = containerById.get();
        return minVersion.compareTo(modContainer.getMetadata().getVersion()) <= 0;
    }

    @Override
    public ResourceConditionType<? extends ResourceCondition> getType() {
        return MekanismRecipeConditions.MOD_VERSION_LOADED;
    }

    public static MapCodec<ModVersionLoadedCondition> makeCodec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
              Codec.STRING.fieldOf(SerializationConstants.MODID).forGetter(ModVersionLoadedCondition::modid),
              VERSION_CODEC.fieldOf(SerializationConstants.VERSION).forGetter(ModVersionLoadedCondition::minVersion)
        ).apply(instance, ModVersionLoadedCondition::new));
    }
}