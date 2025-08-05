package mekanism.common.item.interfaces;

import io.github.fabricators_of_create.porting_lib.event.common.ItemAttributeModifierEvent;

public interface IHasConditionalAttributes {

    /**
     * Called on any items that implement this interface when the event is fired for an item of that type.
     */
    void adjustAttributes(ItemAttributeModifierEvent event);
}