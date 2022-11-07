package me.wesley1808.advancedchat.impl.config;

import me.wesley1808.advancedchat.impl.AdvancedChat;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.data.Json;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigManager {
    private static final File CONFIG = new File(FabricLoader.getInstance().getConfigDir().toFile(), "advanced-chat.json");

    public static void load() {
        if (!CONFIG.exists()) {
            save();
            return;
        }

        try (BufferedReader reader = getReader(CONFIG)) {
            Config.instance = Json.CONFIG.fromJson(reader, Config.class);

            for (ChatChannel channel : Config.instance.channels) {
                channel.name = channel.name.toLowerCase().replace(" ", "");
            }
        } catch (Exception ex) {
            AdvancedChat.getLogger().error("Failed to load config!", ex);
        }

        save();
    }

    private static void save() {
        try (BufferedWriter writer = getWriter(CONFIG)) {
            writer.write(Json.CONFIG.toJson(Config.instance));
        } catch (Exception ex) {
            AdvancedChat.getLogger().error("Failed to save config!", ex);
        }
    }

    private static BufferedWriter getWriter(File file) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
    }

    private static BufferedReader getReader(File file) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }
}
