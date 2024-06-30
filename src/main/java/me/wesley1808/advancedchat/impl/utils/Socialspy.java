package me.wesley1808.advancedchat.impl.utils;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import me.wesley1808.advancedchat.impl.AdvancedChat;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.data.DataManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Socialspy {

    // Private Messages
    public static void send(CommandSourceStack source, ServerPlayer target, PlayerChatMessage message) {
        Config.Socialspy config = Config.instance().socialSpy;
        MutableComponent text = Formatter.parse(config.prefix).append(Placeholders.parseText(
                Formatter.parseNodes(config.privateMessage),
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of(
                        "source", source.getDisplayName(),
                        "target", target.getDisplayName(),
                        "message", getPrivateMessageContent(message)
                )
        ));

        Socialspy.send(target.server, text, (player) -> {
            Socialspy.Mode mode = DataManager.get(player).spyMode;
            return mode.acceptsPrivate() && player != target && player != source.getPlayer();
        });
        if (config.logPrivateMessages) {
            AdvancedChat.getLogger().info(text.getString());
        }
    }

    private static Component getPrivateMessageContent(PlayerChatMessage message) {
        if (ModCompat.STYLEDCHAT) {
            return ExtSignedMessage.getArg(message, "base_input");
        } else {
            return message.decoratedContent();
        }
    }

    // Channel Messages
    public static void send(ServerPlayer sender, List<ServerPlayer> receivers, PlayerChatMessage message) {
        ChatChannel channel = DataManager.get(sender).channel;
        if (channel == null) {
            return;
        }

        Config.Socialspy config = Config.instance().socialSpy;
        MutableComponent text = Formatter.parse(config.prefix).append(Placeholders.parseText(
                Formatter.parseNodes(config.channelMessage),
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of(
                        "channel", AdvancedChatAPI.getChannelPrefix(sender),
                        "sender", sender.getDisplayName(),
                        "message", message.decoratedContent()
                )
        ));

        Socialspy.send(sender.server, text, (player) -> {
            Socialspy.Mode mode = DataManager.get(player).spyMode;
            return mode.acceptsChannel() && channel.canUse(player) && !receivers.contains(player);
        });
    }

    private static void send(MinecraftServer server, Component message, Predicate<ServerPlayer> predicate) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (Permission.check(player, Permission.SOCIALSPY, 2) && predicate.test(player)) {
                player.sendSystemMessage(message);
            }
        }
    }

    public enum Mode {
        ALL,
        CHANNEL,
        PRIVATE,
        NONE;

        private boolean acceptsChannel() {
            return this == ALL || this == CHANNEL;
        }

        private boolean acceptsPrivate() {
            return this == ALL || this == PRIVATE;
        }
    }
}
