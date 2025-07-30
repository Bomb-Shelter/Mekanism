package mekanism.common.capabilities;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record MultiTypeCapability<HANDLER>(BlockApiLookup<HANDLER, @Nullable Direction> block,
                                           ItemApiLookup<HANDLER, Void> item,
                                           EntityApiLookup<HANDLER, ?> entity) implements IMultiTypeCapability<HANDLER, HANDLER> {

    public MultiTypeCapability(ResourceLocation name, Class<HANDLER> handlerClass) {
        this(
              BlockApiLookup.get(name, handlerClass, Direction.class),
              ItemApiLookup.get(name, handlerClass, void.class),
              EntityApiLookup.get(name, handlerClass, void.class)
        );
    }
}