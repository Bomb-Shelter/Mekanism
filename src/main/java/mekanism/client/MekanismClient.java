package mekanism.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;

import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismShaders;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.render.tileentity.RenderSPS;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.base.holiday.ClientHolidayInfo;
import mekanism.common.lib.radiation.ClientRadiation;
import mekanism.common.lib.security.SecurityData;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import mekanism.common.network.PacketUtils;
import mekanism.common.network.to_server.PacketKey;
import mekanism.common.recipe.MekanismRecipeType;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MekanismClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        ClientHolidayInfo.init();
        MekanismShaders.init();
        ClientRegistration.init();
        MekanismRenderer.init();
    }

    public static final Map<UUID, SecurityData> clientSecurityMap = new Object2ObjectOpenHashMap<>();
    public static final Map<UUID, String> clientUUIDMap = new Object2ObjectOpenHashMap<>();

    public static void updateKey(KeyMapping key, int type) {
        updateKey(key.isDown(), type);
    }

    public static void updateKey(boolean pressed, int type) {
        if (Minecraft.getInstance().player != null) {
            UUID playerUUID = Minecraft.getInstance().player.getUUID();
            boolean down = Minecraft.getInstance().screen == null && pressed;
            if (down != Mekanism.keyMap.has(playerUUID, type)) {
                PacketUtils.sendToServer(new PacketKey(type, down));
                Mekanism.keyMap.update(playerUUID, type, down);
            }
        }
    }

    /**
     * Reset things that aren't needed between levels or would leak
     */
    public static void resetDimensionChange() {
        Mekanism.playerState.clear(true);
        Mekanism.activeVibrators.clear();
        ClientRadiation.resetClient();
        RenderSPS.clearBoltRenderers();
        TransmitterNetworkRegistry.clearClientNetworks();
        RenderTickHandler.clearQueued();

        for (IModModule module : Mekanism.modulesLoaded) {
            module.resetClientDimensionChanged();
        }
    }

    public static void reset() {
        clientSecurityMap.clear();
        clientUUIDMap.clear();

        ClientTickHandler.reset();
        SoundHandler.radiationSoundMap.clear();
        MekanismRecipeType.clearCache();

        resetDimensionChange();

        for (IModModule module : Mekanism.modulesLoaded) {
            module.resetClient();
        }
    }

    public static void launchClient(Connection connection) {
        for (IModModule module : Mekanism.modulesLoaded) {
            module.launchClient(connection);
        }
    }

    @Nullable
    public static Level tryGetClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Nullable
    public static Player tryGetClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @SubscribeEvent
    public static void onCloneRespawn(ClientPlayerNetworkEvent.Clone event) {
        if (event.getOldPlayer().level() != event.getNewPlayer().level()) {
            resetDimensionChange();
        }
    }
}