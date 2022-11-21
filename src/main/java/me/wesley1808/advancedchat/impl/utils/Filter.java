package me.wesley1808.advancedchat.impl.utils;

import me.wesley1808.advancedchat.api.AdvancedChatEvents;
import me.wesley1808.advancedchat.impl.AdvancedChat;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.mixins.filter.FilterMaskInvoker;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.TextFilter;
import org.apache.commons.lang3.StringUtils;

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @Override
    public CompletableFuture<FilteredText> processStreamMessage(String input) {
        if (Filter.isEnabled()) {
            return CompletableFuture.supplyAsync(() -> this.filter(input));
        } else {
            return CompletableFuture.completedFuture(FilteredText.passThrough(input));
        }
    }

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
        Config.Filter filter = Config.instance().filter;
        return filter.enabled && filter.filteredWords.length > 0;
    }

    public static FilteredText process(String input) {
        BitSet mask = new BitSet(input.length());
        for (String word : Config.instance().filter.filteredWords) {
            final int start = StringUtils.indexOfIgnoreCase(input, word);
            for (int curr = start; curr != StringUtils.INDEX_NOT_FOUND; curr = StringUtils.indexOfIgnoreCase(input, word, curr)) {
                mask.set(curr, curr += word.length());
            }
        }

        return new FilteredText(input, mask.isEmpty() ? FilterMask.PASS_THROUGH : FilterMaskInvoker.newMask(mask));
    }
}
