package mekanism.common.fabric;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomSoundTypeBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.PlayerDestroyBlock;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class FabricUtil {
    public static int getBurnTime(ItemStack stack) {
        var value = FuelRegistry.INSTANCE.get(stack.getItem());

        if (value == null)
            return 0;

        return value;
    }

    public static SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        if (state.getBlock() instanceof CustomSoundTypeBlock soundTypeBlock)
            return soundTypeBlock.getSoundType(state, level, pos, entity);
        else
            return state.getSoundType();
    }
}
