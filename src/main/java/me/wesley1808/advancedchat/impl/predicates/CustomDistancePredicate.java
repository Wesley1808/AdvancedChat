package me.wesley1808.advancedchat.impl.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.predicate.api.PredicateResult;
import me.wesley1808.advancedchat.api.AbstractChatPredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class CustomDistancePredicate extends AbstractChatPredicate {
    public static final ResourceLocation ID = ResourceLocation.tryBuild("advancedchat", "distance");
    public static final MapCodec<CustomDistancePredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(DistancePredicate.CODEC.fieldOf("value").forGetter((inst) -> inst.predicate))
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
        return PredicateResult.ofBoolean(sender.level() == target.level() && this.predicate.matches(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z));
    }
}
