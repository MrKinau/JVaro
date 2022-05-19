package systems.kinau.jvaro.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.DecimalFormat;
import java.util.Arrays;

public class SimpleTeamCommand implements CommandExecutor {

    private final DecimalFormat FORMATTER = new DecimalFormat("00");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length != 0) {
                if (Bukkit.getScoreboardManager() == null) return true;
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                int nextTeamId = scoreboard.getTeams().size() + 1;
                String teamName = "t" + FORMATTER.format(nextTeamId);
                String displayName = "T" + FORMATTER.format(nextTeamId);
                String prefix = "[T" + FORMATTER.format(nextTeamId) + "] ";
                Team team = scoreboard.registerNewTeam(teamName);

                team.setDisplayName(displayName);
                team.setPrefix(prefix);
                team.setSuffix("§r");
                team.setAllowFriendlyFire(false);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                Arrays.stream(args).forEach(arg -> {
                    if (arg.startsWith("#")) {
                        team.setPrefix(ChatColor.of(arg).toString() + prefix + ChatColor.RESET);
                    } else {
                        team.addEntry(arg);
                    }
                });
                sender.sendMessage("§aTeam §6" + displayName + " §aerstellt!");
            } else sender.sendMessage("Dumm? /simpleteam <Spieler1> <Spieler2> [#rrggbb]");
        } else sender.sendMessage("§cHätteste gedacht ich bin dumm, wa? Ne hascht kene Rechte!");
        return true;
    }
}
