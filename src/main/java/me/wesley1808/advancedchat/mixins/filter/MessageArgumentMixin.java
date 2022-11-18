package me.wesley1808.advancedchat.mixins.filter;

import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(MessageArgument.class)
public class MessageArgumentMixin {

    @Redirect(
            method = "filterPlainText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/PlayerChatMessage;hasSignatureFrom(Ljava/util/UUID;)Z"
            )
    )
    private static boolean advancedchat$alwaysFilterMessage(PlayerChatMessage message, UUID uuid) {
        return true;
    }
}
