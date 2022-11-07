package me.wesley1808.advancedchat.impl.utils;

import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import net.minecraft.network.chat.MutableComponent;

public class Formatter {

    public static MutableComponent parse(String text) {
        return (MutableComponent) TextParserUtils.formatTextSafe(text);
    }

    public static ParentTextNode parseNodes(String text) {
        return TextParserUtils.formatNodesSafe(text);
    }
}
