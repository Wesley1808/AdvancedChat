package me.wesley1808.advancedchat.impl.utils;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import me.wesley1808.advancedchat.impl.AdvancedChat;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.data.DataManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Socialspy {

    // Private Messages
    public static void send(CommandSourceStack source, ServerPlayer target, PlayerChatMessage message) {
        Config.Socialspy config = Config.instance().socialSpy;
        MutableComponent prefix = (MutableComponent) TextParserUtils.formatTextSafe(config.prefix);
        MutableComponent text = prefix.append(
                Placeholders.parseText(
                        TextParserUtils.formatNodesSafe(config.privateMessage),
                        Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                        Map.of(
                                "source", source.getDisplayName(),
                                "target", target.getDisplayName(),
                                "message", Component.literal(message.signedContent())
                        )
                )
        );

        Socialspy.send(target.server, text, (player) -> player != target && player != source.getPlayer());
        if (config.logPrivateMessages) {
            AdvancedChat.getLogger().info(text.getString());
        }
    }

    // Channel Messages
    public static void send(ServerPlayer sender, List<ServerPlayer> receivers, PlayerChatMessage message) {
        AdvancedChatData data = AdvancedChatAPI.getData(sender);
        if (data.channel == null || ChatChannel.isStaff(data.channel)) {
            return;
        }

        Config.Socialspy config = Config.instance().socialSpy;
        MutableComponent prefix = (MutableComponent) TextParserUtils.formatTextSafe(config.prefix);
        MutableComponent text = prefix.append(
                Placeholders.parseText(
                        TextParserUtils.formatNodesSafe(config.channelMessage),
                        Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                        Map.of(
                                "channel", AdvancedChatAPI.getChannelPrefix(sender),
                                "sender", sender.getDisplayName(),
                                "message", Component.literal(message.signedContent())
                        )
                )
        );

        Socialspy.send(sender.server, text, (player) -> !receivers.contains(player));
    }

    private static void send(MinecraftServer server, Component message, @Nullable Predicate<ServerPlayer> predicate) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            AdvancedChatData data = AdvancedChatAPI.getData(player);
            if (data.socialSpy && (predicate == null || predicate.test(player)) && Permission.check(player, "command.socialspy", 2)) {
                player.sendSystemMessage(message);
            }
        }
    }
}
