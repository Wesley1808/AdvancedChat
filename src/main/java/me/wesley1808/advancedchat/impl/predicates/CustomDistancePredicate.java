package me.wesley1808.advancedchat.impl.predicates;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.predicate.api.PredicateResult;
import me.wesley1808.advancedchat.api.AbstractChatPredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class CustomDistancePredicate extends AbstractChatPredicate {
    public static final ResourceLocation ID = new ResourceLocation("advancedchat", "distance");
    public static final MapCodec<CustomDistancePredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(DistancePredicateCodec.INSTANCE.fieldOf("value").forGetter((inst) -> inst.predicate))
            .apply(instance, CustomDistancePredicate::new)
    );

    private final DistancePredicate predicate;
    public CustomDistancePredicate(DistancePredicate predicate) {
        super(ID, CODEC);
        this.predicate = predicate;
    }

    @Override
    public PredicateResult<?> test(ServerPlayer sender, ServerPlayer target) {
        Vec3 pos1 = sender.position();
        Vec3 pos2 = target.position();
        return PredicateResult.ofBoolean(sender.level == target.level && this.predicate.matches(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z));
    }

    private static class DistancePredicateCodec implements Codec<DistancePredicate> {
        private static final DistancePredicateCodec INSTANCE = new DistancePredicateCodec();

        @Override
        public <T> DataResult<Pair<DistancePredicate, T>> decode(DynamicOps<T> ops, T input) {
            return DataResult.success(new Pair<>(DistancePredicate.fromJson(input instanceof JsonElement jsonElement ? jsonElement : ops.convertTo(JsonOps.INSTANCE, input)), input));
        }

        @Override
        public <T> DataResult<T> encode(DistancePredicate input, DynamicOps<T> ops, T prefix) {
            // noinspection unchecked
            T encoded = ops instanceof JsonOps ? (T) input.serializeToJson() : JsonOps.INSTANCE.convertTo(ops, input.serializeToJson());
            return ops.getMap(encoded).flatMap(map -> ops.mergeToMap(prefix, map));
        }
    }


}
