package mekanism.common.network;

import mekanism.common.lib.Version;
import mekanism.common.network.fabric.CommonPayloadTypeRegistry;
import mekanism.common.network.fabric.IPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public abstract class BasePacketHandler {

    protected BasePacketHandler(Version version) {
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            registerClientToServer(new PacketRegistrar(true));
            registerServerToClient(new PacketRegistrar(false));
        });
    }

    protected abstract void registerClientToServer(PacketRegistrar registrar);

    protected abstract void registerServerToClient(PacketRegistrar registrar);

    protected record SimplePacketPayLoad(CustomPacketPayload.Type<CustomPacketPayload> type) implements CustomPacketPayload {

        private SimplePacketPayLoad(ResourceLocation id) {
            this(new CustomPacketPayload.Type<>(id));
        }
    }

    protected record PacketRegistrar(boolean toServer) {

        public <MSG extends IMekanismPacket> void configuration(CustomPacketPayload.Type<MSG> type, StreamCodec<? super FriendlyByteBuf, MSG> reader) {
            if (toServer) {
                CommonPayloadTypeRegistry.configurationToServer().register(type, reader, IMekanismPacket::handle);
            } else {
                CommonPayloadTypeRegistry.configurationToClient().register(type, reader, IMekanismPacket::handle);
            }
        }

        public <MSG extends IMekanismPacket> void play(CustomPacketPayload.Type<MSG> type, StreamCodec<? super RegistryFriendlyByteBuf, MSG> reader) {
            if (toServer) {
                CommonPayloadTypeRegistry.playToServer().register(type, reader, IMekanismPacket::handle);
            } else {
                CommonPayloadTypeRegistry.playToClient().register(type, reader, IMekanismPacket::handle);
            }
        }

        public SimplePacketPayLoad playInstanced(ResourceLocation id, IPayloadHandler<CustomPacketPayload> handler) {
            SimplePacketPayLoad payload = new SimplePacketPayLoad(id);
            if (toServer) {
                CommonPayloadTypeRegistry.playToServer().register(payload.type(), StreamCodec.unit(payload), handler);
            } else {
                CommonPayloadTypeRegistry.playToClient().register(payload.type(), StreamCodec.unit(payload), handler);
            }
            return payload;
        }
    }
}