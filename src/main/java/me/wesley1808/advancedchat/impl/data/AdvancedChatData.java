package me.wesley1808.advancedchat.impl.data;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.utils.Socialspy;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class AdvancedChatData {
    @NotNull
    public Set<ChatChannel> mutedChannels = new ObjectOpenHashSet<>();

    @NotNull
    public Set<UUID> ignored = new ObjectOpenHashSet<>();

    @NotNull
    public Socialspy.Mode spyMode = Socialspy.Mode.NONE;

    @Nullable
    public ChatChannel channel = null;

    public boolean isIgnoring(ServerPlayer player) {
        return this.ignored.contains(player.getUUID());
    }

    public boolean ignore(UUID uuid) {
        return this.ignored.add(uuid);
    }

    public boolean unignore(UUID uuid) {
        return this.ignored.remove(uuid);
    }

    public boolean hasMuted(ChatChannel channel) {
        return this.mutedChannels.contains(channel);
    }

    public boolean toggleMute(ChatChannel channel) {
        if (this.hasMuted(channel)) {
            this.mutedChannels.remove(channel);
            return true;
        } else {
            this.mutedChannels.add(channel);
            return false;
        }
    }
}
