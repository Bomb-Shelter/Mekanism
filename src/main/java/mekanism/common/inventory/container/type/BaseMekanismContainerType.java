package mekanism.common.inventory.container.type;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseMekanismContainerType<T, CONTAINER extends AbstractContainerMenu, FACTORY> extends ExtendedScreenHandlerType<CONTAINER, T> {

    protected final FACTORY mekanismConstructor;
    protected final Class<T> type;

    protected BaseMekanismContainerType(Class<T> type, FACTORY mekanismConstructor, ExtendedFactory<CONTAINER, T> constructor, StreamCodec<? super RegistryFriendlyByteBuf, T> packetCodec) {
        super(constructor, packetCodec);
        this.type = type;
        this.mekanismConstructor = mekanismConstructor;
    }
}