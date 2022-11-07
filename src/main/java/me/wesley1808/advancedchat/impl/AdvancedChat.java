package me.wesley1808.advancedchat.impl;

import com.mojang.logging.LogUtils;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.commands.AdvancedChatCommands;
import me.wesley1808.advancedchat.impl.commands.ChatCommand;
import me.wesley1808.advancedchat.impl.commands.IgnoreCommand;
import me.wesley1808.advancedchat.impl.commands.SocialspyCommand;
import me.wesley1808.advancedchat.impl.config.ConfigManager;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.predicates.Predicates;
import me.wesley1808.advancedchat.impl.utils.PlaceHolders;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;

public class AdvancedChat implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[AdvancedChat] Initializing...");
        Predicates.register();
        ConfigManager.load();
        DataManager.initialize();
        PlaceHolders.register();
        Channels.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            AdvancedChatCommands.register(dispatcher);
            ChatCommand.register(dispatcher);
            IgnoreCommand.register(dispatcher);
            SocialspyCommand.register(dispatcher);
        });
    }
}
