package me.wesley1808.advancedchat.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.wesley1808.advancedchat.common.channels.Channels;
import me.wesley1808.advancedchat.common.channels.ChatChannel;
import me.wesley1808.advancedchat.common.config.ConfigManager;
import me.wesley1808.advancedchat.common.data.DataManager;
import me.wesley1808.advancedchat.common.utils.Permission;
import me.wesley1808.advancedchat.common.utils.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class AdvancedChatCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = literal("advancedchat");
        builder.then(literal("reload")
                .requires(Permission.require("command.reload", 2))
                .executes(ctx -> reload(ctx.getSource())));

        dispatcher.register(builder);
    }

    private static int reload(CommandSourceStack source) {
        ConfigManager.load();
        Channels.register();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            DataManager.modify(player, (data) -> {
                String prevName = data.channel != null ? data.channel.name : null;
                ChatChannel channel = Channels.get(prevName);
                if (channel != null && channel.canPlayerUse(player)) {
                    data.channel = channel;
                } else {
                    data.channel = null;
                }
            });
            Util.resetActionBarPacket(player);
        }

        source.sendSystemMessage(Component.literal("Reloaded config!").withStyle(ChatFormatting.GREEN));
        return Command.SINGLE_SUCCESS;
    }
}
