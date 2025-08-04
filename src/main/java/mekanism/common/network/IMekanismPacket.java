package mekanism.common.network;

import mekanism.common.network.fabric.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IMekanismPacket extends CustomPacketPayload {

    void handle(IPayloadContext context);
}