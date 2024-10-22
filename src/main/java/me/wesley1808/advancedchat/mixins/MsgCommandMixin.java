package me.wesley1808.advancedchat.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.interfaces.IServerPlayer;
import me.wesley1808.advancedchat.impl.utils.Socialspy;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(MsgCommand.class)
public class MsgCommandMixin {

    @Inject(
            method = "method_13463",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/commands/arguments/MessageArgument;resolveChatMessage(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;Ljava/util/function/Consumer;)V"
            )
    )
    private static void advancedchat$verifyNotIgnored(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir, @Local(ordinal = 0) Collection<ServerPlayer> targets) throws CommandSyntaxException {
        Util.throwIfIgnored(context.getSource(), targets);
    }

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private static void advancedchat$hideChatMessage(CommandSourceStack source, Collection<ServerPlayer> collection, PlayerChatMessage message, CallbackInfo ci) {
        ServerPlayer sender = source.getPlayer();
        if (sender != null && Util.shouldHideMessage(sender, message)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "sendMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/commands/CommandSourceStack;shouldFilterMessageTo(Lnet/minecraft/server/level/ServerPlayer;)Z"
            )
    )
    private static void advancedchat$onSendMessage(
            CommandSourceStack source, Collection<ServerPlayer> collection, PlayerChatMessage message, CallbackInfo ci,
            @Local(ordinal = 0) ServerPlayer target
    ) {
        ServerPlayer sender = source.getPlayer();
        if (sender != null) {
            Util.playSound(target, Config.instance().privateMessageSound);
            Socialspy.send(source, target, message);
            IServerPlayer.setReplyTarget(sender, target.getUUID());
            IServerPlayer.setReplyTarget(target, sender.getUUID());
        }
    }
}
