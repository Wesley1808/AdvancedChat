package me.wesley1808.advancedchat.common.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AdvancedChatEvents {
    public static final Event<ChannelMessageEvent> CHANNEL_MESSAGE_EVENT = EventFactory.createArrayBacked(ChannelMessageEvent.class, (listeners) -> (sender, receivers, message) -> {
        for (ChannelMessageEvent listener : listeners) {
            listener.onSentMessage(sender, receivers, message);
        }
    });


    @FunctionalInterface
    public interface ChannelMessageEvent {
        void onSentMessage(ServerPlayer player, List<ServerPlayer> receivers, PlayerChatMessage message);
    }
}
