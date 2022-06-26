package bedtrap.development.systems.modules.combat.AutoLog;

import bedtrap.development.events.event.TickEvent;
import bedtrap.development.ic.Setting;
import bedtrap.development.systems.modules.Module;
import bedtrap.development.systems.modules.render.Nametags.Nametags;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.LiteralText;

import java.util.List;

import static bedtrap.development.systems.utils.advanced.InvUtils.find;

@Module.Info(name = "AutoLog", category = Module.Category.Combat)
public class AutoLog extends Module {
    public Setting<Boolean> logOnHealth = register("LogOnHealth", false);
    public Setting<Integer> health = register("Health", 10, 1, 36);
    public Setting<Boolean> logOnTotems = register("LogOnTotems", false);
    public Setting<Integer> totems = register("Totems", 0, 0, 27);
    public Setting<Boolean> logOnPing = register("LogOnPing", false);
    public Setting<Integer> ping = register("Ping", 160, 5, 999);
    public Setting<Boolean> disableAfter = register("AutoToggle", false);
    public Setting<String> displayedInfo = register("DisplayedInfo", List.of("Health", "Totems", "Ping"), "Ping");

    @Subscribe
    public void onTick(TickEvent.Post event) {
        setInfo();

        if (logOnPing.get() && ping.get() <= mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency()) {
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("Your ping is higher than " + ping.get())));
            if (disableAfter.get()) toggle();
        }

        if (logOnTotems.get() && totems.get() > mc.player.getInventory().count(Items.TOTEM_OF_UNDYING)) {
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("You have fewer totems than " + totems.get())));
            if (disableAfter.get()) toggle();
        }

        if (logOnHealth.get() && health.get() > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("Your health is less than  " + health.get())));
            if (disableAfter.get()) toggle();
        }
    }

    private void setInfo() {
        switch (displayedInfo.get()) {
            case "Health" ->
                    setDisplayInfo((mc.player.getHealth() + mc.player.getAbsorptionAmount()) + "/" + health.get());
            case "Totems" ->
                    setDisplayInfo(mc.player.getInventory().count(Items.TOTEM_OF_UNDYING) + "/" + totems.get());
            case "Ping" -> setDisplayInfo(Nametags.get.getPing(mc.player) + "/" + ping.get());
        }
    }
}