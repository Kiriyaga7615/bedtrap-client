package bedtrap.development.systems.modules.render.Nametags;

import bedtrap.development.events.event.GameJoinedEvent;
import bedtrap.development.events.event.PacketEvent;
import bedtrap.development.events.event.Render3DEvent;
import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Friends;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer3D;
import bedtrap.development.systems.modules.Module;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

@Module.Info(name = "Nametags", category = Module.Category.Render)
public class Nametags extends Module {
    public Setting<Boolean> ping = register("Ping", true);
    public Setting<Boolean> health = register("Health", true);
    public Setting<Boolean> pops = register("Pops", true);
    public Setting<Double> scale = register("Scale", 1.1, 0.5, 2, 2);
    public Setting<String> formatting = register("Formatting", List.of("Italic", "Bold", "Both", "None"), "None");
    public Setting<Boolean> background = register("Background", true);

    private final Object2IntMap<UUID> totemPopMap = new Object2IntOpenHashMap<>();
    int popped;

    @Override
    public void onActivate() {
        totemPopMap.clear();
        popped = 0;
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        for (Entity entity : mc.world.getEntities()) {
            Vec3d rPos = entity.getPos().subtract(Renderer3D.getInterpolationOffset(entity)).add(0, entity.getHeight() + 0.50, 0);
            double size = Math.max(2 * (mc.cameraEntity.distanceTo(entity) / 20), 1);

            if (entity instanceof PlayerEntity) {
                if (entity == mc.player || ((PlayerEntity) entity).isDead()) continue;

                Renderer3D.drawText(Text.of(" "+getPlayerInfo((PlayerEntity) entity)+" "), rPos.x, rPos.y, rPos.z, scale.get() * size, background.get());

            }
        }
    }

    @Subscribe
    private void onPop(PacketEvent.Receive event) {
        if (!pops.get()) return;
        if (!(event.packet instanceof EntityStatusS2CPacket p)) return;
        if (p.getStatus() != 35) return;
        Entity entity = p.getEntity(mc.world);
        if (!(entity instanceof PlayerEntity)) return;
        if ((entity.equals(mc.player))) return;

        synchronized (totemPopMap) {
            popped = totemPopMap.getOrDefault(entity.getUuid(), 0);
            totemPopMap.put(entity.getUuid(), ++popped);
        }
    }

    @Subscribe
    private void onDeath(TickEvent.Post event) {
        synchronized (totemPopMap) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (!totemPopMap.containsKey(player.getUuid())) continue;

                if (player.deathTime > 0 || player.getHealth() <= 0) totemPopMap.removeInt(player.getUuid());
            }
        }
    }

    @Subscribe
    public void onJoin(GameJoinedEvent event) {
        totemPopMap.clear();
        popped = 0;
    }

    private String getPlayerInfo(PlayerEntity player) {
        StringBuilder sb = new StringBuilder();

        sb.append(formattingByName(player) + (ping.get() ? " " + getPing(player)+"ms" : ""));
        sb.append(health.get() ? formattingByHealth(player) : "");
        sb.append(pops.get() ? formattingByPops(getPops(player)) : "");

        return sb.toString();
    }

    private int getPops(PlayerEntity p) {
        if (!totemPopMap.containsKey(p.getUuid())) return 0;
        return totemPopMap.getOrDefault(p.getUuid(), 0);
    }

    public int getPing(PlayerEntity player) {
        if (mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }

    private String formattingByHealth(PlayerEntity player) {
        DecimalFormat df = new DecimalFormat("##");
        double hp = Math.round(player.getHealth() + player.getAbsorptionAmount());
        String health = df.format(Math.round(hp));

        if (hp >= 19) return shortForm() + Formatting.GREEN + " " + health;
        if (hp >= 13 && hp <= 18) return shortForm() + Formatting.YELLOW + " " + health;
        if (hp >= 8 && hp <= 12) return shortForm() + Formatting.GOLD + " " + health;
        if (hp >= 6 && hp <= 7) return  shortForm() + Formatting.RED + " " + health;
        if (hp <= 5) return  shortForm() + Formatting.DARK_RED + " " + health;

        return shortForm() + Formatting.GREEN + " unexpected";
    }

    private String shortForm() {
        return switch (formatting.get()) {
            case "Italic" -> "" + Formatting.ITALIC;
            case "Bold" -> "" + Formatting.BOLD ;
            case "Both" -> "" + Formatting.ITALIC + Formatting.BOLD;
            default -> "";
        };
    }

    private String formattingByName(PlayerEntity player) {
        if (player.isInSneakingPose()) return shortForm() + Formatting.GOLD + player.getEntityName();
        if (Friends.isFriend(player.getEntityName())) return shortForm() + Formatting.AQUA + player.getName().asString();

        return shortForm() + Formatting.WHITE + player.getEntityName();
    }

    private String formattingByPops(int pops) {
        switch (pops){
            case 0: return "";
            case 1: return shortForm() + Formatting.GREEN + " -" + pops;
            case 2: return shortForm() + Formatting.DARK_GREEN + " -" + pops;
            case 3: return shortForm() + Formatting.YELLOW + " -" + pops;
            case 4: return shortForm() + Formatting.GOLD + " -" + pops;
            case 5: return shortForm() + Formatting.RED + " -" + pops;
            default: return shortForm() + Formatting.DARK_RED + " -" + pops;

        }
    }

    public static Nametags get;

    public Nametags() {
        get = this;
    }
}
