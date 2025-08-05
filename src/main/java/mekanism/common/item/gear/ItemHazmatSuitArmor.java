package mekanism.common.item.gear;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import io.github.fabricators_of_create.porting_lib.item.extensions.CustomSupportsEnchantItem;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.ICapabilityAware;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import mekanism.common.registries.MekanismArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class ItemHazmatSuitArmor extends ArmorItem implements ICapabilityAware, CustomEnchantingBehaviorItem, CustomSupportsEnchantItem {

    public ItemHazmatSuitArmor(Type armorType, Properties properties) {
        super(MekanismArmorMaterials.HAZMAT, armorType, properties.rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static double getShieldingByArmor(Type type) {
        return switch (type) {
            case HELMET -> 0.25;
            case CHESTPLATE -> 0.4;
            case LEGGINGS -> 0.2;
            case BOOTS -> 0.15;
            case BODY -> 0.0;
        };
    }

    @Override
    public void attachCapabilities() {
        Capabilities.RADIATION_SHIELDING.registerForItems((stack, ctx) -> RadiationShieldingHandler.create(getShieldingByArmor(getType())), this);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return material.value().enchantmentValue() > 0 && stack.getMaxStackSize() == 1;
    }

    @Override
    public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
        return isEnchantable(stack) && CustomEnchantingBehaviorItem.super.isBookEnchantable(stack, book);
    }

    @Override
    public boolean isPrimaryItemFor(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return isEnchantable(stack) && CustomSupportsEnchantItem.super.isPrimaryItemFor(stack, enchantment);
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return isEnchantable(stack) && CustomSupportsEnchantItem.super.supportsEnchantment(stack, enchantment);
    }
}
