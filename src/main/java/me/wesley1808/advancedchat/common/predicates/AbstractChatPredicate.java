package me.wesley1808.advancedchat.common.predicates;

import com.mojang.serialization.MapCodec;
import eu.pb4.predicate.api.AbstractPredicate;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.predicate.api.PredicateResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractChatPredicate extends AbstractPredicate {

    public <T extends MinecraftPredicate> AbstractChatPredicate(ResourceLocation identifier, MapCodec<T> codec) {
        super(identifier, codec);
    }

    public static PredicateContext createContext(ServerPlayer sender, ServerPlayer target) {
        return new PredicateContext(sender.getServer(), sender.createCommandSourceStack(), sender.getLevel(), sender, target, sender.getGameProfile());
    }

    @Override
    public final PredicateResult<?> test(PredicateContext context) {
        ServerPlayer sender = context.player();
        if (sender == null || !(context.entity() instanceof ServerPlayer target)) {
            return PredicateResult.ofFailure();
        }

        return this.test(sender, target);
    }

    public abstract PredicateResult<?> test(ServerPlayer sender, ServerPlayer target);
}
