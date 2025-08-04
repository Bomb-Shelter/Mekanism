package mekanism.common.network.to_client.configuration;

import java.util.function.Consumer;
import mekanism.common.Mekanism;
import mekanism.common.network.fabric.ICustomConfigurationTask;
import mekanism.common.network.to_client.security.PacketBatchSecurityUpdate;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.NotNull;

public record SyncAllSecurityData(ServerConfigurationPacketListenerImpl listener) implements ICustomConfigurationTask {

    private static final ResourceLocation ID = Mekanism.rl("sync_security");
    private static final Type TYPE = new Type(ID.toString());

    @Override
    public void run(Consumer<CustomPacketPayload> sender) {
        sender.accept(new PacketBatchSecurityUpdate());
        listener().finishCurrentTask(type());
    }

    @NotNull
    @Override
    public Type type() {
        return TYPE;
    }
}