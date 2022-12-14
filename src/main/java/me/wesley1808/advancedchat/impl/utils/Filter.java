package me.wesley1808.advancedchat.impl.utils;

import me.wesley1808.advancedchat.api.AdvancedChatEvents;
import me.wesley1808.advancedchat.impl.AdvancedChat;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.mixins.accessors.FilterMaskInvoker;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.TextFilter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filter implements TextFilter {
    private final ServerPlayer player;

    public Filter(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public void join() {
    }

    @Override
    public void leave() {
    }

    @NotNull
    @Override
    public CompletableFuture<FilteredText> processStreamMessage(String input) {
        if (Filter.isEnabled()) {
            return CompletableFuture.supplyAsync(() -> this.filter(input));
        } else {
            return CompletableFuture.completedFuture(FilteredText.passThrough(input));
        }
    }

    @NotNull
    @Override
    public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> list) {
        if (Filter.isEnabled()) {
            return CompletableFuture.supplyAsync(() -> Util.map(list, this::filter));
        } else {
            return CompletableFuture.completedFuture(Util.map(list, FilteredText::passThrough));
        }
    }

    private FilteredText filter(String input) {
        FilteredText text = Filter.process(input);

        if (text.isFiltered()) {
            AdvancedChatEvents.MESSAGE_FILTERED.invoker().onMessageFiltered(this.player, input);
            if (Config.instance().filter.logFilteredMessages) {
                AdvancedChat.getLogger().info("[AdvancedChat] Filtered text from {}: {}", this.player.getScoreboardName(), input);
            }
        }

        return text;
    }

    public static boolean isEnabled() {
        Config.Filter config = Config.instance().filter;
        return config.enabled && (config.filteredWords.length > 0 || config.regexFilterPatterns.length > 0);
    }

    public static FilteredText process(String input) {
        Config.Filter config = Config.instance().filter;
        BitSet mask = new BitSet(input.length());

        for (Pattern pattern : config.regexFilterPatterns) {
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                mask.set(matcher.start(), matcher.end());
            }
        }

        for (String word : config.filteredWords) {
            final int start = StringUtils.indexOfIgnoreCase(input, word);
            for (int curr = start; curr != StringUtils.INDEX_NOT_FOUND; curr = StringUtils.indexOfIgnoreCase(input, word, curr)) {
                mask.set(curr, curr += word.length());
            }
        }

        return new FilteredText(input, mask.isEmpty() ? FilterMask.PASS_THROUGH : FilterMaskInvoker.newMask(mask));
    }
}
