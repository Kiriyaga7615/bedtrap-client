package bedtrap.development.ic;

import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class Friends {

    private static List<String> friends;

    public Friends() {
        friends = new ArrayList<>();
    }

    public static boolean isFriend(String name) {
        return friends.stream().anyMatch(f -> f.equalsIgnoreCase(name));
    }

    public static boolean isFriend(PlayerEntity player) {
        return friends.stream().anyMatch(f -> f.equalsIgnoreCase(player.getGameProfile().getName()));
    }

    public static List<String> getFriends() {
        return friends;
    }

    public static String getFriend(int index) {
        return friends.get(index);
    }
}