package me.wesley1808.advancedchat.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.pb4.placeholders.api.TextParserUtils;
import me.wesley1808.advancedchat.common.config.Config;
import me.wesley1808.advancedchat.common.data.AdvancedChatData;
import me.wesley1808.advancedchat.common.data.DataManager;
import me.wesley1808.advancedchat.common.utils.Permission;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MsgCommand.class)
public class MsgCommandMixin {

    @Inject(method = "sendMessage", at = @At(value = "HEAD"))
    private static void advancedchat$verifyNotIgnored(CommandSourceStack source, Collection<ServerPlayer> targets, PlayerChatMessage playerChatMessage, CallbackInfo ci) throws CommandSyntaxException {
        ServerPlayer sender = source.getPlayer();
        if (sender != null && !Permission.check(source, "ignore.bypass", 2)) {
            for (ServerPlayer target : targets) {
                AdvancedChatData data = DataManager.get(target);
                if (data.ignored.contains(sender.getUUID())) {
                    throw new SimpleCommandExceptionType(TextParserUtils.formatTextSafe(Config.instance().messages.ignored.replace("${player}", target.getScoreboardName()))).create();
                }
            }
        }
    }
}
