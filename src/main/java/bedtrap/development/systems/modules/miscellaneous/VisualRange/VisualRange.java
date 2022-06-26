package bedtrap.development.systems.modules.miscellaneous.VisualRange;

import bedtrap.development.events.event.EntityAddedEvent;
import bedtrap.development.events.event.EntityRemovedEvent;
import bedtrap.development.ic.Friends;
import bedtrap.development.systems.modules.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

@Module.Info(name = "VisualRange", category = Module.Category.Miscellaneous)
public class VisualRange extends Module {
    @Subscribe
    private void onAdded(EntityAddedEvent event) {
        if (!(event.entity instanceof PlayerEntity player) || mc.player == player) return;

        info(name, getMessage(player, true));
    }

    @Subscribe
    private void onRemoved(EntityRemovedEvent event) {
        if (!(event.entity instanceof PlayerEntity player) || mc.player == player) return;

        info(name, getMessage(player, false));
    }

    private String getMessage(PlayerEntity player, boolean added) {
        String name = player.getGameProfile().getName();

        if (added) return Friends.isFriend(player) ? "Epic dude " + Formatting.GREEN + name + Formatting.GRAY + " is here." : "I see an some nn called " + Formatting.RED + name + Formatting.GRAY + ".";
        return Friends.isFriend(player) ? Formatting.GREEN + name + Formatting.GRAY + " is gone." : Formatting.RED + name + Formatting.GRAY + " got scared.";
    }
}
