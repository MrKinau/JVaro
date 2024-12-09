package systems.kinau.jvaro.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import systems.kinau.jvaro.JVaro;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

                JVaro.getInstance().getLocationLeakManager().leakLocation(offlinePlayer.getUniqueId());
                JVaro.getInstance().getLocationLeakManager().enableLocationLeak(offlinePlayer.getUniqueId());

                sender.sendMessage("§aKoordinaten geleaked und Persönlichkeit stalkbar gemacht!");
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
