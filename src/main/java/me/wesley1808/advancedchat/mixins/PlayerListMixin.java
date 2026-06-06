package me.wesley1808.advancedchat.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.wesley1808.advancedchat.impl.utils.Socialspy;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V", at = @At("HEAD"), cancellable = true)
    private void advancedchat$hideChatMessage(PlayerChatMessage message, Predicate<ServerPlayer> isFiltered, ServerPlayer senderPlayer, ChatType.Bound chatType, CallbackInfo ci) {
        if (senderPlayer != null && Util.shouldHideMessage(senderPlayer, message)) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/players/PlayerList;players:Ljava/util/List;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private List<ServerPlayer> advancedchat$filterPlayers(List<ServerPlayer> original, PlayerChatMessage message, Predicate<ServerPlayer> isFiltered, @Nullable ServerPlayer senderPlayer, ChatType.Bound chatType) {
        List<ServerPlayer> players = senderPlayer != null ? Util.filterIgnored(senderPlayer, original) : original;

        if (senderPlayer != null && !players.isEmpty() && chatType.chatType().is(ChatType.CHAT.identifier())) {
            List<ServerPlayer> receivers = Util.filterByChannel(senderPlayer, players);
            Socialspy.send(senderPlayer, receivers, message);
            return receivers;
        }

        return players;
    }
}
