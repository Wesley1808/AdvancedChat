package me.wesley1808.advancedchat.mixins;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.wesley1808.advancedchat.impl.utils.Socialspy;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;

@Mixin(MsgCommand.class)
public class MsgCommandMixin {

    @Redirect(
            method = "method_13463",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/commands/arguments/EntityArgument;getPlayers(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/util/Collection;"
            )
    )
    private static Collection<ServerPlayer> advancedchat$verifyNotIgnored(CommandContext<CommandSourceStack> context, String string) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, string);
        Util.throwIfIgnored(context.getSource(), targets);
        return targets;
    }

    @Inject(method = "sendMessage",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/commands/CommandSourceStack;shouldFilterMessageTo(Lnet/minecraft/server/level/ServerPlayer;)Z"
            )
    )
    private static void advancedchat$sendSocialSpy(CommandSourceStack commandSource, Collection<ServerPlayer> collection, PlayerChatMessage message, CallbackInfo ci, ChatType.Bound bound, OutgoingChatMessage outgoingChatMessage, boolean bl, Iterator<?> var6, ServerPlayer target, ChatType.Bound bound2) {
        if (commandSource.isPlayer()) {
            Socialspy.send(commandSource, target, message);
        }
    }
}
