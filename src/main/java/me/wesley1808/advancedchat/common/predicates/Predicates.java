package me.wesley1808.advancedchat.common.predicates;

import eu.pb4.predicate.api.PredicateRegistry;

public class Predicates {

    public static void register() {
        PredicateRegistry.register(CustomDistancePredicate.ID, CustomDistancePredicate.CODEC);
    }
}
