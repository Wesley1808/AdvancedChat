package me.wesley1808.advancedchat.mixins;

import com.mojang.authlib.GameProfile;
import me.wesley1808.advancedchat.impl.config.Config;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.data.DataManager;
import me.wesley1808.advancedchat.impl.interfaces.IServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements IServerPlayer {
    @Shadow
    public ServerGamePacketListenerImpl connection;
    @Unique
    @Nullable
    private ClientboundSetActionBarTextPacket actionBarPacket;
    @Unique
    @Nullable
    private PlayerChatMessage lastChatMessage;
    @Unique
    @Nullable
    private UUID replyTarget;
    @Unique
    private long nextPacketTime;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void advancedchat$onInit(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo ci) {
        this.updateActionBarPacket();
    }

    @Override
    public boolean startRiding(Entity entity, boolean bl) {
        if (super.startRiding(entity, bl)) {
            // Prevents the channel overlay packets from overriding the vehicle mount overlay.
            this.delayNextPacket();
            return true;
        }
        return false;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void advancedchat$onTick(CallbackInfo ci) {
        Config config = Config.instance();
        if (config.actionbar) {
            if (this.tickCount % config.actionbarUpdateInterval == 0) {
                this.updateActionBarPacket();
            }

            if (this.actionBarPacket != null) {
                long time = System.currentTimeMillis();
                if (this.nextPacketTime <= time) {
                    this.connection.send(this.actionBarPacket);
                    this.nextPacketTime = time + 1000;
                }
            }
        }
    }

    @Nullable
    @Override
    public ClientboundSetActionBarTextPacket getActionBarPacket() {
        return this.actionBarPacket;
    }

    @Nullable
    @Override
    public PlayerChatMessage getLastChatMessage() {
        return this.lastChatMessage;
    }

    @Override
    public void setLastChatMessage(PlayerChatMessage lastChatMessage) {
        this.lastChatMessage = lastChatMessage;
    }

    @Nullable
    @Override
    public UUID getReplyTarget() {
        return this.replyTarget;
    }

    @Override
    public void setReplyTarget(@Nullable UUID uuid) {
        this.replyTarget = uuid;
    }

    @Override
    public void delayNextPacket() {
        this.nextPacketTime = System.currentTimeMillis() + 3000;
    }

    @Override
    public void updateActionBarPacket() {
        ServerPlayer player = (ServerPlayer) (Object) this;
        AdvancedChatData data = DataManager.get(player);
        if (data.channel == null) {
            this.actionBarPacket = null;
            return;
        }

        this.actionBarPacket = new ClientboundSetActionBarTextPacket(data.channel.getActionBarText(player));
    }
}
