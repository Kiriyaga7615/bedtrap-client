package bedtrap.development.systems.modules.other.HUD;

import bedtrap.development.BedTrap;
import bedtrap.development.events.event.GameJoinedEvent;
import bedtrap.development.events.event.PacketEvent;
import bedtrap.development.events.event.RenderEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.ic.util.Renderer;
import bedtrap.development.mixins.MinecraftClientAccessor;
import bedtrap.development.systems.modules.Modules;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.other.Colors.Colors;
import bedtrap.development.systems.modules.other.Font.Font;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

@Module.Info(name = "Hud", category = Module.Category.Other)
public class HUD extends Module {

    public static HUD get = new HUD();
    public Setting<Boolean> brighter = register("Brighter", true);
    public Setting<Boolean> watermark = register("Watermark", true);
    public Setting<Boolean> weeeed = register("420", false);
    public Setting<Boolean> arrayList = register("ArrayList", true);
    public Setting<String> arraySort = register("Sort", Arrays.asList("Length", "ABC"), "Length");
    public Setting<Boolean> direction = register("Direction", true);
    public Setting<Boolean> coords = register("Coords", true);
    public Setting<Boolean> netherCoords = register("NetherCoords", true);
    public Setting<Boolean> speed = register("Speed", true);
    public Setting<Boolean> durability = register("Durability", true);
    public Setting<Boolean> ping = register("Ping", false);
    public Setting<Boolean> TPS = register("TPS", false);
    public Setting<Boolean> FPS = register("FPS", true);
    public Setting<Boolean> armor = register("Armor", false);
    public Setting<String> rendering = register("Rendering", Arrays.asList("Up", "Down"), "Up");

    private final float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate = -1;
    private long timeGameJoined;

    public HUD() {
        get = this;
    }

    private static double animation(double current, double end, double factor, double start) {
        double progress = (end - current) * factor;

        if (progress > 0) {
            progress = Math.max(start, progress);
            progress = Math.min(end - current, progress);
        } else if (progress < 0) {
            progress = Math.min(-start, progress);
            progress = Math.max(end - current, progress);
        }

        return current + progress;
    }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (cantUpdate()) return;
        int arrayModules = 0;
        int hudElements = 0;

        MatrixStack matrixstack = new MatrixStack();

        if (armor.get()) {
            double x = mc.getWindow().getScaledWidth() / 2 + 7 * mc.getWindow().getScaleFactor();
            double y = mc.getWindow().getScaledHeight() - 28 * mc.getWindow().getScaleFactor();
            double armorX;
            double armorY;
            int slot = 3;

            for (int position = 0; position < 4; position++) {
                ItemStack itemStack = mc.player.getInventory().getArmorStack(slot);

                armorX = x + position * 18;
                armorY = y;


                Renderer.drawItem(itemStack, (int) armorX, (int) armorY, 1, true);
                if (itemStack.isDamageable()) {
                    final float g = (itemStack.getMaxDamage() - (float) itemStack.getDamage()) / itemStack.getMaxDamage();
                    final float r = 1.0f - g;
                    String text = Integer.toString(Math.round(((itemStack.getMaxDamage() - itemStack.getDamage()) * 100f) / (float) itemStack.getMaxDamage()));
                    bedtrap.development.systems.modules.other.Font.Font.get.renderHudText(matrixstack, text, (float) armorX + 1, (float) armorY - 10, new Color(r, g, 0).getRGB());
                }
                slot--;
            }
        }

        if (coords.get()) {
            bedtrap.development.systems.modules.other.Font.Font.get.renderHudText(matrixstack,
                    Formatting.GRAY + "XYZ " + Formatting.WHITE +
                            getString(mc.player.getX()) +
                            Formatting.GRAY + ", " + Formatting.WHITE +
                            getString(mc.player.getY()) +
                            Formatting.GRAY + ", " + Formatting.WHITE +
                            getString(mc.player.getZ()) +
                            (netherCoords.get() ? Formatting.GRAY + " [" + Formatting.WHITE +
                                    getString(worldCords(mc.player.getX())) +
                                    Formatting.GRAY + ", " + Formatting.WHITE +
                                    getString(worldCords(mc.player.getZ())) + Formatting.GRAY + "]" : ""),
                    2, mc.getWindow().getScaledHeight() - mc.textRenderer.fontHeight - (mc.currentScreen instanceof ChatScreen ? 15 : 1), -1);
        }

        if (direction.get()) {
            bedtrap.development.systems.modules.other.Font.Font.get.renderHudText(matrixstack, getFacing() + Formatting.GRAY + " [" + Formatting.WHITE + getTowards() + Formatting.GRAY + "]",
                    2, mc.getWindow().getScaledHeight() - mc.textRenderer.fontHeight * (coords.get() ? 2 : 1) - (mc.currentScreen instanceof ChatScreen ? 15 : 1), -1);

        }

        if (watermark.get()) {
            bedtrap.development.systems.modules.other.Font.Font.get.renderHudText(matrixstack,
                    BedTrap.name + (weeeed.get() ? "420" : " "), 2, 2, color());
        }

        if (arrayList.get()) {
            int[] counter = {1};
            // Formatting.GRAY + " [" + Formatting.WHITE + info + Formatting.GRAY + "]"
            ArrayList<Module> modules = new ArrayList<>(Modules.getModules());
            switch (arraySort.get()) {
                case ("Length"):
                    modules.sort(Comparator.comparing(m -> -(mc.textRenderer.getWidth(m.getName()) + mc.textRenderer.getWidth(m.getDisplayInfo()))));
                case ("ABC"):
                    modules.sort(Comparator.comparing(m -> getName() + getDisplayInfo()));
            }
            for (Module value : modules) {
                Module module;
                module = value;
                if (module.isDrawn() && module.isActive()) {
                    int x = mc.getWindow().getScaledWidth();
                    int y = rendering.get().equalsIgnoreCase("Up") ? 3 + (arrayModules * 10) : mc.getWindow().getScaledHeight() - mc.textRenderer.fontHeight - 1 - (arrayModules * 10);
                    int lWidth = mc.textRenderer.getWidth(module.getName() + module.getDisplayInfo());

                    if (mc.currentScreen instanceof ChatScreen && rendering.get().equalsIgnoreCase("Down")) y = y - 15;

                    if (module.arrayAnimation < lWidth && module.isActive()) {
                        module.arrayAnimation = animation(module.arrayAnimation, lWidth + 5, 1, 0.1);
                    } else if (module.arrayAnimation > 1.5f && !module.isActive()) {
                        module.arrayAnimation = animation(module.arrayAnimation, -1.5, 1, 0.1);
                    } else if (module.arrayAnimation <= 1.5f && !module.isActive()) {
                        module.arrayAnimation = -1f;
                    }
                    if (module.arrayAnimation > lWidth && module.isActive()) {
                        module.arrayAnimation = lWidth;
                    }
                    // TODO: 23.04.2022 свой FontRender, без него анимацию в аррай листе не сделать.
                    x -= module.arrayAnimation;


                    mc.textRenderer.drawWithShadow(matrixstack, module.getName() + module.getDisplayInfo(), x - 2, y, color());

                    arrayModules++;
                    counter[0]++;
                }
            }
        }

        if (speed.get()) {
            int x = mc.getWindow().getScaledWidth();
            int y = rendering.get().equalsIgnoreCase("Up") ? mc.getWindow().getScaledHeight() - mc.textRenderer.fontHeight - 1 - (hudElements * 10) : 3 + (hudElements * 10);
            double speed = getSpeed();

            if (mc.currentScreen instanceof ChatScreen && rendering.get().equalsIgnoreCase("Up")) y = y - 15;

            String text = Formatting.GRAY + "Speed " + Formatting.WHITE + getString(speed) + "km/h";

            float x2 = (float) (x - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? bedtrap.development.systems.modules.other.Font.Font.get.getWidth(text) : mc.textRenderer.getWidth(text)) - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? -17 : 2));
            bedtrap.development.systems.modules.other.Font.Font.get.renderHudText(matrixstack, text,
                    x2, y, -1);

            hudElements++;
        }

        if (durability.get()) {
            if (!mc.player.getMainHandStack().isEmpty() && mc.player.getMainHandStack().isDamageable()) {
                int x = mc.getWindow().getScaledWidth();
                int y = rendering.get().equalsIgnoreCase("Up") ? mc.getWindow().getScaledHeight() - mc.textRenderer.fontHeight - 1 - (hudElements * 10) : 3 + (hudElements * 10);
                if (mc.currentScreen instanceof ChatScreen && rendering.get().equalsIgnoreCase("Up")) y = y - 15;

                ItemStack stack = mc.player.getMainHandStack();

                String content = String.valueOf(stack.getMaxDamage() - stack.getDamage());

                final float g = (stack.getMaxDamage() - (float) stack.getDamage()) / stack.getMaxDamage();
                final float r = 1.0f - g;

                String durability = Formatting.GRAY + "Durability ";
                String text = content;

                float x2 = (float) (x - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? bedtrap.development.systems.modules.other.Font.Font.get.getWidth(durability + text) : mc.textRenderer.getWidth(durability + text)) - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? -8 : 2));
                bedtrap.development.systems.modules.other.Font.Font.get.renderHudText(matrixstack, durability,
                        x2, y, -1);
                bedtrap.development.systems.modules.other.Font.Font.get.renderHudText(matrixstack, text,
                        (float) (x - bedtrap.development.systems.modules.other.Font.Font.get.getWidth(text) - 2), y, new Color(r, g, 0).getRGB());

                hudElements++;
            }
        }

        if (ping.get()) {
            int x = mc.getWindow().getScaledWidth();
            int y = rendering.get().equalsIgnoreCase("Up") ? mc.getWindow().getScaledHeight() - mc.textRenderer.fontHeight - 1 - (hudElements * 10) : 3 + (hudElements * 10);
            if (mc.currentScreen instanceof ChatScreen && rendering.get().equalsIgnoreCase("Up")) y = y - 15;

            PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());

            String content = Formatting.GRAY + "Ping " + Formatting.WHITE + playerListEntry.getLatency();

            float x2 = (float) (x - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? bedtrap.development.systems.modules.other.Font.Font.get.getWidth(content) : mc.textRenderer.getWidth(content)) - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? -17 : 2));
            bedtrap.development.systems.modules.other.Font.Font.get.renderHudText(matrixstack, content,
                    x2, y, -1);

            hudElements++;
        }

        if (TPS.get()) {
            int x = mc.getWindow().getScaledWidth();
            int y = rendering.get().equalsIgnoreCase("Up") ? mc.getWindow().getScaledHeight() - mc.textRenderer.fontHeight - 1 - (hudElements * 10) : 3 + (hudElements * 10);
            if (mc.currentScreen instanceof ChatScreen && rendering.get().equalsIgnoreCase("Up")) y = y - 15;

            String content = Formatting.GRAY + "TPS " + Formatting.WHITE + String.format("%.1f", getTickRate());

            float x2 = (float) (x - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? bedtrap.development.systems.modules.other.Font.Font.get.getWidth(content) : mc.textRenderer.getWidth(content)) - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? -17 : 2));
            bedtrap.development.systems.modules.other.Font.Font.get.renderHudText(matrixstack, content,
                    x2, y, -1);

            hudElements++;
        }

        if (FPS.get()) {
            int x = mc.getWindow().getScaledWidth();
            int y = rendering.get().equalsIgnoreCase("Up") ? mc.getWindow().getScaledHeight() - mc.textRenderer.fontHeight - 1 - (hudElements * 10) : 3 + (hudElements * 10);
            if (mc.currentScreen instanceof ChatScreen && rendering.get().equalsIgnoreCase("Up")) y = y - 15;

            String content = Formatting.GRAY + "FPS " + Formatting.WHITE + MinecraftClientAccessor.getFps();

            float x2 = (float) (x - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? bedtrap.development.systems.modules.other.Font.Font.get.getWidth(content) : mc.textRenderer.getWidth(content)) - (bedtrap.development.systems.modules.other.Font.Font.get.hud() ? -17 : 2));
            Font.get.renderHudText(matrixstack, content,
                    x2, y, -1);

            hudElements++;
        }
    }

    //tickrate

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof WorldTimeUpdateS2CPacket) {
            long now = System.currentTimeMillis();
            float timeElapsed = (float) (now - timeLastTimeUpdate) / 1000.0F;
            tickRates[nextIndex] = clamp(20.0f / timeElapsed, 0.0f, 20.0f);
            nextIndex = (nextIndex + 1) % tickRates.length;
            timeLastTimeUpdate = now;
        }
    }

    @Subscribe
    private void onGameJoined(GameJoinedEvent event) {
        Arrays.fill(tickRates, 0);
        nextIndex = 0;
        timeGameJoined = timeLastTimeUpdate = System.currentTimeMillis();
    }

    public float getTickRate() {
        if (mc.world == null || mc.player == null) return 0;
        if (System.currentTimeMillis() - timeGameJoined < 4000) return 20;

        int numTicks = 0;
        float sumTickRates = 0.0f;
        for (float tickRate : tickRates) {
            if (tickRate > 0) {
                sumTickRates += tickRate;
                numTicks++;
            }
        }
        return sumTickRates / numTicks;
    }

    public float getTimeSinceLastTick() {
        long now = System.currentTimeMillis();
        if (now - timeGameJoined < 4000) return 0;
        return (now - timeLastTimeUpdate) / 1000f;
    }


    private float clamp(float value, float min, float max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    private String getString(double pos) {
        return String.format("%.1f", pos).replace(',', '.');
    }

    private String getFacing() {
        return switch (MathHelper.floor((double) (mc.player.getYaw() * 4.0F / 360.0F) + 0.5D) & 3) {
            case 0 -> "South";
            case 1 -> "West";
            case 2 -> "North";
            case 3 -> "East";
            default -> "Invalid";
        };
    }

    private double worldCords(double cords) {
        if (mc.world.getDimension().isRespawnAnchorWorking()) return (cords * 8);
        return cords / 8;
    }

    private String getTowards() {
        return switch (MathHelper.floor((double) (mc.player.getYaw() * 4.0F / 360.0F) + 0.5D) & 3) {
            case 0 -> "+Z";
            case 1 -> "-X";
            case 2 -> "-Z";
            case 3 -> "+X";
            default -> "Invalid";
        };
    }

    private double getSpeed() {
        if (mc.player == null) return 0;

        double tX = Math.abs(mc.player.getX() - mc.player.prevX);
        double tZ = Math.abs(mc.player.getZ() - mc.player.prevZ);
        double length = Math.sqrt(tX * tX + tZ * tZ);

//        Timer timer = Modules.get().get(Timer.class);
//        if (timer.isActive()) length *= Modules.get().get(Timer.class).getMultiplier();

        return (length * 20) * 3.6;
    }

    public int color() {
        return brighter.get() ? new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue()).brighter().getRGB() : new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue()).getRGB();
    }

    public Color colorType() {
        return brighter.get() ? new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue()).brighter() : new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue());
    }

    public int revertColor() {
        return !brighter.get() ? new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue()).brighter().getRGB() : new Color(Colors.get.r.get().intValue(), Colors.get.g.get().intValue(), Colors.get.b.get().intValue()).getRGB();
    }
}
