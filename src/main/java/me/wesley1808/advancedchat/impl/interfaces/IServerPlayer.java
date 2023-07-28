package me.wesley1808.advancedchat.impl.interfaces;

import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IServerPlayer {

    ClientboundSetActionBarTextPacket getActionBarPacket();

    @Nullable
    PlayerChatMessage getLastChatMessage();

    void setLastChatMessage(PlayerChatMessage message);

    @Nullable
    UUID getReplyTarget();

    void setReplyTarget(UUID uuid);

    void delayNextPacket();

    void updateActionBarPacket();

    @Nullable
    static PlayerChatMessage getLastChatMessage(ServerPlayer player) {
        return ((IServerPlayer) player).getLastChatMessage();
    }

    static void setLastChatMessage(ServerPlayer player, PlayerChatMessage message) {
        ((IServerPlayer) player).setLastChatMessage(message);
    }

    @Nullable
    static UUID getReplyTarget(ServerPlayer player) {
        return ((IServerPlayer) player).getReplyTarget();
    }

    static void setReplyTarget(ServerPlayer player, UUID uuid) {
        ((IServerPlayer) player).setReplyTarget(uuid);
    }

    static void updateActionBarPacket(ServerPlayer player) {
        ((IServerPlayer) player).updateActionBarPacket();
    }
}
