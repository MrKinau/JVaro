package systems.kinau.jvaro.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import systems.kinau.jvaro.JVaro;
import systems.kinau.jvaro.tasks.StartTask;

public class StartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cHätteste gedacht ich bin dumm, wa? Ne hascht kene Rechte!");
            return true;
        }

        if (JVaro.getInstance().getDataConfig().getBoolean("started")) {
            sender.sendMessage("§cHä? Nicht gecheckt, dass es schon angefangen hat?");
            return true;
        }

        if (JVaro.getInstance().getStartTask() != null) {
            sender.sendMessage("§cSache ma, der Counter läuft doch gerade noch, wat soll'n det!");
            return true;
        }

        JVaro.getInstance().setStartTask(Bukkit.getScheduler().runTaskTimerAsynchronously(JVaro.getInstance(), new StartTask(), 0, 20L));
        return true;
    }
}
