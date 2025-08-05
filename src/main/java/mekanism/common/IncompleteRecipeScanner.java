package mekanism.common;

import java.util.List;

import io.github.fabricators_of_create.porting_lib.entity.events.OnDatapackSyncCallback;
import mekanism.api.text.EnumColor;
import mekanism.common.recipe.MekanismRecipeType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import javax.annotation.Nullable;

public class IncompleteRecipeScanner {

    private static final Component RECIPE_WARNING = MekanismLang.LOG_FORMAT.translateColored(EnumColor.RED, MekanismLang.MEKANISM, MekanismLang.RECIPE_WARNING.translate());
    private static boolean foundIncompleteRecipes = false;

    public static void init() {
        OnDatapackSyncCallback.EVENT.register(IncompleteRecipeScanner::recipes);
        ServerLifecycleEvents.SERVER_STARTED.register(IncompleteRecipeScanner::serverStarted);
    }

    public static void recipes(PlayerList playerList, @Nullable ServerPlayer player) {
        //player is logging in
        if (player != null) {
            if (foundIncompleteRecipes) {
                sendMessageToPlayer(player);
            }
            //skip running scan on player login, should have run at start or last reload
            return;
        }

        //run the scan
        foundIncompleteRecipes = MekanismRecipeType.checkIncompleteRecipes(playerList.getServer());

        //if broken, message any players online
        if (foundIncompleteRecipes) {
            List<ServerPlayer> players = playerList.getPlayers();
            if (!players.isEmpty()) {
                players.forEach(IncompleteRecipeScanner::sendMessageToPlayer);
            }
        }
    }

    private static void sendMessageToPlayer(ServerPlayer player) {
        player.sendSystemMessage(RECIPE_WARNING);
    }

    public static void serverStarted(MinecraftServer server) {
        //run the scan. In theory there will be no players at this point, so shouldn't need to send message
        foundIncompleteRecipes = MekanismRecipeType.checkIncompleteRecipes(server);
    }
}
