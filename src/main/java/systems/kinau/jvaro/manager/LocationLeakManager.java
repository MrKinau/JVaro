package systems.kinau.jvaro.manager;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import systems.kinau.jvaro.JVaro;
import systems.kinau.jvaro.utils.OfflinePlayerUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class LocationLeakManager {

    public void leakLocation(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        CompoundTag compound = OfflinePlayerUtils.getPlayerData(offlinePlayer.getUniqueId());
        int x = OfflinePlayerUtils.randomize((int) Math.round(OfflinePlayerUtils.getXLocation(compound)));
        int y = OfflinePlayerUtils.randomize((int) Math.round(OfflinePlayerUtils.getYLocation(compound)));
        int z = OfflinePlayerUtils.randomize((int) Math.round(OfflinePlayerUtils.getZLocation(compound)));
        String world = OfflinePlayerUtils.getWorld(compound);

        JVaro.getInstance().getDiscordManager().sendLocationLeakMessage(offlinePlayer, world, x, y, z);
    }

    public void leakAllLocatablePlayers() {
        List<String> locatable = JVaro.getInstance().getDataConfig().getStringList("locatable");
        for (String s : locatable) {
            try {
                UUID uuid = UUID.fromString(s);
                leakLocation(uuid);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    public void enableLocationLeak(UUID playerId) {
        List<String> locatable = JVaro.getInstance().getDataConfig().getStringList("locatable");
        if (!locatable.contains(playerId.toString())) {
            locatable.add(playerId.toString());
            JVaro.getInstance().getDataConfig().set("locatable", locatable);
            try {
                JVaro.getInstance().getDataConfig().save(JVaro.getInstance().getDataFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disableLocationLeak(UUID playerId) {
        List<String> locatable = JVaro.getInstance().getDataConfig().getStringList("locatable");
        if (locatable.contains(playerId.toString())) {
            System.out.println("Disabled location leak for: " + playerId);
            locatable.remove(playerId.toString());
            JVaro.getInstance().getDataConfig().set("locatable", locatable);
            try {
                JVaro.getInstance().getDataConfig().save(JVaro.getInstance().getDataFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
