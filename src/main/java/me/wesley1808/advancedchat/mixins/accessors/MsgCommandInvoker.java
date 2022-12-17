package me.wesley1808.advancedchat.mixins.accessors;


import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(MsgCommand.class)
public interface MsgCommandInvoker {

    @Invoker("sendMessage")
    static void invokeSendMessage(CommandSourceStack source, Collection<ServerPlayer> collection, PlayerChatMessage message) {
    }
}
