package systems.kinau.jvaro.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;

import java.io.File;
import java.security.SecureRandom;
import java.util.UUID;

public class OfflinePlayerUtils {

    private static final SecureRandom RAND = new SecureRandom();

    public static CompoundTag getPlayerData(UUID uuid) {
        CompoundTag tag = null;
        try {
            File file = new File("world/playerdata/", uuid + ".dat");
            if (file.exists() && file.isFile()) {
                tag = net.minecraft.nbt.NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tag;
    }

    public static double getXLocation(CompoundTag tag) {
        ListTag tagList = tag.getList("Pos", 6);
        return tagList.getDouble(0);
    }

    public static double getYLocation(CompoundTag tag) {
        ListTag tagList = tag.getList("Pos", 6);
        return tagList.getDouble(1);
    }

    public static double getZLocation(CompoundTag tag) {
        ListTag tagList = tag.getList("Pos", 6);
        return tagList.getDouble(2);
    }

    public static String getWorld(CompoundTag tag) {
        String[] namespacedParts = tag.getString("Dimension").split(":");
        return namespacedParts[namespacedParts.length - 1];
    }

    public static int randomize(int coordinate) {
        return coordinate + RAND.nextInt(21) - 10;
    }
}
