package mekanism.common.lib.radiation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;

import mekanism.common.registries.MekanismAttachmentTypes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import io.github.fabricators_of_create.porting_lib.core.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MeltdownLevelData implements INBTSerializable<ListTag> {

    private final List<Meltdown> meltdowns = new ArrayList<>();

    public void createMeltdown(BlockPos minPos, BlockPos maxPos, double magnitude, double chance, float radius, UUID multiblockID) {
        meltdowns.add(new Meltdown(minPos, maxPos, magnitude, chance, radius, multiblockID));
    }

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(MeltdownLevelData::tickWorld);
    }

    public static void tickWorld(ServerLevel serverLevel) {
        MeltdownLevelData existingData = serverLevel.getAttached(MekanismAttachmentTypes.MELTDOWN_DATA);
        if (existingData != null) {
            existingData.tick(serverLevel);
        }
    }

    public void tick(ServerLevel world) {
        if (meltdowns.isEmpty()) {
            return;
        }
        //noinspection Java8CollectionRemoveIf - We can't replace it with removeIf as it has a capturing lambda
        for (Iterator<Meltdown> iterator = meltdowns.iterator(); iterator.hasNext(); ) {
            Meltdown meltdown = iterator.next();
            if (meltdown.update(world)) {
                iterator.remove();
            }
        }
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, ListTag meltdownList) {
        for (int i = 0; i < meltdownList.size(); i++) {
            Meltdown meltdown = Meltdown.load(meltdownList.getCompound(i));
            if (meltdown != null) {
                this.meltdowns.add(meltdown);
            }
        }
    }

    @Override
    @Nullable
    public ListTag serializeNBT(HolderLookup.Provider provider) {
        if (meltdowns.isEmpty()) {
            return null;
        }

        ListTag list = new ListTag();
        for (Meltdown meltdown : meltdowns) {
            list.add(meltdown.write());
        }
        return list;
    }

}
