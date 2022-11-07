package me.wesley1808.advancedchat.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.pb4.placeholders.api.TextParserUtils;
import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.utils.Permission;
import me.wesley1808.advancedchat.impl.utils.Socialspy;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;

@Mixin(MsgCommand.class)
public class MsgCommandMixin {

    @Inject(method = "sendMessage", at = @At(value = "HEAD"))
    private static void advancedchat$verifyNotIgnored(CommandSourceStack source, Collection<ServerPlayer> targets, PlayerChatMessage playerChatMessage, CallbackInfo ci) throws CommandSyntaxException {
        ServerPlayer sender = source.getPlayer();
        if (sender != null && !Permission.check(source, "bypass.ignore", 2)) {
            for (ServerPlayer target : targets) {
                AdvancedChatData data = AdvancedChatAPI.getData(target);
                if (data.ignored.contains(sender.getUUID())) {
                    throw new SimpleCommandExceptionType(TextParserUtils.formatTextSafe(Config.instance().messages.ignored.replace("${player}", target.getScoreboardName()))).create();
                }
            }
        }
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
