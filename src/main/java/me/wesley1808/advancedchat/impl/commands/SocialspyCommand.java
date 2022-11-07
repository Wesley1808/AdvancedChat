package me.wesley1808.advancedchat.impl.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.utils.Formatter;
import me.wesley1808.advancedchat.impl.utils.Permission;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class SocialspyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("socialspy")
                .requires(Permission.require(Permission.SOCIALSPY, 2))
                .executes(context -> execute(context.getSource().getPlayerOrException()))
        );
    }

    private static int execute(ServerPlayer player) {
        Config.Messages messages = Config.instance().messages;

        AdvancedChatAPI.modifyData(player, (data) -> {
            data.socialSpy = !data.socialSpy;
            if (data.socialSpy) {
                player.sendSystemMessage(Formatter.parse(messages.socialSpyEnabled));
            } else {
                player.sendSystemMessage(Formatter.parse(messages.socialSpyDisabled));
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
