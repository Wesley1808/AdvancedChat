package me.wesley1808.advancedchat.api;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

@SuppressWarnings("unused")
public class AdvancedChatAPI {

    /**
     * Returns the prefix of the channel the player is currently in.
     * <p>
     * If the player is not in a channel, it will return an empty text component.
     */
    public static Component getChannelPrefix(ServerPlayer player) {
        return Util.getChannelPrefix(player);
    }

    /**
     * Hook for other mods to check if a message sent by the specified player is actually sent globally.
     * <p>
     * Minecraft-Discord bridges can use this check to prevent (private) channel messages from being sent to a public discord channel.
     */
    public static boolean isPublicChat(ServerPlayer player) {
        return Util.isPublicChat(player);
    }

    /**
     * Verifies that the command source isn't ignored by any of the targets.
     *
     * @throws CommandSyntaxException: If the command source is ignored by any of the targets.
     */
    public static void throwIfIgnored(CommandSourceStack source, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        Util.throwIfIgnored(source, targets);
    }
}
