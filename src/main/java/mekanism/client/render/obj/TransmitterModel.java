package mekanism.client.render.obj;

import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.models.obj.ObjModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class TransmitterModel implements IUnbakedGeometry<TransmitterModel> {

    private final ObjModel internal;
    @Nullable
    private final ObjModel glass;

    public TransmitterModel(ObjModel internalModel, @Nullable ObjModel glass) {
        this.internal = internalModel;
        this.glass = glass;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                           ItemOverrides overrides) {
        return new TransmitterBakedModel(internal, glass, owner, baker, spriteGetter, modelTransform, overrides);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        internal.resolveParents(modelGetter, context);
        if (glass != null) {
            glass.resolveParents(modelGetter, context);
        }
    }
}