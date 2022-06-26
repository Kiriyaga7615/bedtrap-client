package bedtrap.development.systems.modules;

import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Modules {

    private static ArrayList<Module> modules;

    public Modules() {
        modules = new ArrayList<>();

        Set<Class<? extends Module>> reflections = new Reflections("bedtrap.development.systems.modules").getSubTypesOf(Module.class);
        reflections.forEach(aClass -> {
            try {
                if (aClass.isAnnotationPresent(Module.Info.class)) modules.add(aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        modules.sort(Comparator.comparing(Module::getName));
    }

    public static ArrayList<Module> getModules() {
        return Modules.modules;
    }

    public static List<Module> getModules(Module.Category c) {
        List<Module> modules = new ArrayList<Module>();
        for (Module m : Modules.modules) {
            if (m.getCategory() == c)
                modules.add(m);
        }
        return modules;
    }

    public static Module getModule(String name) {
        for (Module m : Modules.modules) {
            if (m.getName().equalsIgnoreCase(name))
                return m;
        }
        return null;
    }
}
