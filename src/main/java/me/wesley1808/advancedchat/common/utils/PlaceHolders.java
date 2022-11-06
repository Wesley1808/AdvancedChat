package me.wesley1808.advancedchat.common.utils;

import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PlaceHolders {

    public static void register() {

        register("channelprefix", (ctx, arg) -> {
            ServerPlayer sender = ctx.player();

            if (sender != null) {
                Component component = Util.getChannelPrefix(sender);
                return PlaceholderResult.value(component == null ? Component.empty() : Util.addHoverText((MutableComponent) component, sender));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }

    private static void register(String name, PlaceholderHandler handler) {
        Placeholders.register(new ResourceLocation("advancedchat", name), handler);
    }
}

