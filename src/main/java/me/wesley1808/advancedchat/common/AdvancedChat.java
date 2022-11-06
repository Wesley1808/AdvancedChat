package me.wesley1808.advancedchat.common;

import com.mojang.logging.LogUtils;
import eu.pb4.predicate.api.PredicateRegistry;
import me.wesley1808.advancedchat.common.channels.Channels;
import me.wesley1808.advancedchat.common.commands.AdvancedChatCommands;
import me.wesley1808.advancedchat.common.commands.ChatCommand;
import me.wesley1808.advancedchat.common.commands.IgnoreCommand;
import me.wesley1808.advancedchat.common.config.ConfigManager;
import me.wesley1808.advancedchat.common.data.DataManager;
import me.wesley1808.advancedchat.common.predicates.DistanceComparisonPredicate;
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
        PredicateRegistry.register(DistanceComparisonPredicate.ID, DistanceComparisonPredicate.CODEC);
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
}
