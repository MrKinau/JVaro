package systems.kinau.jvaro.tasks;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import systems.kinau.jvaro.JVaro;

import java.util.function.Consumer;

@AllArgsConstructor
public class LogOffTask implements Runnable {

    private Player player;
    private Consumer<Boolean> callBack;
    private int counter;


    @Override
    public void run() {
        if ((counter % 10 == 0 && counter > 0) || (counter >= 2 && counter <= 9))
            player.sendMessage("§cDeine Zeit läuft in " + counter + " Sekunden ab!");
        else if (counter == 1)
            player.sendMessage("§cDeine Zeit läuft in einer Sekunde ab!");
        else if (counter <= 0) {
            callBack.accept(true);
            player.sendMessage("§4Deine Zeit ist abgelaufen! Disconnecte, wenn du nicht gerade um dein Leben kämpfst!");
            System.out.println(player.getName() + " should log off ----");
            JVaro.getInstance().getDiscordManager().sendShouldLogoutMessage(player);
            return;
        }
        counter--;
    }
}
