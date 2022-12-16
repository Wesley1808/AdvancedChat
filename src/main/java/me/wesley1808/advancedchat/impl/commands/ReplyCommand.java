package me.wesley1808.advancedchat.impl.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.wesley1808.advancedchat.impl.interfaces.IServerPlayer;
import me.wesley1808.advancedchat.mixins.accessors.MsgCommandInvoker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.MessageArgument.message;

public final class ReplyCommand {
    private static final String MESSAGE_KEY = "message";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(literal("reply")
                .then(argument(MESSAGE_KEY, message())
                        .executes(ReplyCommand::execute)
                )
        );

        dispatcher.register(literal("r").redirect(node));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer source = context.getSource().getPlayerOrException();
        UUID uuid = IServerPlayer.getReplyTarget(source);

        ServerPlayer target;
        if (uuid == null || (target = source.server.getPlayerList().getPlayer(uuid)) == null) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }

        MessageArgument.resolveChatMessage(context, MESSAGE_KEY, (message) -> {
            MsgCommandInvoker.sendMessage(context.getSource(), List.of(target), message);
        });

        return Command.SINGLE_SUCCESS;
    }
}