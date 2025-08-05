package mekanism.client.render.item.gear;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mekanism.client.model.ModelArmoredJetpack;
import mekanism.client.model.ModelJetpack;
import mekanism.client.render.item.MekanismISTER;
import mekanism.common.Mekanism;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderJetpack extends MekanismISTER {

    public static final RenderJetpack RENDERER = new RenderJetpack(Mekanism.rl("render_jetpack"), false);
    public static final RenderJetpack ARMORED_RENDERER = new RenderJetpack(Mekanism.rl("render_armored_jetpack"), true);

    private final boolean armored;
    private ModelJetpack jetpack;

    private RenderJetpack(ResourceLocation id, boolean armored) {
        super(id);
        this.armored = armored;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        if (armored) {
            jetpack = new ModelArmoredJetpack(getEntityModels());
        } else {
            jetpack = new ModelJetpack(getEntityModels());
        }
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
          int light, int overlayLight) {
        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.mulPose(Axis.ZP.rotationDegrees(180));
        jetpack.render(matrix, renderer, light, overlayLight, stack.hasFoil());
        matrix.popPose();
    }
}