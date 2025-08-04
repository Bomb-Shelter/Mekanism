package mekanism.common.network.fabric;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPayloadContext implements IPayloadContext {
    public static IPayloadContext configuration(ServerConfigurationNetworking.Context context) {
        return new ServerPayloadContext(context.responseSender(), context.responseSender());
    }

    public static IPayloadContext play(ServerPlayNetworking.Context context) {
        return new ServerPayloadContext(context.responseSender(), context.player().connection);
    }

    private final PacketSender responseSender;

    public ServerPayloadContext(PacketSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public PacketSender responseSender() {
        return responseSender;
    }
}
