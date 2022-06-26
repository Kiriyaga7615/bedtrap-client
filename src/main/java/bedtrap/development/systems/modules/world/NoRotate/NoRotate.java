package bedtrap.development.systems.modules.world.NoRotate;

import bedtrap.development.events.event.PacketEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.mixins.MinecraftClientAccessor;
import bedtrap.development.mixins.PlayerPositionLookS2CPacketAccessor;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import bedtrap.development.systems.utils.other.TimerUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.shape.VoxelShape;

@Module.Info(name = "NoRotate", category = Module.Category.World)
public class NoRotate extends Module {

    @Subscribe
    private void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerPositionLookS2CPacket && mc.player != null) {
            ((PlayerPositionLookS2CPacketAccessor) event.packet).setPitch(mc.player.getPitch());
            ((PlayerPositionLookS2CPacketAccessor) event.packet).setYaw(mc.player.getYaw());
        }
    }
}
