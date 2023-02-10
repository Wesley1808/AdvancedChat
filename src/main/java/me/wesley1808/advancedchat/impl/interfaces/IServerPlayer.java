package me.wesley1808.advancedchat.impl.interfaces;

import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface IServerPlayer {

    ClientboundSetActionBarTextPacket getActionBarPacket();

    UUID getReplyTarget();

    void setReplyTarget(UUID uuid);

    void delayNextPacket();

    void updateActionBarPacket();

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
