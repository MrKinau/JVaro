package systems.kinau.jvaro.commands;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import systems.kinau.jvaro.JVaro;
import systems.kinau.jvaro.utils.OfflinePlayerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class LeakLocationCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (offlinePlayer.getName() == null) {
                    sender.sendMessage("§cWow sogar zu dumm zum tippen. GG");
                    return true;
                }

                CompoundTag compound = OfflinePlayerUtils.getPlayerData(offlinePlayer.getUniqueId());
                double x = OfflinePlayerUtils.getXLocation(compound);
                double z = OfflinePlayerUtils.getZLocation(compound);
                String world = OfflinePlayerUtils.getWorld(compound);

                JVaro.getInstance().getDiscordManager().sendLocationLeakMessage(offlinePlayer, world, (int)Math.round(x), (int)Math.round(z));
                sender.sendMessage("§aKoordinaten geleaked!");
            } else sender.sendMessage("Dumm? /locationleak <Spieler>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1)
            return Arrays.stream(Bukkit.getOfflinePlayers())
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .filter(it -> it.startsWith(args[0]))
                    .collect(Collectors.toList());
            else
                return Collections.emptyList();
    }
}
