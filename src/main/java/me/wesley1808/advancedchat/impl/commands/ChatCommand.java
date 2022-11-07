package me.wesley1808.advancedchat.impl.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.utils.Formatter;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ChatCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = literal("chat");

        builder.then(literal("global").executes(ctx -> execute(ctx.getSource().getPlayerOrException(), "Global", true)));

        builder.then(argument("channel", word())
                .suggests(availableChannels())
                .executes(ctx -> execute(ctx.getSource().getPlayerOrException(), getString(ctx, "channel"), false))
        );

        dispatcher.register(builder);
    }

    private static int execute(ServerPlayer player, String name, boolean isGlobal) {
        ChatChannel channel = isGlobal ? null : Channels.get(name);
        if (!isGlobal && (channel == null || !channel.canPlayerUse(player))) {
            player.sendSystemMessage(Formatter.parse(Config.instance().messages.channelNotFound.replace("${name}", name)));
            return 0;
        }

        player.sendSystemMessage(Formatter.parse(Config.instance().messages.switchedChannels.replace("${channel}", StringUtils.capitalize(name))));
        AdvancedChatAPI.modifyData(player, (data) -> data.channel = channel);
        Util.resetActionBarPacket(player);
        return Command.SINGLE_SUCCESS;
    }

    private static SuggestionProvider<CommandSourceStack> availableChannels() {
        return (ctx, builder) -> {
            ServerPlayer source = ctx.getSource().getPlayerOrException();
            List<String> channels = new ObjectArrayList<>();
            Channels.getAll().forEach((name, channel) -> {
                if (channel.canPlayerUse(source)) {
                    channels.add(name);
                }
            });

            return Util.suggest(builder, channels);
        };
    }
}
