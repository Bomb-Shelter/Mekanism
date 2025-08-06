package mekanism.common.base;

import java.util.function.Predicate;

import me.lucko.fabric.api.permissions.v0.Permissions;
import mekanism.common.Mekanism;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class MekanismPermissions {

    public static final String BYPASS_SECURITY = node("bypass_security");

    //Commands
    public static final CommandPermissionNode COMMAND = new CommandPermissionNode(node("command"), Commands.LEVEL_ALL);

    public static final CommandPermissionNode COMMAND_BUILD = nodeOpCommand("build");
    public static final CommandPermissionNode COMMAND_BUILD_REMOVE = nodeSubCommand(COMMAND_BUILD, "remove");

    public static final CommandPermissionNode COMMAND_CHUNK = nodeOpCommand("chunk");
    public static final CommandPermissionNode COMMAND_CHUNK_CLEAR = nodeSubCommand(COMMAND_CHUNK, "clear");
    public static final CommandPermissionNode COMMAND_CHUNK_FLUSH = nodeSubCommand(COMMAND_CHUNK, "flush");
    public static final CommandPermissionNode COMMAND_CHUNK_UNWATCH = nodeSubCommand(COMMAND_CHUNK, "unwatch");
    public static final CommandPermissionNode COMMAND_CHUNK_WATCH = nodeSubCommand(COMMAND_CHUNK, "watch");

    public static final CommandPermissionNode COMMAND_DEBUG = nodeOpCommand("debug");
    public static final CommandPermissionNode COMMAND_FORCE_RETROGEN = nodeOpCommand("force_retrogen");

    public static final CommandPermissionNode COMMAND_RADIATION = nodeOpCommand("radiation");
    public static final CommandPermissionNode COMMAND_RADIATION_ADD = nodeSubCommand(COMMAND_RADIATION, "add");
    public static final CommandPermissionNode COMMAND_RADIATION_ADD_ENTITY = nodeSubCommand(COMMAND_RADIATION, "add_entity");
    public static final CommandPermissionNode COMMAND_RADIATION_ADD_ENTITY_OTHERS = nodeSubCommand(COMMAND_RADIATION_ADD_ENTITY, "others");
    public static final CommandPermissionNode COMMAND_RADIATION_GET = nodeSubCommand(COMMAND_RADIATION, "get");
    public static final CommandPermissionNode COMMAND_RADIATION_HEAL = nodeSubCommand(COMMAND_RADIATION, "heal");
    public static final CommandPermissionNode COMMAND_RADIATION_HEAL_OTHERS = nodeSubCommand(COMMAND_RADIATION_HEAL, "others");
    public static final CommandPermissionNode COMMAND_RADIATION_REDUCE = nodeSubCommand(COMMAND_RADIATION, "reduce");
    public static final CommandPermissionNode COMMAND_RADIATION_REDUCE_OTHERS = nodeSubCommand(COMMAND_RADIATION_REDUCE, "others");
    public static final CommandPermissionNode COMMAND_RADIATION_REMOVE_ALL = nodeSubCommand(COMMAND_RADIATION, "remove.all");

    public static final CommandPermissionNode COMMAND_TEST_RULES = nodeOpCommand("test_rules");
    public static final CommandPermissionNode COMMAND_TP = nodeOpCommand("tp");
    public static final CommandPermissionNode COMMAND_TP_POP = nodeOpCommand("tp_pop");

    private static CommandPermissionNode nodeOpCommand(String nodeName) {
        return new CommandPermissionNode("command." + nodeName, Commands.LEVEL_GAMEMASTERS);
    }

    private static CommandPermissionNode nodeSubCommand(CommandPermissionNode parent, String nodeName) {
        //Because sub commands can assume that the parent was checked before getting to them, we can have a default resolver of always true
        // The main benefit for them to have their own node is just in case someone wants to do more restricting
        return new CommandPermissionNode(subNode(parent.node, nodeName), parent.fallbackLevel);
    }

    private static String subNode(String fullParentName, String nodeName) {
        //Strip the modid from the parent's node name
        String parentName = fullParentName.substring(fullParentName.indexOf('.') + 1);
        return node(parentName + "." + nodeName);
    }

    private static String node(String nodeName) {
        return Mekanism.MODID + "." + nodeName;
    }

    public record CommandPermissionNode(String node, int fallbackLevel) implements Predicate<CommandSourceStack> {

        @Override
        public boolean test(CommandSourceStack source) {
            //See https://github.com/MinecraftForge/MinecraftForge/commit/f7eea35cb9b043aae0a3866a9578724aa7560585 for details on why
            // has permission is checked first and the implications
            return source.hasPermission(fallbackLevel) || source.isPlayer() && Permissions.check(source.getPlayer(), node);
        }
    }
}