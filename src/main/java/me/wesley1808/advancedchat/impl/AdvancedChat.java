package me.wesley1808.advancedchat.impl;

import com.mojang.logging.LogUtils;
import eu.pb4.styledchat.StyledChatEvents;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.commands.AdvancedChatCommands;
import me.wesley1808.advancedchat.impl.commands.ChatCommand;
import me.wesley1808.advancedchat.impl.commands.IgnoreCommand;
import me.wesley1808.advancedchat.impl.commands.SocialspyCommand;
import me.wesley1808.advancedchat.impl.config.ConfigManager;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.predicates.Predicates;
import me.wesley1808.advancedchat.impl.utils.Filter;
import me.wesley1808.advancedchat.impl.utils.ModCompat;
import me.wesley1808.advancedchat.impl.utils.PlaceHolders;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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
        DataManager.initialize();
        PlaceHolders.register();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ConfigManager.load();
            Channels.register();
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            AdvancedChatCommands.register(dispatcher);
            ChatCommand.register(dispatcher);
            IgnoreCommand.register(dispatcher);
            SocialspyCommand.register(dispatcher);
        });

        if (ModCompat.STYLEDCHAT) {
            StyledChatEvents.PRE_MESSAGE_CONTENT.register((message, context) -> {
                return context.hasPlayer() ? Filter.filterStyledChat(message) : message;
            });
        }
    }
}
