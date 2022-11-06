package me.wesley1808.advancedchat.common.channels;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import me.wesley1808.advancedchat.common.predicates.AbstractChatPredicate;
import me.wesley1808.advancedchat.common.utils.Permission;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class ChatChannel {
    public String name;
    public boolean enabled = true;
    public boolean isStaff;
    public String permission;
    public String actionbar;
    public String prefix;
    public MinecraftPredicate canSee;

    public ChatChannel(String name, String permission, boolean isStaff, String prefix, String actionbar, MinecraftPredicate canSee) {
        this.name = name;
        this.permission = permission;
        this.isStaff = isStaff;
        this.prefix = prefix;
        this.actionbar = actionbar;
        this.canSee = canSee;
    }

    public static boolean notStaff(ChatChannel channel) {
        return channel == null || !channel.isStaff;
    }

    public boolean canPlayerUse(ServerPlayer player) {
        if (!this.enabled) {
            return false;
        }

        return this.hasPermission(player);
    }

    public boolean hasPermission(ServerPlayer player) {
        return this.permission == null || Permission.check(player, this.permission, 2);
    }

    public boolean canSee(ServerPlayer sender, ServerPlayer target) {
        if (!this.hasPermission(target)) {
            return false;
        }

        if (this.canSee == null) {
            return true;
        }

        PredicateContext context = AbstractChatPredicate.createContext(sender, target);
        return this.canSee.test(context).success();
    }

    public Component getPrefix(ServerPlayer sender) {
        return Placeholders.parseText(
                TextParserUtils.formatNodesSafe(this.prefix),
                PlaceholderContext.of(sender)
        );
    }

    @Nullable
    public Component getActionBarText(ServerPlayer sender) {
        if (this.actionbar == null || this.actionbar.isEmpty()) {
            return null;
        }

        return Placeholders.parseText(
                TextParserUtils.formatNodesSafe(this.actionbar),
                PlaceholderContext.of(sender)
        );
    }
}
