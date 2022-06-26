package bedtrap.development.systems.modules.render.BlockESP;

import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.utils.other.Colors;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Module.Info(name = "BlockESP", category = Module.Category.Render)
public class BlockESP extends Module {
    public Setting<String> mode = register("Mode", List.of("Fill", "Outline", "Both"), "Outline");
    public Setting<String> chest = register("Chest", Colors.colors, "Yellow");
    public Setting<String> eChest = register("EChest", Colors.colors, "Magenta");
    public Setting<String> shulker = register("Shulker", Colors.colors, "Pink");
    public Setting<String> endPortal = register("EndPortal", Colors.colors, "Cyan");
    public Setting<Integer> brightness = register("Brightness", 0, -5, 5);
    public Setting<Boolean> tracers = register("Tracers", false);
    public Setting<Boolean> white = register("White", false);

    @Subscribe
    public void onRender(Render3DEvent event) {
        int count = 0;

        for (BlockEntity blockEntity : getBlockEntities()) {
            if (!chest.get("None") && blockEntity instanceof ChestBlockEntity) {
                render(event, blockEntity.getPos(), Colors.getColor(chest.get()), brightness.get(), tracers.get());
                count++;
            }
            if (!eChest.get("None") && blockEntity instanceof EnderChestBlockEntity) {
                render(event, blockEntity.getPos(), Colors.getColor(eChest.get()), brightness.get(), tracers.get());
                count++;
            }
            if (!shulker.get("None") && blockEntity instanceof ShulkerBoxBlockEntity) {
                render(event, blockEntity.getPos(), Colors.getColor(shulker.get()), brightness.get(), tracers.get());
                count++;
            }
            if (!endPortal.get("None") && blockEntity instanceof EndPortalBlockEntity) {
                render(event, blockEntity.getPos(), Colors.getColor(endPortal.get()), brightness.get(), tracers.get());
                count++;
            }
        }

        setDisplayInfo(String.valueOf(count));
    }

    private void render(Render3DEvent event, BlockPos blockPos, Color color, int brightness, boolean tracers) {
        Vec3d vec3d = Renderer3D.get.getRenderPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Box box = new Box(vec3d.x, vec3d.y, vec3d.z, vec3d.x + 1, vec3d.y + 1, vec3d.z + 1);

        Color finalColor = Colors.getBrightness(color, brightness);
        switch (mode.get()) {
            case "Fill" -> Renderer3D.get.drawFilledBox(event.getMatrixStack(), box, finalColor.getRGB());
            case "Outline" -> Renderer3D.get.drawOutlineBox(event.getMatrixStack(), box, finalColor.getRGB());
            case "Both" -> {
                Renderer3D.get.drawBox(event.getMatrixStack(), box, finalColor.getRGB());
                Renderer3D.get.drawOutlineBox(event.getMatrixStack(), box, finalColor.getRGB());
            }
        }

        if (!tracers) return;
        renderTracer(event, blockPos, white.get() ? Color.WHITE : finalColor);
    }

    private void renderTracer(Render3DEvent event, BlockPos blockPos, Color color) {
        Vec3d vec3d = Renderer3D.get.getRenderPosition(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        Vec3d player = Renderer3D.get.getEntityRenderPosition(mc.player, event.getPartialTicks());

        Renderer3D.get.drawLine(event.getMatrixStack(), (float) vec3d.x, (float) vec3d.y, (float) vec3d.z, (float) player.x, (float) player.y, (float) player.z, color.getRGB());
    }

    private List<BlockEntity> getBlockEntities() {
        List<BlockEntity> blockEntities = new ArrayList<>();
        ChunkPos chunkPos = mc.player.getChunkPos();
        int viewDistance = mc.options.viewDistance;
        for (int x = -viewDistance; x <= viewDistance; x++) {
            for (int z = -viewDistance; z <= viewDistance; z++) {
                WorldChunk worldChunk = mc.world.getChunkManager().getWorldChunk(chunkPos.x + x, chunkPos.z + z);
                if (worldChunk != null) blockEntities.addAll(worldChunk.getBlockEntities().values());
            }
        }

        return blockEntities;
    }
}
