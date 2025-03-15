package me.wesley1808.advancedchat.impl.utils;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.network.chat.MutableComponent;

public class Formatter {
    private static final NodeParser PARSER = NodeParser.builder()
            .simplifiedTextFormat()
            .quickText()
            .requireSafe()
            .build();

    public static MutableComponent parse(String text) {
        return (MutableComponent) PARSER.parseText(text, ParserContext.of());
    }

    public static ParentTextNode parseNodes(String text) {
        return new ParentNode(PARSER.parseNodes(new LiteralNode(text)));
    }
}
