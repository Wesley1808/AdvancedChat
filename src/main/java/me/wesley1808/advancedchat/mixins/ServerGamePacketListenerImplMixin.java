package me.wesley1808.advancedchat.mixins;

import me.wesley1808.advancedchat.api.AdvancedChatAPI;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.interfaces.IServerPlayer;
import me.wesley1808.advancedchat.impl.utils.Util;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 900)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {
    @Shadow
    public ServerPlayer player;

    public ServerGamePacketListenerImplMixin(MinecraftServer minecraftServer, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraftServer, connection, commonListenerCookie);
    }

    @Inject(method = "sendPlayerChatMessage", at = @At("HEAD"))
    private void advancedchat$onChatMessage(PlayerChatMessage message, ChatType.Bound bound, CallbackInfo ci) {
        String pingName = '@' + this.player.getScoreboardName();
        if (message.signedContent().contains(pingName)) {
            Util.playSound(this.player, Config.instance().chatPingSound);
        }
    }

    @Redirect(
            method = "broadcastChatMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/ChatType;bind(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/network/chat/ChatType$Bound;"
            )
    )
    private ChatType.Bound advancedchat$addChannelPrefix(ResourceKey<ChatType> key, Entity entity) {
        MutableComponent prefix = (MutableComponent) AdvancedChatAPI.getChannelPrefix(this.player);
        return ChatType.bind(key, entity.level().registryAccess(), prefix.append(entity.getDisplayName()));
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener packetSendListener) {
        super.send(packet, packetSendListener);

        if (Config.instance().actionbar && this.player instanceof IServerPlayer player) {
            Packet<?> current = player.getActionBarPacket();
            if (current != null && current != packet && Util.isOverlayPacket(packet)) {
                // Prevents the channel overlay packets from overriding other overlays from the server.
                player.delayNextPacket();
            }
        }
    }
}
