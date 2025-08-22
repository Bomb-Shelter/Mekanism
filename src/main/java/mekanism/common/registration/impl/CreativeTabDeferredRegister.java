package mekanism.common.registration.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import io.github.fabricators_of_create.porting_lib.item.itemgroup.PortingLibCreativeTab;
import mekanism.api.functions.ConstantPredicates;
import mekanism.api.text.ILangEntry;
import mekanism.client.SpecialColors;
import mekanism.common.registration.MekanismDeferredHolder;
import mekanism.common.registration.MekanismDeferredRegister;
import mekanism.common.registries.MekanismBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CreativeTabDeferredRegister extends MekanismDeferredRegister<CreativeModeTab> {

    private final BiConsumer<ResourceKey<CreativeModeTab>, FabricItemGroupEntries> addToExistingTabs;

    public CreativeTabDeferredRegister(String modid) {
        this(modid, (key, event) -> {
        });
    }

    public CreativeTabDeferredRegister(String modid, BiConsumer<ResourceKey<CreativeModeTab>, FabricItemGroupEntries> addToExistingTabs) {
        super(Registries.CREATIVE_MODE_TAB, modid);
        this.addToExistingTabs = addToExistingTabs;
    }

    @Override
    public void register() {
        super.register();
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> {
            addToExistingTabs.accept(BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(group).orElseThrow(), entries);
        });
        //bus.addListener(addToExistingTabs);
    }

    /**
     * @apiNote We manually require the title and icon to be passed so that we ensure all tabs have one.
     */
    public MekanismDeferredHolder<CreativeModeTab, CreativeModeTab> registerMain(ILangEntry title, Holder<Item> icon, UnaryOperator<PortingLibCreativeTab.PortingLibCreativeTabBuilder> operator) {
        return register(getNamespace(), title, icon, operator);
    }

    /**
     * @apiNote We manually require the title and icon to be passed so that we ensure all tabs have one.
     */
    public MekanismDeferredHolder<CreativeModeTab, CreativeModeTab> register(String name, ILangEntry title, Holder<Item> icon, UnaryOperator<PortingLibCreativeTab.PortingLibCreativeTabBuilder> operator) {
        return register(name, () -> {
            PortingLibCreativeTab.PortingLibCreativeTabBuilder builder = (PortingLibCreativeTab.PortingLibCreativeTabBuilder) PortingLibCreativeTab.builder()
                  .title(title.translate())
                  .withLabelColor(SpecialColors.TEXT_TITLE.argb())
                  .icon(() -> new ItemStack(icon));
                  //.withTabFactory(MekanismCreativeTab::new);
            return operator.apply(builder).build();
        });
    }

    public static void addToDisplay(CreativeModeTab.Output output, Collection<? extends Holder<Item>> items, Predicate<Holder<Item>> shouldSkip) {
        for (Holder<Item> itemProvider : items) {
            if (!shouldSkip.test(itemProvider)) {
                addToDisplay(output, itemProvider);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void addToDisplay(CreativeModeTab.Output output, BlockRegistryObject<?, ?>... blocks) {
        addToDisplay(output, Arrays.stream(blocks).map(BlockRegistryObject::getItemHolder).toArray(Holder[]::new));
    }

    @SafeVarargs
    public static void addToDisplay(CreativeModeTab.Output output, Holder<Item>... items) {
        CreativeModeTab.TabVisibility visibility;
        if (output instanceof FabricItemGroupEntries) {
            //If we are added from the event, only add the item to the parent tab, as we will already be contained in the search tab
            // from when we are adding to our tabs
            visibility = CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
        } else {
            visibility = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
        }
        for (Holder<Item> item : items) {
            Item itemLike = item.value();
            if (itemLike instanceof ICustomCreativeTabContents contents) {
                if (contents.addDefault()) {
                    output.accept(itemLike, visibility);
                }
                contents.addItems(item, stack -> output.accept(stack, visibility));
            } else {
                output.accept(itemLike, visibility);
            }
        }
    }

    public static void addToDisplay(ItemDeferredRegister register, CreativeModeTab.Output output) {
        addToDisplay(output, register.getEntries(), ConstantPredicates.alwaysFalse());
    }

    public static void addToDisplay(BlockDeferredRegister register, CreativeModeTab.Output output) {
        //Don't add bounding blocks to the creative tab
        addToDisplay(output, register.getSecondaryEntries(), MekanismBlocks.BOUNDING_BLOCK::secondaryKeyMatches);
    }

    public static void addToDisplay(FluidDeferredRegister register, CreativeModeTab.Output output) {
        addToDisplay(output, register.getBucketEntries(), ConstantPredicates.alwaysFalse());
    }

    public interface ICustomCreativeTabContents {

        void addItems(Holder<Item> item, Consumer<ItemStack> addToTab);

        default boolean addDefault() {
            return true;
        }
    }

    /*
    public static class MekanismCreativeTab extends CreativeModeTab {

        protected MekanismCreativeTab(CreativeModeTab.Builder builder) {
            super(builder);
        }

        @Override
        public int getLabelColor() {
            return SpecialColors.TEXT_TITLE.argb();
        }
    }
     */
}