package mekanism.client.model.baked.fabric;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class QuadEmittingRenderContext implements RenderContext {
    private static final RenderContext.QuadTransform NO_TRANSFORM = q -> true;

    private final QuadEmitter emitter;
    private final BakedModel model;
    private final RandomSource random;
    private RenderContext.QuadTransform activeTransform = NO_TRANSFORM;
    private final ObjectArrayList<QuadTransform> transformStack = new ObjectArrayList<>();
    private final RenderContext.QuadTransform stackTransform = q -> {
        int i = transformStack.size() - 1;

        while (i >= 0) {
            if (!transformStack.get(i--).transform(q)) {
                return false;
            }
        }

        return true;
    };

    public QuadEmittingRenderContext(QuadEmitter emitter, BakedModel model, RandomSource rand) {
        this.emitter = new WrappedQuadEmitter(emitter) {
            @Override
            public QuadEmitter emit() {
                activeTransform.transform(this);
                return super.emit();
            }
        };
        this.model = model;
        this.random = rand;
    }

    public void emit() {

    }

    @Override
    public QuadEmitter getEmitter() {
        return emitter;
    }

    @Override
    public void pushTransform(RenderContext.QuadTransform transform) {
        if (transform == null) {
            throw new NullPointerException("Renderer received null QuadTransform.");
        }

        transformStack.push(transform);

        if (transformStack.size() == 1) {
            activeTransform = transform;
        } else if (transformStack.size() == 2) {
            activeTransform = stackTransform;
        }
    }

    @Override
    public void popTransform() {
        transformStack.pop();

        if (transformStack.size() == 0) {
            activeTransform = NO_TRANSFORM;
        } else if (transformStack.size() == 1) {
            activeTransform = transformStack.get(0);
        }
    }

    @Override
    public BakedModelConsumer bakedModelConsumer() {
        return new BakedModelConsumer() {
            @Override
            public void accept(BakedModel model) {
                model.emitBlockQuads(null, null, null, () -> rand, QuadEmittingRenderContext.this);
            }

            @Override
            public void accept(BakedModel model, @Nullable BlockState state) {
                model.emitBlockQuads(null, state, null, () -> rand, QuadEmittingRenderContext.this);
            }
        };
    }
}