package mekanism.client.render.lib;

import java.util.ArrayList;
import java.util.List;
import mekanism.api.functions.ToFloatFunction;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;

public class QuadUtils {

    private QuadUtils() {
    }

    private static final float eps = 1F / 0x100;

    public static List<Quad> unpack(List<BakedQuad> quads) {
        return quads.stream().map(Quad::new).toList();
    }

    public static List<BakedQuad> bake(List<Quad> quads) {
        return quads.stream().map(Quad::bake).toList();
    }

    public static List<Quad> flip(List<Quad> quads) {
        return quads.stream().map(Quad::flip).toList();
    }

    public static List<Quad> transformQuads(List<Quad> orig, QuadTransformation transformation) {
        List<Quad> list = new ArrayList<>(orig.size());
        for (Quad quad : orig) {
            transformation.transform(quad);
            list.add(quad);
        }
        return list;
    }

    public static List<BakedQuad> transformBakedQuads(List<BakedQuad> orig, QuadTransformation transformation) {
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder meshBuilder = renderer.meshBuilder();
        QuadEmitter emitter = meshBuilder.getEmitter();
        for (BakedQuad bakedQuad : orig) {
            Quad quad = new Quad(bakedQuad);
            transformation.transform(quad.bake(emitter, null));
            emitter.emit();
        }
        return ModelHelper.toQuadLists(meshBuilder.build())[ModelHelper.NULL_FACE_ID];
    }

    public static List<BakedQuad> transformAndBake(List<Quad> orig, QuadTransformation transformation) {
        List<BakedQuad> list = new ArrayList<>(orig.size());
        for (Quad quad : orig) {
            transformation.transform(quad);
            list.add(quad.bake());
        }
        return list;
    }

    public static boolean isSameSprite(MutableQuadView quad, TextureAtlasSprite sprite) {
        SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)).find(quad);
    }

    public static void remapUVs(MutableQuadView quad, TextureAtlasSprite newTexture) {
        TextureAtlasSprite texture = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)).find(quad);
        float uMin = texture.getU0(), uMax = texture.getU1();
        float vMin = texture.getV0(), vMax = texture.getV1();
        for (int i = 0; i < 4; i++) {
            float newU = (quad.u(i) - uMin) / (uMax - uMin);
            float newV = (quad.v(i) - vMin) / (vMax - vMin);
            quad.uv(i, newTexture.getU(newU), newTexture.getV(newV));
        }
    }

    // this is an adaptation of fry's original UV contractor (pulled from BakedQuadBuilder).
    // ultimately this fixes UVs bleeding over the edge slightly when dealing with smaller models or tight UV bounds
    public static void contractUVs(Quad quad) {
        TextureAtlasSprite texture = quad.getTexture();
        float sizeX = texture.contents().width() / (texture.getU1() - texture.getU0());
        float sizeY = texture.contents().height() / (texture.getV1() - texture.getV0());
        float ep = 1F / (Math.max(sizeX, sizeY) * 0x100);
        float[] newUs = contract(quad, Vertex::getTexU, ep);
        float[] newVs = contract(quad, Vertex::getTexV, ep);
        for (int i = 0; i < quad.getVertices().length; i++) {
            quad.getVertices()[i].texRaw(newUs[i], newVs[i]);
        }
    }

    private static float[] contract(Quad quad, ToFloatFunction<Vertex> uvf, float ep) {
        float center = 0;
        float[] ret = new float[4];
        for (int v = 0; v < 4; v++) {
            center += uvf.applyAsFloat(quad.getVertices()[v]);
        }
        center /= 4;
        for (int v = 0; v < 4; v++) {
            float orig = uvf.applyAsFloat(quad.getVertices()[v]);
            float shifted = orig * (1 - eps) + center * eps;
            float delta = orig - shifted;
            if (Math.abs(delta) < ep) { // not moving a fraction of a pixel
                float centerDelta = Math.abs(orig - center);
                if (centerDelta < 2 * ep) { // center is closer than 2 fractions of a pixel, don't move too close
                    shifted = (orig + center) / 2;
                } else { // move at least by a fraction
                    shifted = orig + (delta < 0 ? ep : -ep);
                }
            }
            ret[v] = shifted;
        }
        return ret;
    }
}
