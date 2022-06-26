package bedtrap.development.systems.modules.combat.CevBreaker;

import bedtrap.development.events.event.EntityAddedEvent;
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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

import static bedtrap.development.systems.utils.advanced.TargetUtils.*;

@Module.Info(name = "CevBreaker", category = Module.Category.Combat)
public class CevBreaker extends Module {
    public Setting<Integer> radius = register("TargetRange", 5, 0, 7);
    public Setting<Boolean> packet = register("Packet", true);
    public Setting<Boolean> render = register("Render", true);

    private PlayerEntity target;
    private FindItemResult crystal, obsidian, pickaxe;
    private BlockPos blockPos;
    private EndCrystalEntity endCrystal;

    private Stage stage;
    private final Task mineTask = new Task();
    private final PacketUtils packetMine = new PacketUtils();

    @Override
    public void onActivate() {
        mineTask.reset();
        packetMine.reset();

        target = null;
        endCrystal = null;
        blockPos = null;

        stage = Stage.Obsidian;
    }

    @Subscribe
    public void onAdded(EntityAddedEvent event) {
        if (!(event.entity instanceof EndCrystalEntity)) return;
        if (blockPos == null) return;

        BlockPos crystalPos = event.entity.getBlockPos();

        if (blockPos.up().equals(crystalPos)) endCrystal = (EndCrystalEntity) event.entity;
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        // Module info
        //setDisplayInfo(stage.name());

        target = getPlayerTarget(radius.get(), SortPriority.LowestDistance);
        if (isBadTarget(target, radius.get())) return;

        crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);
        obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);
        pickaxe = InvUtils.findInHotbar(Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE);

        if (!crystal.found() || !obsidian.found() || !pickaxe.found()) return;

        switch (stage) {
            case Obsidian -> {
                blockPos = target.getBlockPos().up(2);
                interact(blockPos, obsidian);

                if (is(blockPos, Blocks.OBSIDIAN)) stage = Stage.Crystal;
            }
            case Crystal -> {
                interact(blockPos, crystal);

                if (endCrystal != null) stage = Stage.Pickaxe;
            }
            case Pickaxe -> {
                breakBlock(blockPos, pickaxe);

                if (is(blockPos, Blocks.AIR)) stage = Stage.Attack;
            }
            case Attack -> {
                attack(endCrystal);

                endCrystal = null;
                stage = Stage.Obsidian;
            }
        }
    }

    private void breakBlock(BlockPos blockPos, FindItemResult findItemResult) {
        if (blockPos == null) return;

        mc.player.getInventory().selectedSlot = findItemResult.slot();
        if (packet.get()) packetMine.mine(blockPos, mineTask);
        else mc.interactionManager.updateBlockBreakingProgress(blockPos, Direction.DOWN);

        mc.player.swingHand(findItemResult.getHand());
    }

    private void interact(BlockPos blockPos, FindItemResult findItemResult) {
        if (stage.equals(Stage.Obsidian) && !canPlace(blockPos)) {
            stage = Stage.Crystal;
            return;
        }

        if (!(findItemResult.isOffhand() && findItemResult.equals(crystal))) {
            mc.player.getInventory().selectedSlot = findItemResult.slot();
        }
        if (canPlace(blockPos) || findItemResult.equals(crystal)) {
            if (packet.get())
                mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(findItemResult.getHand(), new BlockHitResult(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), Direction.DOWN, blockPos, true)));
            else
                mc.interactionManager.interactBlock(mc.player, mc.world, findItemResult.getHand(), new BlockHitResult(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), Direction.DOWN, blockPos, false));
        }
        mc.player.swingHand(findItemResult.getHand());
    }

    private void attack(EndCrystalEntity crystal) {
        if (crystal == null) return;

        mc.interactionManager.attackEntity(mc.player, crystal);
        mc.player.swingHand(Hand.MAIN_HAND);

        packetMine.reset();
        mineTask.reset();
    }

    private boolean canPlace(BlockPos blockPos) {
        return mc.world.getBlockState(blockPos).isAir();
    }

    private boolean is(BlockPos blockPos, Block block) {
        return mc.world.getBlockState(blockPos).isOf(block);
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (!render.get() || blockPos == null) return;

        Vec3d vec3d = Renderer3D.get.getRenderPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Box box = new Box(vec3d.x, vec3d.y, vec3d.z, vec3d.x + 1, vec3d.y + 1, vec3d.z + 1);
        Renderer3D.get.drawBox(event.getMatrixStack(), box, new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue(), 230).getRGB());
        Renderer3D.get.drawOutlineBox(event.getMatrixStack(), box, new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue(), 230).getRGB());
    }

    public enum Stage {
        Obsidian, Crystal, Pickaxe, Attack
    }
}
