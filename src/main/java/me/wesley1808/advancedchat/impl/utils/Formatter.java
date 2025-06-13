package me.wesley1808.advancedchat.impl.utils;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Function;

public class Formatter {
    private static final NodeParser BASE_PARSER = baseParser().build();

    public static MutableComponent parse(String text, NodeParser parser) {
        return (MutableComponent) parser.parseText(text, ParserContext.of());
    }

    public static ParentTextNode parseNodes(String text, NodeParser parser) {
        return new ParentNode(parser.parseNodes(new LiteralNode(text)));
    }

    public static MutableComponent parse(String text) {
        return parse(text, BASE_PARSER);
    }

    public static ParentTextNode parseNodes(String text) {
        return parseNodes(text, BASE_PARSER);
    }

    public static NodeParser placeholderParser(Function<String, Component> placeholderMapper) {
        return baseParser()
                .customTags(TagLikeParser.PLACEHOLDER_USER, TagLikeParser.Provider.placeholderText(placeholderMapper))
                .build();
    }

    private static ParserBuilder baseParser() {
        return NodeParser.builder()
                .simplifiedTextFormat()
                .quickText()
                .requireSafe();
    }
}
