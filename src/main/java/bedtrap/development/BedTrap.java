package bedtrap.development;

import bedtrap.development.ic.Friends;
import bedtrap.development.ic.Settings;
import bedtrap.development.systems.commands.Commands;
import bedtrap.development.gui.Gui;
import bedtrap.development.ic.Configs;
import bedtrap.development.systems.modules.Modules;
import bedtrap.development.systems.utils.advanced.DamageUtils;
import bedtrap.development.systems.utils.game.ChatUtils;
import com.google.common.eventbus.EventBus;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;


public class BedTrap implements ModInitializer {

    public static final String name = "BedTrap";
    public static final String version = "453";
    public static long initTime;

    public static final EventBus EventBus = new EventBus();
    private static Modules Modules;
    private static Commands Commands;
    private static Friends Friends;
    private static Configs Configs;
    private static Settings Settings;
    private static Gui Gui;

    public static DamageUtils Damage;

    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static BedTrap INSTANCE;

    public BedTrap() {
        INSTANCE = this;
    }

    @Override
    public void onInitialize() {
        System.out.println("Initializing " + name  + "...");
        initTime = System.currentTimeMillis();
        Settings = new Settings();
        Modules = new Modules();
        Commands = new Commands();
        Gui = new Gui();
        Friends = new Friends();
        Configs = new Configs();
        Configs.load();

        Damage = new DamageUtils();

        System.out.println("BedTrap started in " + (System.currentTimeMillis() - initTime) + " ms.");
        Runtime.getRuntime().addShutdownHook(new Configs());
    }

    public static Modules getModuleManager() {
        return Modules;
    }

    public static Commands getCommandManager() {
        return Commands;
    }

    public static Friends getFriendManager() {
        return Friends;
    }

    public static Settings getSettingManager() {
        return Settings;
    }

    public static Gui getClickGui() {
        return Gui;
    }
}
