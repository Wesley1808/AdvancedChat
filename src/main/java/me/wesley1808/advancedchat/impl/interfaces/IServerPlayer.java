package me.wesley1808.advancedchat.impl.interfaces;

import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IServerPlayer {

    ClientboundSetActionBarTextPacket advancedchat$getActionBarPacket();

    @Nullable
    PlayerChatMessage advancedchat$getLastChatMessage();

    void advancedchat$setLastChatMessage(PlayerChatMessage message);

    @Nullable
    UUID advancedchat$getReplyTarget();

    void advancedchat$setReplyTarget(UUID uuid);

    void advancedchat$delayNextPacket();

    void advancedchat$updateActionBarPacket();

    @Nullable
    static PlayerChatMessage getLastChatMessage(ServerPlayer player) {
        return ((IServerPlayer) player).advancedchat$getLastChatMessage();
    }

    static void setLastChatMessage(ServerPlayer player, PlayerChatMessage message) {
        ((IServerPlayer) player).advancedchat$setLastChatMessage(message);
    }

    @Nullable
    static UUID getReplyTarget(ServerPlayer player) {
        return ((IServerPlayer) player).advancedchat$getReplyTarget();
    }

    static void setReplyTarget(ServerPlayer player, UUID uuid) {
        ((IServerPlayer) player).advancedchat$setReplyTarget(uuid);
    }

    static void updateActionBarPacket(ServerPlayer player) {
        ((IServerPlayer) player).advancedchat$updateActionBarPacket();
    }
}
