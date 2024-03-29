package me.wesley1808.advancedchat.impl.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.utils.Formatter;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;

import java.util.Optional;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.GameProfileArgument.gameProfile;

public class IgnoreCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ignore")
                .then(argument(Util.TARGET, gameProfile())
                        .executes(context -> execute(context.getSource().getPlayerOrException(), Util.getProfile(context), true))
                )
        );

        dispatcher.register(literal("unignore")
                .then(argument(Util.TARGET, gameProfile())
                        .suggests(ignoredPlayers())
                        .executes(context -> execute(context.getSource().getPlayerOrException(), Util.getProfile(context), false))
                )
        );
    }

    private static int execute(ServerPlayer source, GameProfile target, boolean ignore) {
        Config.Messages messages = Config.instance().messages;
        if (ignore && source.getUUID().equals(target.getId())) {
            source.sendSystemMessage(Formatter.parse(messages.cannotIgnoreSelf));
            return 0;
        }

        AdvancedChatData data = DataManager.get(source);
        if (ignore) {
            if (data.ignore(target.getId())) {
                sendMessage(source, messages.ignoredPlayer, target);
            } else {
                sendMessage(source, messages.alreadyIgnored, target);
            }
        } else {
            if (data.unignore(target.getId())) {
                sendMessage(source, messages.unignoredPlayer, target);
            } else {
                sendMessage(source, messages.notAlreadyIgnored, target);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void sendMessage(ServerPlayer source, String text, GameProfile target) {
        source.sendSystemMessage(Formatter.parse(
                text.replace("${player}", target.getName())
        ));
    }

    private static SuggestionProvider<CommandSourceStack> ignoredPlayers() {
        return (ctx, builder) -> {
            CommandSourceStack source = ctx.getSource();
            AdvancedChatData data = DataManager.get(source.getPlayerOrException());
            GameProfileCache profileCache = source.getServer().getProfileCache();

            return Util.suggest(builder, Util.map(data.ignored, (uuid) -> {
                Optional<GameProfile> profile = profileCache != null ? profileCache.get(uuid) : Optional.empty();
                return profile.map(GameProfile::getName).orElse(null);
            }));
        };
    }
}
