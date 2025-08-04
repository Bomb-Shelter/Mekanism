package mekanism.common.network.fabric;

import mekanism.common.network.IMekanismPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WrappedPayloadTypeRegistry<B extends FriendlyByteBuf> implements CommonPayloadTypeRegistry<B> {

    public static final WrappedPayloadTypeRegistry<FriendlyByteBuf> CONFIGURATION_C2S = new WrappedPayloadTypeRegistry<>(PayloadTypeRegistry.configurationC2S(), (type, handler) -> {
        ServerConfigurationNetworking.registerGlobalReceiver(type, (payload, context) -> {
            handler.handle(payload, IPayloadContext);
        });
    });
    public static final WrappedPayloadTypeRegistry<FriendlyByteBuf> CONFIGURATION_S2C = new WrappedPayloadTypeRegistry<>(PayloadTypeRegistry.configurationC2S());
    public static final WrappedPayloadTypeRegistry<RegistryFriendlyByteBuf> PLAY_C2S = new WrappedPayloadTypeRegistry<>(PayloadTypeRegistry.playC2S());
    public static final WrappedPayloadTypeRegistry<RegistryFriendlyByteBuf> PLAY_S2C = new WrappedPayloadTypeRegistry<>(PayloadTypeRegistry.playC2S());

    private final PayloadTypeRegistry<B> registry;
    private final BiConsumer<CustomPacketPayload.Type<? extends IMekanismPacket>, IPayloadHandler<? extends IMekanismPacket>> handlerConsumer;

    public WrappedPayloadTypeRegistry(PayloadTypeRegistry<B> registry, BiConsumer<CustomPacketPayload.Type<? extends IMekanismPacket>, IPayloadHandler<? extends IMekanismPacket>> handlerConsumer) {
        this.registry = registry;
        this.handlerConsumer = handlerConsumer;
    }

    @Override
    public <T extends CustomPacketPayload> CustomPacketPayload.TypeAndCodec<? super B, T> register(CustomPacketPayload.Type<T> id, StreamCodec<? super B, T> codec, IPayloadHandler<T> handler) {
        var type = registry.register(id, codec);
        this.handlerConsumer.accept(handler);
        return type;
    }


}
