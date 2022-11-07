package me.wesley1808.advancedchat.impl.channels;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.wesley1808.advancedchat.impl.config.Config;

public class Channels {
    private static final Object2ObjectOpenHashMap<String, ChatChannel> CHANNELS = new Object2ObjectOpenHashMap<>();

    public static void register() {
        CHANNELS.clear();
        for (ChatChannel channel : Config.instance().channels) {
            if (channel.enabled) {
                CHANNELS.put(channel.name, channel);
            }
        }
    }

    public static ChatChannel get(String name) {
        return CHANNELS.get(name);
    }

    public static Object2ObjectOpenHashMap<String, ChatChannel> getAll() {
        return CHANNELS;
    }
}
