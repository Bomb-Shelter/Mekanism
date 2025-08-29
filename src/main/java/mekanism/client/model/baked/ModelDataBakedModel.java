package mekanism.client.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;

import io.github.fabricators_of_create.porting_lib.models.TransformTypeDependentItemBakedModel;
import mekanism.api.annotations.NothingNullByDefault;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

@NothingNullByDefault
public class ModelDataBakedModel extends ForwardingBakedModel implements TransformTypeDependentItemBakedModel {

    private final Object modelData;
    private final List<BakedModel> renderPasses;

    public ModelDataBakedModel(BakedModel original, Object data) {
        super(original);
        this.modelData = data;
        this.renderPasses = Collections.singletonList(this);
    }

//    @Override
//    @Deprecated
//    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
//        return getQuads(state, side, rand, modelData, null);
//    }

//    @Override
//    @Deprecated
//    public TextureAtlasSprite getParticleIcon() {
//        return getParticleIcon(modelData);
//    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext displayContext, PoseStack mat, boolean applyLeftHandTransform, DefaultTransform defaultTransform) {
        // have the original model apply any perspective transforms onto the MatrixStack
        defaultTransform.apply(this.getWrappedModel());
        // return this model, as we want to draw the item variant quads ourselves
        return this;
    }

//    @Override
//    public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) {
//        //Make sure our model is the one that gets rendered rather than the internal one
//        return renderPasses;
//    }
}