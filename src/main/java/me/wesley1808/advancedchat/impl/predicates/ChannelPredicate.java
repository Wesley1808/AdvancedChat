package me.wesley1808.advancedchat.impl.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.predicate.api.AbstractPredicate;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.predicate.api.PredicateResult;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.data.DataManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class ChannelPredicate extends AbstractPredicate {
    private static final String GLOBAL_CHANNEL = "global";
    public static final ResourceLocation ID = ResourceLocation.tryBuild("advancedchat", "channel");
    public static final MapCodec<ChannelPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.STRING.fieldOf("channel").forGetter(ChannelPredicate::channel))
            .apply(instance, ChannelPredicate::new)
    );

    @Nullable
    private final ChatChannel channel;
    private final boolean isInvalid;

    public ChannelPredicate(String channel) {
        super(ID, CODEC);
        this.channel = Channels.get(channel);
        this.isInvalid = this.channel == null && !channel.equalsIgnoreCase(GLOBAL_CHANNEL);
    }

    public String channel() {
        if (this.isInvalid) {
            return "<invalid>";
        }
        return this.channel != null ? this.channel.name : GLOBAL_CHANNEL;
    }

    @Override
    public PredicateResult<?> test(PredicateContext context) {
        ServerPlayer player = context.player();
        if (player == null || this.isInvalid) {
            return PredicateResult.ofFailure();
        }

        AdvancedChatData data = DataManager.get(player);
        return PredicateResult.ofBoolean(data.channel == this.channel);
    }
}
