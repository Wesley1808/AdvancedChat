package me.wesley1808.advancedchat.impl.utils;

import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.interfaces.IServerPlayer;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.StringUtils;

public class AntiSpam {

    public static boolean checkForSpam(ServerPlayer sender, PlayerChatMessage message) {
        Config.AntiSpam config = Config.instance().antiSpam;
        PlayerChatMessage lastMessage = IServerPlayer.getLastChatMessage(sender);

        if (lastMessage != null && config.enabled) {
            // Check for similar messages
            if (config.blockSimilarMessages && !Permission.check(sender, Permission.BYPASS_ANTISPAM_SIMILARITY, 2)) {
                int similarity = AntiSpam.getSimilarity(lastMessage.signedContent(), message.signedContent(), config.similarityMinLength);
                if (similarity >= config.similarityThreshold) {
                    sender.sendSystemMessage(Formatter.parse(Config.instance().messages.cannotSendSimilar));
                    return true;
                }
            }

            // Check for the message cooldown
            if (message.timeStamp().isBefore(lastMessage.timeStamp().plusMillis(config.messageCooldown)) && !Permission.check(sender, Permission.BYPASS_ANTISPAM_COOLDOWN, 2)) {
                sender.sendSystemMessage(Formatter.parse(Config.instance().messages.cannotSendSpam));
                return true;
            }
        }

        IServerPlayer.setLastChatMessage(sender, message);
        return false;
    }

    public static int getSimilarity(String first, String second, int minLength) {
        int longest = Math.max(first.length(), second.length());
        if (longest < minLength) {
            return 0;
        }

        if (longest == 0 || first.equalsIgnoreCase(second)) {
            return 100;
        }

        int distance = StringUtils.getLevenshteinDistance(first.toLowerCase(), second.toLowerCase());
        return (longest - distance) * 100 / longest;
    }
}
