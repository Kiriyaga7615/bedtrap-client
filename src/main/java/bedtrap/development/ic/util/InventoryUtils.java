package bedtrap.development.ic.util;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtils {

    public static MinecraftClient mc = MinecraftClient.getInstance();

    public static void swap(int slot) {
        if (slot != -1)
            mc.player.getInventory().selectedSlot = slot;
    }

    public static int findSlot(Item item) {
        for (int j = 0; j < 9; j++) {
            if (mc.player.getInventory().getStack(j).getItem() == item) {
                return j;
            }
        }
        return -1;
    }

    public static int findSlot(Block block) {
        for (int j = 0; j < 9; j++) {
            if (mc.player.getInventory().getStack(j).getItem() instanceof BlockItem) {
                if (((BlockItem) mc.player.getInventory().getStack(j).getItem()).getBlock() == block) {
                    return j;
                }
            }
        }
        return -1;
    }

    public static int findSlot(Class c) {
        for (int j = 0; j < 9; j++) {
            ItemStack stack = mc.player.getInventory().getStack(j);
            if (c.isInstance(stack.getItem())) return j;
            if (stack.getItem() instanceof BlockItem) {
                Block b = ((BlockItem) stack.getItem()).getBlock();
                if (c.isInstance(b)) return j;
            }
        }
        return -1;
    }
}
