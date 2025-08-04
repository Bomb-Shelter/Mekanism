package mekanism.common.network.fabric;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface CommonPayloadTypeRegistry<B extends FriendlyByteBuf> {
    <T extends CustomPacketPayload> CustomPacketPayload.TypeAndCodec<? super B, T> register(CustomPacketPayload.Type<T> id, StreamCodec<? super B, T> codec, IPayloadHandler<T> handler);

    static CommonPayloadTypeRegistry<FriendlyByteBuf> configurationToServer() {
        return WrappedPayloadTypeRegistry.CONFIGURATION_C2S;
    }

    static CommonPayloadTypeRegistry<FriendlyByteBuf> configurationToClient() {
        return WrappedPayloadTypeRegistry.CONFIGURATION_S2C;
    }

    static CommonPayloadTypeRegistry<RegistryFriendlyByteBuf> playToServer() {
        return WrappedPayloadTypeRegistry.PLAY_C2S;
    }

    static CommonPayloadTypeRegistry<RegistryFriendlyByteBuf> playToClient() {
        return WrappedPayloadTypeRegistry.PLAY_S2C;
    }
}
