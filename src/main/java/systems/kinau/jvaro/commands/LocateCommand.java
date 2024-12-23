package systems.kinau.jvaro.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import systems.kinau.jvaro.JVaro;
import systems.kinau.jvaro.utils.OfflinePlayerUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LocateCommand implements CommandExecutor {

    private final Cache<UUID, Long> cantUseLocate = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    private final String messagePattern = "§e%s befindet sich bei §cX=%s§e, §cY=%s§e, §cZ=%s §ein §c%s§e.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c/locate <Spieler>");
            return true;
        }

        Long useLocateAt;
        if (sender instanceof Player && (useLocateAt = cantUseLocate.getIfPresent(((Player) sender).getUniqueId())) != null) {
            long msLeft = useLocateAt - System.currentTimeMillis();
            long secondsLeft = (int)Math.round(msLeft / 1000.0);
            String timeLeft = secondsLeft <= 0 ? "weniger als einer Sekunde" : (secondsLeft == 1 ? "einer Sekunde" : secondsLeft + " Sekunden");
            sender.sendMessage("§cDu kannst diesen Command erst in §e" + timeLeft + " §cwieder nutzen!");
            return true;
        }

        UUID locatedUuid = getUUID(args[0]);

        String loc = getLocation(locatedUuid);
        if (loc == null) {
            sender.sendMessage("§cWow sogar zu dumm zum tippen. GG. Es gibt niemanden, der so einen komischen Namen hat.");
            return true;
        }

        Player locatedPlayer = Bukkit.getPlayer(locatedUuid);
        if (locatedPlayer == null || !locatedPlayer.isOnline()) {
            sender.sendMessage("§cWow sogar zu dumm zum tippen. GG. Dieser Spieler ist nicht online, du Dulli.");
            return true;
        }

        List<String> locatable = JVaro.getInstance().getDataConfig().getStringList("locatable");
        if (!locatable.contains(locatedUuid.toString())) {
            sender.sendMessage("§cDiese Persönlichkeit darf nicht von dir gestalkt werden.");
            if (sender instanceof Player)
                return true;
        }

        sender.sendMessage(loc);

        if (sender instanceof Player)
            cantUseLocate.put(((Player) sender).getUniqueId(), System.currentTimeMillis() + 60_000);

        return true;
    }

    private UUID getUUID(String playerName) {
        UUID uuid;

        Player player = Bukkit.getServer().getPlayer(playerName);
        if (player != null && player.isOnline())
            uuid = player.getUniqueId();
        else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (offlinePlayer.getName() != null) {
                uuid = offlinePlayer.getUniqueId();
            } else
                return null;
        }
        return uuid;
    }

    private String getLocation(UUID uuid) {
        if (uuid == null) return null;

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            String world = player.getLocation().getWorld().getName();
            String x = String.valueOf(player.getLocation().getBlockX());
            String y = String.valueOf(player.getLocation().getBlockY());
            String z = String.valueOf(player.getLocation().getBlockZ());
            return String.format(messagePattern, player.getName(), x, y, z, world);
        }

        CompoundTag tag = OfflinePlayerUtils.getPlayerData(uuid);
        if (tag == null) return null;

        String world = OfflinePlayerUtils.getWorld(tag);
        String x = String.valueOf((int)Math.round(OfflinePlayerUtils.getXLocation(tag)));
        String y = String.valueOf((int)Math.round(OfflinePlayerUtils.getYLocation(tag)));
        String z = String.valueOf((int)Math.round(OfflinePlayerUtils.getZLocation(tag)));
        return String.format(messagePattern, Bukkit.getOfflinePlayer(uuid).getName(), x, y, z, world);
    }
}
