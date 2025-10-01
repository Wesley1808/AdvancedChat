package me.wesley1808.advancedchat.impl.utils;

import eu.pb4.placeholders.api.parsers.NodeParser;
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
import java.util.function.Predicate;

public class Socialspy {

    // Private Messages
    public static void send(CommandSourceStack source, ServerPlayer target, PlayerChatMessage message) {
        Config.Socialspy config = Config.instance().socialSpy;

        NodeParser placeholderParser = Formatter.placeholderParser((key) -> switch (key) {
            case "source" -> source.getDisplayName();
            case "target" -> target.getDisplayName();
            case "message" -> getPrivateMessageContent(message);
            default -> null;
        });

        MutableComponent text = Formatter.parse(config.prefix).append(Formatter.parse(config.privateMessage, placeholderParser));
        Socialspy.send(target.level().getServer(), text, (player) -> {
            Mode mode = DataManager.get(player).spyMode;
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

        NodeParser placeholderParser = Formatter.placeholderParser((key) -> switch (key) {
            case "channel" -> AdvancedChatAPI.getChannelPrefix(sender);
            case "sender" -> sender.getDisplayName();
            case "message" -> message.decoratedContent();
            default -> null;
        });

        MutableComponent text = Formatter.parse(config.prefix).append(Formatter.parse(config.channelMessage, placeholderParser));
        Socialspy.send(sender.level().getServer(), text, (player) -> {
            Mode mode = DataManager.get(player).spyMode;
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
