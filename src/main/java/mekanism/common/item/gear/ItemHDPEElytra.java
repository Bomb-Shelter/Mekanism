package mekanism.common.item.gear;

import io.github.fabricators_of_create.porting_lib.item.extensions.EquipmentItem;
import mekanism.common.registries.MekanismItems;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemHDPEElytra extends ElytraItem implements EquipmentItem, FabricElytraItem {

    public ItemHDPEElytra(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.is(MekanismItems.HDPE_SHEET);
    }
}