package me.wesley1808.advancedchat.impl.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.predicate.api.PredicateResult;
import eu.pb4.predicate.impl.predicates.GenericObject;
import me.wesley1808.advancedchat.api.AbstractChatPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class PlayerPredicateCompare extends AbstractChatPredicate {
    public static final ResourceLocation ID = ResourceLocation.tryBuild("advancedchat", "compare");
    public static final MapCodec<PlayerPredicateCompare> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GenericObject.CODEC.fieldOf("compare_predicate").forGetter((x) -> x.predicateObj)
    ).apply(instance, PlayerPredicateCompare::new));

    private final Object predicateObj;
    private final MinecraftPredicate predicate;

    public PlayerPredicateCompare(Object predicateObj) {
        super(ID, CODEC);

        this.predicateObj = predicateObj;
        this.predicate = GenericObject.toPredicate(predicateObj);
    }

    @Override
    public PredicateResult<?> test(ServerPlayer sender, ServerPlayer target) {
        var val1 = this.predicate.test(PredicateContext.of(sender));
        var val2 = this.predicate.test(PredicateContext.of(target));

        if (val1.value() instanceof Component text && val2.value() instanceof String string) {
            return PredicateResult.ofBoolean(text.getString().equals(string));
        } else if (val2.value() instanceof Component text && val1.value() instanceof String string) {
            return PredicateResult.ofBoolean(text.getString().equals(string));
        } else {
            return PredicateResult.ofBoolean(val1.success() == val2.success() && Objects.equals(val1.value(), val2.value()));
        }
    }
}
