package me.wesley1808.advancedchat.common;

import com.mojang.logging.LogUtils;
import me.wesley1808.advancedchat.common.channels.Channels;
import me.wesley1808.advancedchat.common.commands.AdvancedChatCommands;
import me.wesley1808.advancedchat.common.commands.ChatCommand;
import me.wesley1808.advancedchat.common.commands.IgnoreCommand;
import me.wesley1808.advancedchat.common.config.ConfigManager;
import me.wesley1808.advancedchat.common.data.DataManager;
import me.wesley1808.advancedchat.common.predicates.Predicates;
import me.wesley1808.advancedchat.common.utils.ModCompat;
import me.wesley1808.advancedchat.common.utils.PlaceHolders;
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
        this.verifyStyledChat();
        Predicates.register();
        ConfigManager.load();
        DataManager.initialize();
        PlaceHolders.register();
        Channels.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            AdvancedChatCommands.register(dispatcher);
            ChatCommand.register(dispatcher);
            IgnoreCommand.register(dispatcher);
        });
    }

    private void verifyStyledChat() {
        if (!ModCompat.STYLED_CHAT) {
            LOGGER.warn("|----------------------------------------------------------------|");
            LOGGER.warn("| [AdvancedChat] StyledChat was not found.                       |");
            LOGGER.warn("|                                                                |");
            LOGGER.warn("| Some important features in this mod won't work without it.     |");
            LOGGER.warn("|                                                                |");
            LOGGER.warn("| You can download it here: https://modrinth.com/mod/styled-chat |");
            LOGGER.warn("|----------------------------------------------------------------|");
        }
    }

}
