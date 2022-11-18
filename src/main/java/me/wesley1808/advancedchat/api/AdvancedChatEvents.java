package me.wesley1808.advancedchat.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public class AdvancedChatEvents {

    public static final Event<MessageFilteredEvent> MESSAGE_FILTERED = EventFactory.createArrayBacked(MessageFilteredEvent.class, (listeners) -> (sender, message) -> {
        for (MessageFilteredEvent listener : listeners) {
            listener.onMessageFiltered(sender, message);
        }
    });


    @FunctionalInterface
    public interface MessageFilteredEvent {
        void onMessageFiltered(ServerPlayer sender, String message);
    }
}
