package me.wesley1808.advancedchat.common.config;

import me.wesley1808.advancedchat.common.channels.ChatChannel;
import me.wesley1808.advancedchat.common.predicates.DistanceComparisonPredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;

import static net.minecraft.advancements.critereon.MinMaxBounds.Doubles.ANY;

public class Config {
    protected static Config instance = new Config();

    public boolean actionbar = true;
    public String hoverText = "<dark_aqua>Receivers: <dark_gray>[${receivers}]";
    public String receiver = "<green>${player}</green>";
    public String selfPrefix = "<dark_gray>[<aqua>Self</aqua>]";
    public Messages messages = new Messages();

    public ChatChannel[] channels = {
            new ChatChannel(
                    "staff",
                    "channel.staff",
                    true,
                    "<dark_gray>[<aqua>Staff</aqua>] ",
                    "<dark_aqua>Chat Mode: <green>Staff",
                    null
            ),
            new ChatChannel(
                    "admin",
                    "channel.admin",
                    true,
                    "<dark_gray>[<red>Admin</red>] ",
                    "<dark_aqua>Chat Mode: <green>Admin",
                    null
            ),
            new ChatChannel(
                    "local",
                    null,
                    false,
                    "<dark_gray>[<aqua>Local</aqua>] ",
                    "<dark_aqua>Chat Mode: <green>Local",
                    new DistanceComparisonPredicate(DistancePredicate.horizontal(MinMaxBounds.Doubles.between(0.0D, 256.0D)))
            ),
            new ChatChannel(
                    "world",
                    null,
                    false,
                    "<dark_gray>[<aqua>%world:name%</aqua>] ",
                    "<dark_aqua>Chat Mode: <green>World",
                    new DistanceComparisonPredicate(new DistancePredicate(ANY, ANY, ANY, ANY, ANY))
            )
    };

    public static Config instance() {
        return instance;
    }

    public static class Messages {
        public String switchedChannels = "<dark_aqua>Chat Mode -> <green>${channel}";
        public String ignored = "<red>${player} is ignoring you.";
        public String ignoredPlayer = "<dark_aqua>You are now ignoring <green>${player}";
        public String unignoredPlayer = "<dark_aqua>You are no longer ignoring <green>${player}";
        public String cannotIgnoreSelf = "<red>You cannot ignore yourself!";
        public String alreadyIgnored = "<red>You are already ignoring ${player}!";
        public String notAlreadyIgnored = "<red>You aren't ignoring ${player}!";
        public String channelNotFound = "<red>Unable to find a channel with name '${name}'!";
    }
}
