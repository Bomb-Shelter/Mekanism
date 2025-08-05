package mekanism.common;

import mekanism.api.text.EnumColor;
import mekanism.common.advancements.MekanismCriteriaTriggers;
import mekanism.common.block.BlockBounding;
import mekanism.common.block.BlockCardboardBox;
import mekanism.common.block.BlockMekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.radiation.PlayerExposure;
import mekanism.common.network.fabric.PacketDistributor;
import mekanism.common.network.to_client.player_data.PacketPlayerData;
import mekanism.common.network.to_client.player_data.PacketResetPlayerClient;
import mekanism.common.network.to_client.radiation.PacketPlayerRadiationData;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tags.MekanismTags.Items;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvents.PlayerLoggedInEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvents.PlayerLoggedOutEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerInteractEvent.RightClickBlock;

public class CommonPlayerTracker {

    private static final Component ALPHA_WARNING = MekanismLang.LOG_FORMAT.translateColored(EnumColor.RED, MekanismLang.MEKANISM, EnumColor.GRAY,
          MekanismLang.ALPHA_WARNING.translate(EnumColor.INDIGO, ChatFormatting.UNDERLINE, new ClickEvent(Action.OPEN_URL,
                "https://github.com/mekanism/Mekanism#alpha-status"), MekanismLang.ALPHA_WARNING_HERE));

    public CommonPlayerTracker() {
        PlayerLoggedInEvent.EVENT.register(this::onPlayerLoginEvent);
        PlayerLoggedOutEvent.EVENT.register(this::onPlayerLogoutEvent);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(this::onPlayerDimChangedEvent);
        PlayerEvents.StartTracking.EVENT.register(this::onPlayerStartTrackingEvent);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::respawnEvent);
        RightClickBlock.EVENT.register(this::rightClickEvent);
    }

    public void onPlayerLoginEvent(PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide) {
            if (MekanismConfig.general.enableAlphaWarning.getAsBoolean()) {
                player.sendSystemMessage(ALPHA_WARNING);
            }
            MekanismCriteriaTriggers.LOGGED_IN.value().trigger((ServerPlayer) player);
        }
    }

    public void onPlayerLogoutEvent(PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        Mekanism.playerState.clearPlayer(player.getUUID(), false);
    }

    public void onPlayerDimChangedEvent(ServerPlayer player, ServerLevel origin, ServerLevel destination) {
        Mekanism.playerState.clearPlayer(player.getUUID(), false);
        PacketDistributor.sendToPlayer(player, new PacketPlayerRadiationData(player));
        PlayerExposure.updateClientRadiation(player);
    }

    public void onPlayerStartTrackingEvent(PlayerEvents.StartTracking event) {
        if (event.getTarget() instanceof Player player && event.getEntity() instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new PacketPlayerData(player.getUUID()));
        }
    }

    public void respawnEvent(ServerPlayer oldPlayer, ServerPlayer player, boolean alive) {
        PacketDistributor.sendToPlayer(player, new PacketPlayerRadiationData(player));
        PlayerExposure.updateClientRadiation(player);
        PacketDistributor.sendToAllPlayers(new PacketResetPlayerClient(player.getUUID()));
    }

    /**
     * If the player is sneaking and the dest block is a cardboard box, ensure onBlockActivated is called, and that the item use is not.
     */
    public void rightClickEvent(RightClickBlock event) {
        ItemStack itemInHand = event.getEntity().getItemInHand(event.getHand());
        if (itemInHand.is(Items.CONFIGURATORS) && !itemInHand.is(MekanismItems.CONFIGURATOR)) {
            //it's a wrench, see if it's our block. Not the configurator, as it handles bypass correctly
            Block block = event.getLevel().getBlockState(event.getPos()).getBlock();
            if (block instanceof BlockMekanism || block instanceof BlockBounding) {
                event.setUseBlock(TriState.TRUE);//force it to use the item on the block
            }
        } else if (event.getEntity().isShiftKeyDown() && event.getLevel().getBlockState(event.getPos()).getBlock() instanceof BlockCardboardBox) {
            event.setUseBlock(TriState.TRUE);
            event.setUseItem(TriState.FALSE);
        }
    }
}