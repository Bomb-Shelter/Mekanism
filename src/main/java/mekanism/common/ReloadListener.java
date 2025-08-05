package mekanism.common;

import mekanism.common.recipe.MekanismRecipeType;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class ReloadListener implements SimpleSynchronousResourceReloadListener {
    public static final ResourceLocation ID = Mekanism.rl("reload");

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        CommonWorldTickHandler.flushTagAndRecipeCaches = true;
        MekanismRecipeType.clearCache();
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}