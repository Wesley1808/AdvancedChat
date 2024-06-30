package me.wesley1808.advancedchat.impl.predicates;

import eu.pb4.predicate.api.PredicateRegistry;

public class Predicates {

    public static void register() {
        PredicateRegistry.register(CustomDistancePredicate.ID, CustomDistancePredicate.CODEC);
        PredicateRegistry.register(ChannelPredicate.ID, ChannelPredicate.CODEC);
        PredicateRegistry.register(PlayerComparePredicate.ID, PlayerComparePredicate.CODEC);
    }
}
