package me.wesley1808.advancedchat.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wesley1808.advancedchat.impl.utils.Socialspy;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingPlayerChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;

@Mixin(MsgCommand.class)
public class MsgCommandMixin {

    @Inject(method = "sendMessage", at = @At(value = "HEAD"))
    private static void advancedchat$verifyNotIgnored(CommandSourceStack source, Collection<ServerPlayer> targets, MessageArgument.ChatMessage chatMessage, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        Util.throwIfIgnored(source, targets);
    }

    @Inject(method = "method_44144",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/commands/CommandSourceStack;shouldFilterMessageTo(Lnet/minecraft/server/level/ServerPlayer;)Z"
            )
    )
    private static void advancedchat$sendSocialSpy(CommandSourceStack commandSource, Collection<?> collection, ChatType.Bound bound, PlayerChatMessage message, CallbackInfo ci, OutgoingPlayerChatMessage outgoingPlayerChatMessage, boolean bl, Entity entity, boolean bl2, Iterator<?> var8, ServerPlayer target, ChatType.Bound bound2) {
        if (commandSource.isPlayer()) {
            Socialspy.send(commandSource, target, message);
        }
    }
}
