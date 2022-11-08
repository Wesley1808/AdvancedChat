package me.wesley1808.advancedchat.impl.utils;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

public class Permission {
    public static final String BASE = "advancedchat.";
    public static final String BYPASS_IGNORE = permission("bypass.ignore");
    public static final String SOCIALSPY = permission("command.socialspy");
    public static final String RELOAD = permission("command.reload");

    public static boolean check(SharedSuggestionProvider source, String perm, int level) {
        return Permissions.check(source, perm, level);
    }

    public static boolean check(ServerPlayer source, String perm, int level) {
        return Permissions.check(source, perm, level);
    }

    public static Predicate<CommandSourceStack> require(String perm, int level) {
        return Permissions.require(perm, level);
    }

    private static String permission(String perm) {
        return BASE + perm;
    }
}