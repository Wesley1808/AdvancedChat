package me.wesley1808.advancedchat.impl;

import com.mojang.logging.LogUtils;
import eu.pb4.styledchat.StyledChatEvents;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.commands.*;
import me.wesley1808.advancedchat.impl.config.ConfigManager;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.predicates.Predicates;
import me.wesley1808.advancedchat.impl.utils.Filter;
import me.wesley1808.advancedchat.impl.utils.ModCompat;
import me.wesley1808.advancedchat.impl.utils.PlaceHolders;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.level.ServerPlayer;
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
            ReplyCommand.register(dispatcher);
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, chatType) -> Util.canSendChatMessage(sender, message, false));

        ServerMessageEvents.ALLOW_COMMAND_MESSAGE.register((message, source, chatType) -> {
            ServerPlayer sender = source.getPlayer();
            return sender == null || Util.canSendChatMessage(sender, message, true);
        });

        if (ModCompat.STYLEDCHAT) {
            StyledChatEvents.PRE_MESSAGE_CONTENT.register((message, context) -> {
                return context.hasPlayer() && Filter.isEnabled() ? Filter.process(message).filteredOrEmpty() : message;
            });
        }
    }
}