package me.wesley1808.advancedchat.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.utils.Socialspy;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V", at = @At("HEAD"), cancellable = true)
    private void advancedchat$hideChatMessage(PlayerChatMessage message, Predicate<ServerPlayer> predicate, ServerPlayer sender, ChatType.Bound bound, CallbackInfo ci) {
        if (sender != null && Util.shouldHideMessage(sender, message)) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/players/PlayerList;players:Ljava/util/List;"
            )
    )
    private List<ServerPlayer> advancedchat$filterPlayers(List<ServerPlayer> original, PlayerChatMessage message, Predicate<ServerPlayer> predicate, @Nullable ServerPlayer sender, ChatType.Bound bound) {
        List<ServerPlayer> players = sender != null ? Util.filterIgnored(sender, original) : original;

        if (sender != null && !players.isEmpty() && Util.isChat(this.server, bound.chatType())) {
            List<ServerPlayer> receivers = Util.filterByChannel(sender, players);
            Util.playSound(sender, receivers, Config.instance().channelMessageSound);
            Socialspy.send(sender, receivers, message);
            return receivers;
        }

        return players;
    }
}
