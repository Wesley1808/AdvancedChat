package me.wesley1808.advancedchat.common.data;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.wesley1808.advancedchat.common.channels.ChatChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class AdvancedChatData {
    @NotNull
    public Set<UUID> ignored = new ObjectOpenHashSet<>();

    @Nullable
    public ChatChannel channel;

    public boolean unignore(UUID uuid) {
        return this.ignored.remove(uuid);
    }

    public boolean ignore(UUID uuid) {
        return this.ignored.add(uuid);
    }
}
