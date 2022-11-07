package me.wesley1808.advancedchat.api;

import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AdvancedChatAPI {

    @Nullable
    public static ChatChannel getChannel(String name) {
        return Channels.get(name);
    }

    @NotNull
    public static Component getChannelPrefix(ServerPlayer player) {
        return Util.getChannelPrefix(player);
    }

    @NotNull
    public static AdvancedChatData getData(ServerPlayer player) {
        return DataManager.get(player);
    }

    public static void modifyData(ServerPlayer player, Consumer<AdvancedChatData> consumer) {
        DataManager.modify(player, consumer);
    }
}
