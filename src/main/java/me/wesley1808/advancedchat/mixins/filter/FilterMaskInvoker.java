package me.wesley1808.advancedchat.mixins.filter;

import net.minecraft.network.chat.FilterMask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.BitSet;

@Mixin(FilterMask.class)
public interface FilterMaskInvoker {

    @Invoker("<init>")
    static FilterMask newMask(BitSet bitSet) {
        return null;
    }
}
