package me.wesley1808.advancedchat.impl.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.config.ConfigManager;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.interfaces.IServerPlayer;
import me.wesley1808.advancedchat.impl.utils.Permission;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class AdvancedChatCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = literal("advancedchat");
        builder.then(literal("reload")
                .requires(Permission.require(Permission.RELOAD, 2))
                .executes(ctx -> reload(ctx.getSource()))
        );

        builder.then(literal("save")
                .requires(Permission.require(Permission.RELOAD, 2))
                .executes(ctx -> save(ctx.getSource()))
        );

        dispatcher.register(builder);
    }

    private static int save(CommandSourceStack source) {
        String error = ConfigManager.save();
        if (error != null) {
            MutableComponent component = Component.literal("Failed to save config!");
            component.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(error))));
            source.sendFailure(component);
            return 0;
        } else {
            source.sendSystemMessage(Component.literal("Saved config!").withStyle(ChatFormatting.GREEN));
            return Command.SINGLE_SUCCESS;
        }
    }

    private static int reload(CommandSourceStack source) {
        String error = ConfigManager.load();
        if (error != null) {
            MutableComponent component = Component.literal("Failed to reload config!");
            component.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(error))));
            source.sendFailure(component);
            return 0;
        } else {
            source.sendSystemMessage(Component.literal("Reloaded config!").withStyle(ChatFormatting.GREEN));
            Channels.register();

            for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
                AdvancedChatData data = DataManager.get(player);
                String prevName = data.channel != null ? data.channel.name : null;
                ChatChannel channel = Channels.get(prevName);
                if (channel != null && channel.canUse(player)) {
                    data.channel = channel;
                } else {
                    data.channel = null;
                }

                IServerPlayer.updateActionBarPacket(player);
            }

            return Command.SINGLE_SUCCESS;
        }
    }
}
