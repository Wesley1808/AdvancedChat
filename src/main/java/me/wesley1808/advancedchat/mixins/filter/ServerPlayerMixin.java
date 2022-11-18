package me.wesley1808.advancedchat.mixins.filter;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.utils.Filter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.TextFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @ModifyExpressionValue(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;createTextFilterForPlayer(Lnet/minecraft/server/level/ServerPlayer;)Lnet/minecraft/server/network/TextFilter;"
            )
    )
    private TextFilter advancedchat$modifyTextFilter(TextFilter original) {
        if (original == TextFilter.DUMMY) {
            return new Filter((ServerPlayer) (Object) this);
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "updateOptions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ServerboundClientInformationPacket;textFilteringEnabled()Z"
            )
    )
    private boolean forceTextFiltering(boolean original) {
        return original || Config.instance().filter.forceTextFiltering;
    }
}
