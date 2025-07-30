package mekanism.common.attachments.containers.creator;

import io.github.fabricators_of_create.porting_lib.core.util.INBTSerializable;
import mekanism.common.attachments.containers.ContainerType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface IBasicContainerCreator<CONTAINER extends INBTSerializable<CompoundTag>> {

    CONTAINER create(ContainerType<? super CONTAINER, ?, ?> containerType, ItemStack attachedTo, int containerIndex);
}