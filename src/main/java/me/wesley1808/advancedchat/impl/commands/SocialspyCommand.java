package me.wesley1808.advancedchat.impl.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.utils.Formatter;
import me.wesley1808.advancedchat.impl.utils.Permission;
import me.wesley1808.advancedchat.impl.utils.Socialspy;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.StringUtils;

import static net.minecraft.commands.Commands.literal;

public class SocialspyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = literal("socialspy").requires(Permission.require(Permission.SOCIALSPY, 2));

        for (Socialspy.Mode mode : Socialspy.Mode.values()) {
            String name = mode.name().toLowerCase();
            builder.then(literal(name)
                    .executes(context -> execute(context.getSource().getPlayerOrException(), mode, name))
            );
        }

        dispatcher.register(builder);
    }

    private static int execute(ServerPlayer player, Socialspy.Mode mode, String name) {
        player.sendSystemMessage(Formatter.parse(Config.instance().messages.switchedSocialSpy.replace("${mode}", StringUtils.capitalize(name))));
        DataManager.modify(player, (data) -> data.spyMode = mode);
        return Command.SINGLE_SUCCESS;
    }
}
