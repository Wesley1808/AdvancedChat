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
    private ClientboundSetActionBarTextPacket advancedchat$actionBarPacket;
    @Unique
    @Nullable
    private PlayerChatMessage advancedchat$lastChatMessage;
    @Unique
    @Nullable
    private UUID advancedchat$replyTarget;
    @Unique
    private long advancedchat$nextPacketTime;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void advancedchat$onInit(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo ci) {
        this.advancedchat$updateActionBarPacket();
    }

    @Override
    public boolean startRiding(Entity entity, boolean bl) {
        if (super.startRiding(entity, bl)) {
            // Prevents the channel overlay packets from overriding the vehicle mount overlay.
            this.advancedchat$delayNextPacket();
            return true;
        }
        return false;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void advancedchat$onTick(CallbackInfo ci) {
        Config config = Config.instance();
        if (config.actionbar) {
            if (this.tickCount % config.actionbarUpdateInterval == 0) {
                this.advancedchat$updateActionBarPacket();
            }

            if (this.advancedchat$actionBarPacket != null) {
                long time = System.currentTimeMillis();
                if (this.advancedchat$nextPacketTime <= time) {
                    this.connection.send(this.advancedchat$actionBarPacket);
                    this.advancedchat$nextPacketTime = time + 1000;
                }
            }
        }
    }

    @Nullable
    @Override
    public ClientboundSetActionBarTextPacket advancedchat$getActionBarPacket() {
        return this.advancedchat$actionBarPacket;
    }

    @Nullable
    @Override
    public PlayerChatMessage advancedchat$getLastChatMessage() {
        return this.advancedchat$lastChatMessage;
    }

    @Override
    public void advancedchat$setLastChatMessage(PlayerChatMessage lastChatMessage) {
        this.advancedchat$lastChatMessage = lastChatMessage;
    }

    @Nullable
    @Override
    public UUID advancedchat$getReplyTarget() {
        return this.advancedchat$replyTarget;
    }

    @Override
    public void advancedchat$setReplyTarget(@Nullable UUID uuid) {
        this.advancedchat$replyTarget = uuid;
    }

    @Override
    public void advancedchat$delayNextPacket() {
        this.advancedchat$nextPacketTime = System.currentTimeMillis() + 3000;
    }

    @Override
    public void advancedchat$updateActionBarPacket() {
        ServerPlayer player = (ServerPlayer) (Object) this;
        AdvancedChatData data = DataManager.get(player);
        if (data.channel == null) {
            this.advancedchat$actionBarPacket = null;
            return;
        }

        this.advancedchat$actionBarPacket = new ClientboundSetActionBarTextPacket(data.channel.getActionBarText(player));
    }
}
