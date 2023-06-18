package me.wesley1808.advancedchat.impl.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.pb4.placeholders.api.Placeholders;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.drex.vanish.api.VanishAPI;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.data.DataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
        String receiver = Config.instance().receiver;
        String hover = Config.instance().hoverText;
        if (receiver.isEmpty() || hover.isEmpty()) {
            return component;
        }

        List<ServerPlayer> players = sender.server.getPlayerList().getPlayers();

        // Filter out all players that ignore the sender and that cannot view the chat channel.
        List<ServerPlayer> filtered = Util.filterByChannel(sender, Util.filterIgnored(sender, players));

        // Filter out vanished players and spectators to prevent them from being exposed.
        filtered.removeIf(player -> player.isSpectator() || Util.isVanished(player));
        if (filtered.isEmpty()) {
            return component;
        }

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
        if (data.channel != null) {
            prefix = data.channel.getPrefix(player);
        }

        return prefix == null ? Component.empty() : Util.addHoverText((MutableComponent) prefix, player);
    }

    public static boolean canSendChatMessage(ServerPlayer sender, boolean isGlobal) {
        AdvancedChatData data = DataManager.get(sender);
        ChatChannel channel = isGlobal ? null : data.channel;
        Config.Messages messages = Config.instance().messages;

        if (data.hasMuted(channel)) {
            sender.sendSystemMessage(Formatter.parse(messages.channelMuted));
            return false;
        }

        if (ChatChannel.notStaff(channel) && Util.isVanished(sender)) {
            sender.sendSystemMessage(Formatter.parse(messages.cannotSendVanished));
            return false;
        }

        return true;
    }

    public static boolean shouldHideMessage(ServerPlayer sender, PlayerChatMessage message) {
        FilterMask mask = message.filterMask();

        if (!mask.isEmpty() && Config.instance().filter.hideFilteredMessages) {
            String content = message.signedContent();
            String censored = mask.apply(content);
            sender.sendSystemMessage(Placeholders.parseText(
                    Formatter.parseNodes(Config.instance().messages.cannotSendFiltered),
                    Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("message", Component.literal(censored != null ? censored : content).withStyle(ChatFormatting.RED))
            ));
            return true;
        }

        return false;
    }

    public static List<ServerPlayer> filterByChannel(ServerPlayer sender, List<ServerPlayer> players) {
        AdvancedChatData data = DataManager.get(sender);
        if (data.channel == null) {
            return players;
        }

        ObjectArrayList<ServerPlayer> filtered = new ObjectArrayList<>();
        for (ServerPlayer target : players) {
            if (data.channel.canSee(sender, target)) {
                filtered.add(target);
            }
        }

        return filtered;
    }

    public static List<ServerPlayer> filterIgnored(ServerPlayer sender, List<ServerPlayer> players) {
        final boolean bypassesIgnore = Permission.check(sender, Permission.BYPASS_IGNORE, 2);
        final boolean bypassesMute = Permission.check(sender, Permission.BYPASS_CHANNEL_MUTE, 2);

        ObjectArrayList<ServerPlayer> filtered = new ObjectArrayList<>();
        ChatChannel channel = DataManager.get(sender).channel;

        for (ServerPlayer target : players) {
            AdvancedChatData data = DataManager.get(target);
            if ((bypassesMute || !data.hasMuted(channel)) && (bypassesIgnore || !data.isIgnoring(sender))) {
                filtered.add(target);
            }
        }

        return filtered;
    }

    public static boolean isChat(MinecraftServer server, ChatType type) {
        Optional<? extends Registry<ChatType>> optional = server.registryAccess().registry(Registries.CHAT_TYPE);
        ResourceLocation key = optional.map(registry -> registry.getKey(type)).orElse(null);
        return ChatType.CHAT.location().equals(key);
    }

    public static boolean isVanished(Entity entity) {
        return ModCompat.VANISH && VanishAPI.isVanished(entity);
    }

    public static boolean isPublicChat(ServerPlayer sender) {
        return !Util.isVanished(sender) && DataManager.get(sender).channel == null;
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

    public static void playSound(ServerPlayer sender, Collection<ServerPlayer> targets, Config.Sound config) {
        if (config.enabled && config.sound != null) {
            Holder<SoundEvent> sound = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(config.sound);
            long seed = sender.serverLevel().getRandom().nextLong();

            for (ServerPlayer target : targets) {
                if (sender != target) {
                    target.connection.send(new ClientboundSoundPacket(
                            sound,
                            SoundSource.RECORDS,
                            target.getX(),
                            target.getY(),
                            target.getZ(),
                            config.volume,
                            config.pitch,
                            seed
                    ));
                }
            }
        }
    }

    public static boolean isOverlayPacket(Packet<?> packet) {
        return packet instanceof ClientboundSetActionBarTextPacket ||
               packet instanceof ClientboundSystemChatPacket chatPacket && chatPacket.overlay();
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
