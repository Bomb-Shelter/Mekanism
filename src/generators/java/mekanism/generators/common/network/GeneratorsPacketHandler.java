package mekanism.generators.common.network;

import mekanism.common.lib.Version;
import mekanism.common.network.BasePacketHandler;
import mekanism.generators.common.network.to_server.PacketGeneratorsGuiInteract;
import mekanism.generators.common.network.to_server.PacketGeneratorsTileButtonPress;

public class GeneratorsPacketHandler extends BasePacketHandler {

    public GeneratorsPacketHandler(Version version) {
        super(version);
    }

    @Override
    protected void registerClientToServer(PacketRegistrar registrar) {
        registrar.play(PacketGeneratorsTileButtonPress.TYPE, PacketGeneratorsTileButtonPress.STREAM_CODEC);
        registrar.play(PacketGeneratorsGuiInteract.TYPE, PacketGeneratorsGuiInteract.STREAM_CODEC);
    }

    @Override
    protected void registerServerToClient(PacketRegistrar registrar) {
    }
}