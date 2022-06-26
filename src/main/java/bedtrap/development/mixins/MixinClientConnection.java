package bedtrap.development.mixins;

import bedtrap.development.BedTrap;
import bedtrap.development.ic.Command;
import bedtrap.development.events.event.PacketEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Future;

import static bedtrap.development.ic.util.Wrapper.mc;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Shadow
    private Channel channel;
    @Shadow
    @Final
    private NetworkSide side;

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
    public void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericFutureListener_1,
                     CallbackInfo callback) {
        if (packet instanceof ChatMessageC2SPacket pack) {
            if (pack.getChatMessage().startsWith(Command.getPrefix())) {
                BedTrap.getCommandManager().runCommand(pack.getChatMessage().substring(Command.getPrefix().length()));
                callback.cancel();
            }
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void channelRead0(ChannelHandlerContext chc, Packet<?> packet, CallbackInfo ci) {
        if (mc.player == null && mc.world == null) return;
        if (this.channel.isOpen() && packet != null) {
            try {
                PacketEvent.Receive event = new PacketEvent.Receive(packet);
                BedTrap.EventBus.post(event);
                if (event.isCancelled())
                    ci.cancel();
            } catch (Exception ignored) {
            }
        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void sendImmediately(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener, CallbackInfo ci) {
        if (mc.player == null && mc.world == null) return;
        if (this.side != NetworkSide.CLIENTBOUND) return;
        try {
            PacketEvent.Send event = new PacketEvent.Send(packet);
            BedTrap.EventBus.post(event);
            if (event.isCancelled()) ci.cancel();
        } catch (Exception ignored) {
        }
    }

}
