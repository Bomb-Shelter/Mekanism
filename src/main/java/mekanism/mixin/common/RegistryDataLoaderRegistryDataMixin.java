package mekanism.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Lifecycle;
import mekanism.api.MekanismAPI;
import mekanism.common.registries.MekanismRobitSkins;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RegistryDataLoader.RegistryData.class)
public class RegistryDataLoaderRegistryDataMixin<T> {
    @WrapOperation(method = "create", at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/core/MappedRegistry;"))
    private MappedRegistry<T> makeRobitRegistryDefaulted(ResourceKey<? extends Registry<T>> resourceKey, Lifecycle lifecycle, Operation<MappedRegistry<T>> original) {
        if (resourceKey == MekanismAPI.ROBIT_SKIN_SERIALIZER_REGISTRY_NAME) {
            return new DefaultedMappedRegistry<>(MekanismRobitSkins.BASE.toString(), resourceKey, lifecycle, false);
        }
        return original.call(resourceKey, lifecycle);
    }
}
