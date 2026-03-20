package me.wesley1808.advancedchat.impl.utils;

import eu.pb4.placeholders.api.Placeholder;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class PlaceHolders {

    public static void register() {

        register("channelprefix", (ctx, arg) -> {
            if (ctx.player() instanceof ServerPlayer sender) {
                return PlaceholderResult.value(AdvancedChatAPI.getChannelPrefix(sender));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }

    private static void register(String name, Placeholder.Handler<ServerPlaceholderContext, String> handler) {
        Placeholders.registerServer(Identifier.fromNamespaceAndPath("advancedchat", name), handler);
    }
}

