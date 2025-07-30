package mekanism.common.attachments.containers.creator;

import io.github.fabricators_of_create.porting_lib.core.util.INBTSerializable;
import mekanism.common.attachments.containers.IAttachedContainers;
import net.minecraft.nbt.CompoundTag;

public interface IContainerCreator<CONTAINER extends INBTSerializable<CompoundTag>, ATTACHED extends IAttachedContainers<?, ATTACHED>> extends IBasicContainerCreator<CONTAINER> {

    int totalContainers();

    ATTACHED initStorage(int containers);
}