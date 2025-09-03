package mekanism.common.registries;

import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.registry.DeferredRegister;
import mekanism.common.Mekanism;
import mekanism.common.particle.LaserParticleType;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

public class MekanismParticleTypes {

    private MekanismParticleTypes() {
    }

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Mekanism.MODID);

    public static final DeferredHolder<ParticleType<?>, LaserParticleType> LASER = PARTICLE_TYPES.register("laser", LaserParticleType::new);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> JETPACK_FLAME = PARTICLE_TYPES.register("jetpack_flame", () -> FabricParticleTypes.simple(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> JETPACK_SMOKE = PARTICLE_TYPES.register("jetpack_smoke", () -> FabricParticleTypes.simple(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SCUBA_BUBBLE = PARTICLE_TYPES.register("scuba_bubble", () -> FabricParticleTypes.simple(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RADIATION = PARTICLE_TYPES.register("radiation", () -> FabricParticleTypes.simple(false));
}