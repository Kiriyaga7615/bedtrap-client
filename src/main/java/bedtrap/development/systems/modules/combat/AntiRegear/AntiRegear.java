package bedtrap.development.systems.modules.combat.AntiRegear;

import bedtrap.development.events.event.PlaceBlockEvent;
import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import bedtrap.development.systems.utils.advanced.PacketUtils;
import bedtrap.development.systems.utils.other.Task;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static bedtrap.development.systems.modules.combat.AntiRegear.ARUtils.distanceBetween;
import static bedtrap.development.systems.modules.combat.AntiRegear.ARUtils.getSphere;

@Module.Info(name = "AntiRegear", category = Module.Category.Combat)
public class AntiRegear extends Module {
    public Setting<Double> radius = register("Radius", 1, 0, 1, 2);
    public Setting<Boolean> own = register("Own", true);

    private final ArrayList<BlockPos> ownBlocks = new ArrayList<>();
    private FindItemResult tool;
    private int prevSlot;
    private BlockPos currentPos;
    private BlockState currentState;
    private int timer;

    private final Task mine = new Task();
    private final PacketUtils packetMine = new PacketUtils();

    @Override
    public void onActivate() {
        timer = 0;
        currentPos = null;
        currentState = null;
        ownBlocks.clear();

        mine.reset();
    }

    @Subscribe
    public void onPlace(PlaceBlockEvent event) {
        if (event.block instanceof ShulkerBoxBlock || event.block instanceof EnderChestBlock) {
            ownBlocks.add(event.blockPos);
        }
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        if (getBlocks(radius.get()).isEmpty()) return;

        if (currentPos != null) {
            tool = InvUtils.findFastestTool(currentState);
            if (!tool.found()) return;

            packetMine.mine(currentPos, mine);
            mc.world.setBlockBreakingInfo(mc.player.getId(), currentPos, (int) (packetMine.getProgress() * 10.0F) - 1);

            if (packetMine.isReadyOn(0.95)) mc.player.getInventory().selectedSlot = tool.slot();
            else prevSlot = mc.player.getInventory().selectedSlot;

            boolean shouldStop = distanceBetween(mc.player.getBlockPos(), currentPos) >= 5 || isBugged();
            if (mc.world.getBlockState(currentPos).isAir() || shouldStop) {
                if (shouldStop) packetMine.abortMining(currentPos);
                if (prevSlot != -1) mc.player.getInventory().selectedSlot = prevSlot;
                currentPos = null;
                currentState = null;
                packetMine.reset();
                mine.reset();
            }
        } else {
            getBlocks(radius.get()).forEach(blockPos -> {
                currentPos = blockPos;
                currentState = mc.world.getBlockState(currentPos);
            });
        }
    }

    private ArrayList<BlockPos> getBlocks(double radius) {
        ArrayList<BlockPos> sphere = new ArrayList<>(getSphere(mc.player.getBlockPos(), radius, radius));
        ArrayList<BlockPos> blocks = new ArrayList<>();

        for (BlockPos blockPos : sphere) {
            if (mc.world.getBlockState(blockPos).isAir()) continue;
            if (!own.get() && ownBlocks.contains(blockPos)) continue;

            if (!blocks.contains(blockPos) && mc.world.getBlockState(blockPos).getBlock() instanceof ShulkerBoxBlock || mc.world.getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST) {
                blocks.add(blockPos);
            }
        }

        blocks.sort(Comparator.comparingDouble(this::distanceTo));
        return blocks;
    }

    public double distanceTo(BlockPos blockPos) {
        return distanceBetween(mc.player.getBlockPos(), blockPos);
    }

    private boolean isBugged() {
        if (!packetMine.isReady()) return false;
        timer++;

        if (timer >= 10) {
            timer = 0;
            return true;
        }

        return false;
    }

    @Subscribe
    public void onRender(Render3DEvent e) {
        if (cantUpdate()) return;
        if (getBlocks(radius.get()).isEmpty()) return;

        getBlocks(radius.get()).forEach(blockPos -> {
            Vec3d vec3d = Renderer3D.get.getRenderPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            Box box = new Box(vec3d.x, vec3d.y, vec3d.z, vec3d.x + 1, vec3d.y + 1, vec3d.z + 1);
            Renderer3D.get.drawBox(e.getMatrixStack(), box, new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue(), 230).getRGB());
            Renderer3D.get.drawOutlineBox(e.getMatrixStack(), box, new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue(), 230).getRGB());
        });
    }
}
