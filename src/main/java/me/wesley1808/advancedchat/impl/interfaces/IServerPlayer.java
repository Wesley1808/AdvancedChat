package me.wesley1808.advancedchat.impl.interfaces;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface IServerPlayer {

    void resetActionBarPacket();

    void setReplyTarget(UUID uuid);

    UUID getReplyTarget();

    static void resetActionBarPacket(ServerPlayer player) {
        ((IServerPlayer) player).resetActionBarPacket();
    }

    static void setReplyTarget(ServerPlayer player, UUID uuid) {
        ((IServerPlayer) player).setReplyTarget(uuid);
    }

    static UUID getReplyTarget(ServerPlayer player) {
        return ((IServerPlayer) player).getReplyTarget();
    }
}
