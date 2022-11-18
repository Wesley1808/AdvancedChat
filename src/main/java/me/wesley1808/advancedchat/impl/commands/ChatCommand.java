package me.wesley1808.advancedchat.impl.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import eu.pb4.styledchat.StyledChatUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.utils.Formatter;
import me.wesley1808.advancedchat.impl.utils.ModCompat;
import me.wesley1808.advancedchat.impl.utils.Util;
import me.wesley1808.advancedchat.mixins.PlayerListInvoker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.MessageArgument.message;

public class ChatCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = literal("chat");

        builder.then(literal("global").executes(ctx -> switchChannel(ctx.getSource().getPlayerOrException(), "Global", true)));

        builder.then(argument("channel", word())
                .suggests(availableChannels())
                .executes(ctx -> switchChannel(ctx.getSource().getPlayerOrException(), getString(ctx, "channel"), false))
                .then(argument("message", message())
                        .executes(ctx -> {
                            CommandSourceStack source = ctx.getSource();
                            ServerPlayer player = source.getPlayerOrException();
                            MessageArgument.resolveChatMessage(ctx, "message", (message) -> {
                                sendInChannel(source, player, getString(ctx, "channel"), message);
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );

        dispatcher.register(builder);
    }

    private static int switchChannel(ServerPlayer player, String name, boolean isGlobal) {
        ChatChannel channel = isGlobal ? null : Channels.get(name);
        if (!isGlobal && (channel == null || !channel.canPlayerUse(player))) {
            player.sendSystemMessage(Formatter.parse(Config.instance().messages.channelNotFound.replace("${name}", name)));
            return 0;
        }

        player.sendSystemMessage(Formatter.parse(Config.instance().messages.switchedChannels.replace("${channel}", StringUtils.capitalize(name))));
        DataManager.modify(player, (data) -> data.channel = channel);
        Util.resetActionBarPacket(player);
        return Command.SINGLE_SUCCESS;
    }

    private static void sendInChannel(CommandSourceStack source, ServerPlayer player, String name, PlayerChatMessage message) {
        ChatChannel channel = Channels.get(name);
        if (channel == null || !channel.canPlayerUse(player)) {
            player.sendSystemMessage(Formatter.parse(Config.instance().messages.channelNotFound.replace("${name}", name)));
            return;
        }

        DataManager.modify(player, (data) -> {
            ChatChannel original = data.channel;
            data.channel = channel;

            // StyledChat support
            if (ModCompat.STYLEDCHAT) {
                StyledChatUtils.modifyForSending(message, source, ChatType.CHAT);
            }

            MutableComponent prefix = (MutableComponent) AdvancedChatAPI.getChannelPrefix(player);
            ChatType.Bound bound = ChatType.bind(ChatType.CHAT, player.level.registryAccess(), prefix.append(player.getDisplayName()));

            // We can't use the other broadcast methods here as they would trigger fabric api's message event.
            ((PlayerListInvoker) player.server.getPlayerList()).invokeBroadcastChatMessage(message, player::shouldFilterMessageTo, player, bound);
            data.channel = original;
        });
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
