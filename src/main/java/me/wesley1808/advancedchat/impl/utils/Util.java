package me.wesley1808.advancedchat.impl.utils;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.drex.vanish.api.VanishAPI;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.interfaces.IServerPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Util {
    public static final String TARGET = "target";
    private static final SimpleCommandExceptionType ONE_PLAYER_EXCEPTION = new SimpleCommandExceptionType(Component.literal("You can only select one player!"));

    public static GameProfile getProfile(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, TARGET);
        if (profiles.size() > 1) {
            throw ONE_PLAYER_EXCEPTION.create();
        }

        return profiles.iterator().next();
    }

    public static MutableComponent addHoverText(MutableComponent component, ServerPlayer sender) {
        String hover = Config.instance().hoverText;
        if (hover.isEmpty()) return component;

        List<ServerPlayer> players = sender.server.getPlayerList().getPlayers();
        List<ServerPlayer> filtered = Util.filterByChannel(sender);
        if (players == filtered) return component;

        filtered.removeIf(player -> player.isSpectator() || Util.isVanished(player));
        if (filtered.isEmpty()) return component;

        String receiver = Config.instance().receiver;
        String receivers = String.join(", ", Util.map(filtered, player ->
                receiver.replace("${player}", player.getScoreboardName())
        ));

        return component.withStyle(style -> style.withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Formatter.parse(hover.replace("${receivers}", receivers))
        )));
    }

    public static Component getChannelPrefix(ServerPlayer player) {
        Component prefix = null;

        AdvancedChatData data = DataManager.get(player);
        if (!ChatChannel.isStaff(data.channel) && isVanished(player)) {
            prefix = Formatter.parse(Config.instance().selfPrefix);
        } else if (data.channel != null) {
            prefix = data.channel.getPrefix(player);
        }

        return prefix == null ? Component.empty() : Util.addHoverText((MutableComponent) prefix, player);
    }

    public static List<ServerPlayer> filterByChannel(ServerPlayer sender) {
        return filterByChannel(sender, sender.server.getPlayerList().getPlayers());
    }

    public static List<ServerPlayer> filterByChannel(ServerPlayer sender, List<ServerPlayer> players) {
        AdvancedChatData data = DataManager.get(sender);
        return !ChatChannel.isStaff(data.channel) && isVanished(sender)
                ? Lists.newArrayList(sender)
                : getPlayersIn(sender, data.channel, players);
    }

    public static List<ServerPlayer> getPlayersIn(ServerPlayer sender, @Nullable ChatChannel channel, List<ServerPlayer> players) {
        if (channel == null) {
            return players;
        }

        ObjectArrayList<ServerPlayer> filtered = new ObjectArrayList<>();
        for (ServerPlayer target : players) {
            if (channel.canSee(sender, target)) {
                filtered.add(target);
            }
        }

        return filtered;
    }

    public static List<ServerPlayer> filterIgnored(ServerPlayer sender) {
        return filterIgnored(sender, sender.server.getPlayerList().getPlayers());
    }

    public static List<ServerPlayer> filterIgnored(ServerPlayer sender, Collection<ServerPlayer> players) {
        if (Permission.check(sender, Permission.BYPASS_IGNORE, 2)) {
            return new ObjectArrayList<>(players);
        }

        ObjectArrayList<ServerPlayer> filtered = new ObjectArrayList<>();
        for (ServerPlayer target : players) {
            AdvancedChatData data = DataManager.get(target);
            if (!data.ignored.contains(sender.getUUID())) {
                filtered.add(target);
            }
        }

        return filtered;
    }

    public static void resetActionBarPacket(ServerPlayer player) {
        ((IServerPlayer) player).resetActionBarPacket();
    }

    public static boolean isChat(MinecraftServer server, ChatType type) {
        Optional<? extends Registry<ChatType>> optional = server.registryAccess().registry(Registry.CHAT_TYPE_REGISTRY);
        ResourceLocation key = optional.map(registry -> registry.getKey(type)).orElse(null);
        return ChatType.CHAT.location().equals(key);
    }

    public static boolean isVanished(Entity entity) {
        return ModCompat.VANISH && VanishAPI.isVanished(entity);
    }

    public static boolean isPublicChat(ServerPlayer sender) {
        return !isVanished(sender) && DataManager.get(sender).channel == null;
    }

    public static void throwIfIgnored(CommandSourceStack source, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        ServerPlayer sender = source.getPlayer();
        if (sender != null && !Permission.check(source, Permission.BYPASS_IGNORE, 2)) {
            for (ServerPlayer target : targets) {
                AdvancedChatData data = DataManager.get(target);
                if (data.ignored.contains(sender.getUUID())) {
                    throw new SimpleCommandExceptionType(Formatter.parse(Config.instance().messages.ignored.replace("${player}", target.getScoreboardName()))).create();
                }
            }
        }
    }

    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> function) {
        List<R> result = new ObjectArrayList<>(collection.size());
        for (T value : collection) {
            R mapped = function.apply(value);
            if (mapped != null) {
                result.add(mapped);
            }
        }

        return result;
    }

    public static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, Collection<String> suggestions) {
        if (suggestions.isEmpty()) {
            return Suggestions.empty();
        }

        for (String suggestion : suggestions) {
            builder.suggest(suggestion);
        }

        return builder.buildFuture();
    }
}
