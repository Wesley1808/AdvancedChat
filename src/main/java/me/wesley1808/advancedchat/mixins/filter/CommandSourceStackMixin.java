package me.wesley1808.advancedchat.mixins.filter;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.wesley1808.advancedchat.impl.config.Config;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommandSourceStack.class)
public class CommandSourceStackMixin {

    @ModifyReturnValue(method = "shouldFilterMessageTo", at = @At(value = "RETURN"))
    private boolean advancedchat$forceEnableTextFiltering(boolean original) {
        return original || Config.instance().filter.enabled;
    }
}
