package me.wesley1808.advancedchat.impl.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;

import java.util.function.Predicate;

public class Permission {
    private static final String BASE = "advancedchat";
    public static final Identifier BYPASS_IGNORE = permission("bypass.ignore");
    public static final Identifier BYPASS_ANTISPAM_SIMILARITY = permission("bypass.antispam.similarity");
    public static final Identifier BYPASS_ANTISPAM_COOLDOWN = permission("bypass.antispam.cooldown");
    public static final Identifier BYPASS_CHANNEL_MUTE = permission("bypass.channel_mute");
    public static final Identifier SOCIALSPY = permission("command.socialspy");
    public static final Identifier RELOAD = permission("command.reload");

    public static boolean check(CommandSourceStack source, Identifier perm, PermissionLevel level) {
        return source.checkPermission(perm, level);
    }

    public static boolean check(ServerPlayer source, Identifier perm, PermissionLevel level) {
        return source.checkPermission(perm, level);
    }

    public static Predicate<CommandSourceStack> require(Identifier perm, PermissionLevel level) {
        return (s) -> s.checkPermission(perm, level);
    }

    public static boolean checkExternal(ServerPlayer source, String perm, PermissionLevel level) {
        Identifier id = Identifier.tryBySeparator(perm, '.');
        if (id == null) {
            id = Identifier.tryParse(perm);
            if (id == null) {
                // Unable to parse permission string.
                return false;
            }
        }

        return check(source, id, level);
    }

    private static Identifier permission(String perm) {
        return Identifier.fromNamespaceAndPath(BASE, perm);
    }
}