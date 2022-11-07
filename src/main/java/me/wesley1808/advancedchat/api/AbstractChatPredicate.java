package me.wesley1808.advancedchat.api;

import com.mojang.authlib.GameProfile;
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
        // Stores the sender in the game profile, as it isn't really used anywhere else.
        // For everything else we want to be testing against the target.
        return new PredicateContext(target.server, target.createCommandSourceStack(), target.getLevel(), target, target, sender.getGameProfile());
    }

    @Override
    public final PredicateResult<?> test(PredicateContext context) {
        GameProfile profile = context.gameProfile();
        ServerPlayer target = context.player();
        ServerPlayer sender;
        if (target == null || profile == null || (sender = context.server().getPlayerList().getPlayer(profile.getId())) == null) {
            return PredicateResult.ofFailure();
        }

        return this.test(sender, target);
    }

    public abstract PredicateResult<?> test(ServerPlayer sender, ServerPlayer target);
}
