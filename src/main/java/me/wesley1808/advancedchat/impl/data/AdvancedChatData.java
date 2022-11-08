package me.wesley1808.advancedchat.impl.data;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.utils.Socialspy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class AdvancedChatData {
    @NotNull
    public Set<UUID> ignored = new ObjectOpenHashSet<>();

    @Nullable
    public ChatChannel channel = null;

    public Socialspy.Mode spyMode = Socialspy.Mode.NONE;

    public boolean unignore(UUID uuid) {
        return this.ignored.remove(uuid);
    }

    public boolean ignore(UUID uuid) {
        return this.ignored.add(uuid);
    }
}
