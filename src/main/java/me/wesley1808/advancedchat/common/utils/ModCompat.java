package me.wesley1808.advancedchat.common.utils;

import net.fabricmc.loader.api.FabricLoader;

public class ModCompat {
    public static final boolean VANISH = isLoaded("melius-vanish");
    public static final boolean STYLED_CHAT = isLoaded("styledchat");

    private static boolean isLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
