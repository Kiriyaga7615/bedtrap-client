package bedtrap.development.systems.modules.world.MiningTS;

import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.events.event.StartBreakingBlockEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.advanced.FindItemResult;
import bedtrap.development.systems.utils.advanced.InvUtils;
import bedtrap.development.systems.utils.other.TimerUtils;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static bedtrap.development.systems.modules.world.MiningTS.MTSUtils.canBreak;
import static bedtrap.development.systems.modules.world.MiningTS.MTSUtils.progress;

@Module.Info(name = "MiningTS", category = Module.Category.World)
public class MiningTS extends Module {
    public Setting<Integer> actionDelay = register("ActionDelay", 0, 0, 5);
    public Setting<Boolean> fastBreak = register("FastBreak", false);

    private BlockPos blockPos;
    private Direction direction;

    private final TimerUtils timer = new TimerUtils();

    @Override
    public void onActivate() {
        blockPos = null;
        direction = null;
    }

    @Subscribe
    public void onStartBreaking(StartBreakingBlockEvent event) {
        if (mc.world.getBlockState(event.blockPos).isOf(Blocks.BEDROCK)) return;
        if (blockPos != null && blockPos == event.blockPos) return;

        blockPos = event.blockPos;
        direction = event.direction;

        progress = 0;
    }


    @Subscribe
    public void onTick(TickEvent.Post event) {
        if (blockPos == null) return;

        int slot = pickSlot();
        if (!canBreak(slot, blockPos)) {
            timer.reset();
        } else swap(slot);
    }

    private boolean swap(int slot) {
        if (slot == 420 || progress < 1 || mc.world.isAir(blockPos) || !timer.passedTicks(actionDelay.get())) return false;

        move(mc.player.getInventory().selectedSlot, slot);
        mine(blockPos);
        move(mc.player.getInventory().selectedSlot, slot);
        timer.reset();
        return true;
    }

    private int pickSlot() {
        FindItemResult pick = InvUtils.find(Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE);
        return pick.found() ? pick.slot() : 420;
    }

    private void move(int from, int to) {
        ScreenHandler screenHandler = mc.player.currentScreenHandler;

        DefaultedList<Slot> slots = screenHandler.slots;
        int i = slots.size();
        List<ItemStack> list = Lists.newArrayListWithCapacity(i);
        for (Slot slot : slots) list.add(slot.getStack().copy());

        screenHandler.onSlotClick(from, to, SlotActionType.SWAP, mc.player);
        Int2ObjectMap<ItemStack> stacks = new Int2ObjectOpenHashMap();

        for (int slot = 0; slot < i; slot++) {
            ItemStack stack1 = list.get(slot);
            ItemStack stack2 = slots.get(slot).getStack();

            if (!ItemStack.areEqual(stack1, stack2)) stacks.put(slot, stack2.copy());
        }

        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(screenHandler.syncId, screenHandler.getRevision(), PlayerInventory.MAIN_SIZE + from, to, SlotActionType.SWAP, screenHandler.getCursorStack().copy(), stacks));
    }

    private void mine(BlockPos blockPos) {
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

        if (fastBreak.get()) mc.world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        if (blockPos == null) return;

        int slot = pickSlot();
        if (slot == 420) return;

        Vec3d rPos = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        Renderer3D.drawText(Text.of(String.valueOf(((double) Math.round(progress * 100) / 100))), rPos.x, rPos.y, rPos.z, 0.80, false);
    }
}
