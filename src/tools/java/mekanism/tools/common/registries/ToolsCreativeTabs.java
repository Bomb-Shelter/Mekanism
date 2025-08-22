package mekanism.tools.common.registries;

import io.github.fabricators_of_create.porting_lib.item.itemgroup.PortingLibCreativeTab;
import mekanism.common.registration.MekanismDeferredHolder;
import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import mekanism.common.registries.MekanismCreativeTabs;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.item.ItemMekanismArmor;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;

public class ToolsCreativeTabs {

    public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(MekanismTools.MODID, ToolsCreativeTabs::addToExistingTabs);

    public static final MekanismDeferredHolder<CreativeModeTab, CreativeModeTab> TOOLS = CREATIVE_TABS.registerMain(ToolsLang.MEKANISM_TOOLS, ToolsItems.DIAMOND_PAXEL, builder ->
            (PortingLibCreativeTab.PortingLibCreativeTabBuilder) builder.backgroundTexture(MekanismTools.rl("textures/gui/creative_tab.png"))
                //.withSearchBar(80)//Allow our tabs to be searchable for convenience purposes
                //.withTabsBefore(MekanismCreativeTabs.MEKANISM.getId())
                .displayItems((displayParameters, output) -> CreativeTabDeferredRegister.addToDisplay(ToolsItems.ITEMS, output))
    );

    private static void addToExistingTabs(ResourceKey<CreativeModeTab> tabKey, FabricItemGroupEntries event) {
        if (tabKey == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for (Holder<Item> holder : ToolsItems.ITEMS.getEntries()) {
                if (holder.value() instanceof DiggerItem) {
                    CreativeTabDeferredRegister.addToDisplay(event, holder);
                }
            }
        } else if (tabKey == CreativeModeTabs.COMBAT) {
            for (Holder<Item> itemProvider : ToolsItems.ITEMS.getEntries()) {
                Item item = itemProvider.value();
                if (item instanceof ItemMekanismArmor || item instanceof SwordItem || item instanceof ShieldItem) {
                    CreativeTabDeferredRegister.addToDisplay(event, itemProvider);
                }
            }
        }
    }
}