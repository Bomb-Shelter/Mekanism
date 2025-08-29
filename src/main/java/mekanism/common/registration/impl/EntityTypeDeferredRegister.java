package mekanism.common.registration.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import mekanism.common.Mekanism;
import mekanism.common.registration.MekanismDeferredHolder;
import mekanism.common.registration.MekanismDeferredRegister;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

public class EntityTypeDeferredRegister extends MekanismDeferredRegister<EntityType<?>> {

    private Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>> livingEntityAttributes = new HashMap<>();
    private Map<Supplier<? extends EntityType<? extends LivingEntity>>, SpawnPlacementData<?>> livingEntityPlacements = new HashMap<>();

    public EntityTypeDeferredRegister(String modid) {
        super(Registries.ENTITY_TYPE, modid);
    }

    public <ENTITY extends Monster> MekanismDeferredHolder<EntityType<?>, EntityType<ENTITY>> registerBasicMonster(String name,
          Supplier<EntityType.Builder<ENTITY>> builder, Supplier<AttributeSupplier.Builder> attributes) {
        return registerBasicPlacement(name, builder, attributes, Monster::checkMonsterSpawnRules);
    }

    public <ENTITY extends LivingEntity> MekanismDeferredHolder<EntityType<?>, EntityType<ENTITY>> registerBasicPlacement(String name,
          Supplier<EntityType.Builder<ENTITY>> builder, Supplier<AttributeSupplier.Builder> attributes, SpawnPlacements.SpawnPredicate<ENTITY> placementPredicate) {
        return register(name, builder, attributes, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, placementPredicate);
    }

    public <ENTITY extends LivingEntity> MekanismDeferredHolder<EntityType<?>, EntityType<ENTITY>> register(String name, Supplier<EntityType.Builder<ENTITY>> builder,
          Supplier<AttributeSupplier.Builder> attributes, @Nullable SpawnPlacementType placementType, @Nullable Heightmap.Types heightmap,
          SpawnPlacements.SpawnPredicate<ENTITY> placementPredicate) {
        MekanismDeferredHolder<EntityType<?>, EntityType<ENTITY>> entityTypeRO = register(name, builder, attributes);
        livingEntityPlacements.put(entityTypeRO, new SpawnPlacementData<>(placementType, heightmap, placementPredicate));
        return entityTypeRO;
    }

    public <ENTITY extends LivingEntity> MekanismDeferredHolder<EntityType<?>, EntityType<ENTITY>> register(String name, Supplier<EntityType.Builder<ENTITY>> builder,
          Supplier<AttributeSupplier.Builder> attributes) {
        MekanismDeferredHolder<EntityType<?>, EntityType<ENTITY>> entityTypeRO = registerBuilder(name, builder);
        livingEntityAttributes.put(entityTypeRO, attributes);
        return entityTypeRO;
    }

    public <ENTITY extends Entity> MekanismDeferredHolder<EntityType<?>, EntityType<ENTITY>> registerBuilder(String name, Supplier<EntityType.Builder<ENTITY>> builder) {
        return register(name, () -> builder.get().build(name));
    }

    @Override
    public void register() {
        super.register();
        registerEntityAttributes();
        registerPlacements();
    }

    private void registerEntityAttributes() {
        if (livingEntityAttributes == null) {
            Mekanism.logger.error("Entity Attributes have already been created. This should not happen.");
        } else {
            //Register our living entity attributes
            for (Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>> entry : livingEntityAttributes.entrySet()) {
                FabricDefaultAttributeRegistry.register(entry.getKey().get(), entry.getValue().get());
            }
            //And set the map to null to allow it to be garbage collected
            livingEntityAttributes = null;
        }
    }

    private void registerPlacements() {
        if (livingEntityPlacements == null) {
            Mekanism.logger.error("Entity Placements have already been set. This should not happen.");
        } else {
            //Register our living entity placements
            for (Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, SpawnPlacementData<?>> entry : livingEntityPlacements.entrySet()) {
                entry.getValue().register(entry.getKey().get());
            }
            //And set the map to null to allow it to be garbage collected
            livingEntityPlacements = null;
        }
    }

    private record SpawnPlacementData<ENTITY extends LivingEntity>(@Nullable SpawnPlacementType placementType, @Nullable Heightmap.Types heightmap,
                                                                  SpawnPlacements.SpawnPredicate<ENTITY> predicate/*,
                                                                   RegisterSpawnPlacementsEvent.Operation operation*/) {

        private void register(EntityType<?> entityType) {
            SpawnPlacements.register((EntityType) entityType, placementType, heightmap, (SpawnPlacements.SpawnPredicate<? extends Mob>) predicate);
        }
    }
}