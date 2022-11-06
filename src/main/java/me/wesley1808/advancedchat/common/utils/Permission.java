package me.wesley1808.advancedchat.common.utils;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

public class Permission {

    public static boolean check(SharedSuggestionProvider source, String perm, int level) {
        return Permissions.check(source, permission(perm), level);
    }

    public static boolean check(ServerPlayer target, String perm, int level) {
        return check(target.createCommandSourceStack(), perm, level);
    }

    public static Predicate<CommandSourceStack> require(String perm, int level) {
        return source -> check(source, perm, level);
    }

    private static String permission(String perm) {
        return "advancedchat." + perm;
    }
}