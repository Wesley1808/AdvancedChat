package me.wesley1808.advancedchat.impl.utils;

import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PlaceHolders {

    public static void register() {

        register("channelprefix", (ctx, arg) -> {
            ServerPlayer sender = ctx.player();

            if (sender != null) {
                return PlaceholderResult.value(AdvancedChatAPI.getChannelPrefix(sender));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }

    private static void register(String name, PlaceholderHandler handler) {
        Placeholders.register(new ResourceLocation("advancedchat", name), handler);
    }
}

