package me.wesley1808.advancedchat.mixins;

import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import me.wesley1808.advancedchat.impl.config.Config;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(ServerMessageEvents.class)
public class ServerMessageEventsMixin {

    @Inject(method = "lambda$static$6", at = @At(value = "HEAD"), require = 0, cancellable = true)
    private static void advancedchat$cancelEvent(ServerMessageEvents.ChatMessage[] handlers, PlayerChatMessage message, ServerPlayer sender, ChatType.Bound params, CallbackInfo ci) {
        // Cancels the ServerMessageEvents.CHAT_MESSAGE event if the message was sent through a chat channel.
        // This way mods listening for this event won't trigger, as they would expect these messages to be public - even if they aren't.
        if (!Config.instance().alwaysTriggerMessageEvent && !AdvancedChatAPI.isPublicChat(sender)) {
            ci.cancel();
        }
    }
}
