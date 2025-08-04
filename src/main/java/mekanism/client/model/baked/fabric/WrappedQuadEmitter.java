package mekanism.client.model.baked.fabric;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class WrappedQuadEmitter implements QuadEmitter {
    private final QuadEmitter wrapped;

    public WrappedQuadEmitter(QuadEmitter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public QuadEmitter pos(int vertexIndex, float x, float y, float z) {
        wrapped.pos(vertexIndex, x, y, z);
        return this;
    }

    @Override
    public QuadEmitter color(int vertexIndex, int color) {
        wrapped.color(vertexIndex, color);
        return this;
    }

    @Override
    public QuadEmitter uv(int vertexIndex, float u, float v) {
        wrapped.uv(vertexIndex, u, v);
        return this;
    }

    @Override
    public QuadEmitter spriteBake(TextureAtlasSprite sprite, int bakeFlags) {
        wrapped.spriteBake(sprite, bakeFlags);
        return this;
    }

    @Override
    public QuadEmitter lightmap(int vertexIndex, int lightmap) {
        wrapped.lightmap(vertexIndex, lightmap);
        return this;
    }

    @Override
    public QuadEmitter normal(int vertexIndex, float x, float y, float z) {
        wrapped.normal(vertexIndex, x, y, z);
        return this;
    }

    @Override
    public QuadEmitter cullFace(@Nullable Direction face) {
        wrapped.cullFace(face);
        return this;
    }

    @Override
    public QuadEmitter nominalFace(@Nullable Direction face) {
        wrapped.nominalFace(face);
        return this;
    }

    @Override
    public QuadEmitter material(RenderMaterial material) {
        wrapped.material(material);
        return this;
    }

    @Override
    public QuadEmitter colorIndex(int colorIndex) {
        wrapped.colorIndex(colorIndex);
        return this;
    }

    @Override
    public QuadEmitter tag(int tag) {
        wrapped.tag(tag);
        return this;
    }

    @Override
    public QuadEmitter copyFrom(QuadView quad) {
        wrapped.copyFrom(quad);
        return this;
    }

    @Override
    public QuadEmitter fromVanilla(int[] quadData, int startIndex) {
        wrapped.fromVanilla(quadData, startIndex);
        return this;
    }

    @Override
    public QuadEmitter fromVanilla(BakedQuad quad, RenderMaterial material, @Nullable Direction cullFace) {
        wrapped.fromVanilla(quad, material, cullFace);
        return this;
    }

    @Override
    public QuadEmitter emit() {
        wrapped.emit();
        return this;
    }

    @Override
    public float x(int vertexIndex) {
        return wrapped.x(vertexIndex);
    }

    @Override
    public float y(int vertexIndex) {
        return wrapped.y(vertexIndex);
    }

    @Override
    public float z(int vertexIndex) {
        return wrapped.z(vertexIndex);
    }

    @Override
    public float posByIndex(int vertexIndex, int coordinateIndex) {
        return wrapped.posByIndex(vertexIndex, coordinateIndex);
    }

    @Override
    public Vector3f copyPos(int vertexIndex, @Nullable Vector3f target) {
        return wrapped.copyPos(vertexIndex, target);
    }

    @Override
    public int color(int vertexIndex) {
        return wrapped.color(vertexIndex);
    }

    @Override
    public float u(int vertexIndex) {
        return wrapped.u(vertexIndex);
    }

    @Override
    public float v(int vertexIndex) {
        return wrapped.v(vertexIndex);
    }

    @Override
    public Vector2f copyUv(int vertexIndex, @Nullable Vector2f target) {
        return wrapped.copyUv(vertexIndex, target);
    }

    @Override
    public int lightmap(int vertexIndex) {
        return wrapped.lightmap(vertexIndex);
    }

    @Override
    public boolean hasNormal(int vertexIndex) {
        return wrapped.hasNormal(vertexIndex);
    }

    @Override
    public float normalX(int vertexIndex) {
        return wrapped.normalX(vertexIndex);
    }

    @Override
    public float normalY(int vertexIndex) {
        return wrapped.normalY(vertexIndex);
    }

    @Override
    public float normalZ(int vertexIndex) {
        return wrapped.normalZ(vertexIndex);
    }

    @Nullable
    @Override
    public Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target) {
        return wrapped.copyNormal(vertexIndex, target);
    }

    @Nullable
    @Override
    public  Direction cullFace() {
        return wrapped.cullFace();
    }

    @Override
    public Direction lightFace() {
        return wrapped.lightFace();
    }

    @Override
    public @Nullable Direction nominalFace() {
        return wrapped.nominalFace();
    }

    @Override
    public Vector3f faceNormal() {
        return wrapped.faceNormal();
    }

    @Override
    public RenderMaterial material() {
        return wrapped.material();
    }

    @Override
    public int colorIndex() {
        return wrapped.colorIndex();
    }

    @Override
    public int tag() {
        return wrapped.tag();
    }

    @Override
    public void toVanilla(int[] target, int targetIndex) {
        wrapped.toVanilla(target, targetIndex);
    }
}
