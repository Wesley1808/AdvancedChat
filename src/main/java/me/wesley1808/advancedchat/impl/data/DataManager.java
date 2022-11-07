package me.wesley1808.advancedchat.impl.data;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DataManager {
    private static final PlayerDataStorage<AdvancedChatData> DATA_STORAGE = new JsonDataStorage<>("advancedchat", AdvancedChatData.class, Json.PLAYER_DATA);

    public static void initialize() {
        PlayerDataApi.register(DATA_STORAGE);
    }

    @NotNull
    public static AdvancedChatData get(ServerPlayer player) {
        AdvancedChatData data = PlayerDataApi.getCustomDataFor(player, DATA_STORAGE);
        return data == null ? new AdvancedChatData() : data;
    }

    public static void modify(ServerPlayer player, Consumer<AdvancedChatData> consumer) {
        AdvancedChatData data = DataManager.get(player);
        consumer.accept(data);
        PlayerDataApi.setCustomDataFor(player, DATA_STORAGE, data);
    }
}
