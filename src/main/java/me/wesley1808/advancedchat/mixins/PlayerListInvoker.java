package me.wesley1808.advancedchat.mixins;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;

@Mixin(PlayerList.class)
public interface PlayerListInvoker {

    @Invoker("broadcastChatMessage")
    void invokeBroadcastChatMessage(PlayerChatMessage playerChatMessage, Predicate<ServerPlayer> predicate, @Nullable ServerPlayer serverPlayer, ChatType.Bound bound);
}
