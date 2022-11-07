package me.wesley1808.advancedchat.impl.utils;

import net.fabricmc.loader.api.FabricLoader;

public class ModCompat {
    public static final boolean VANISH = isLoaded("melius-vanish");

    private static boolean isLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
