package systems.kinau.jvaro.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import systems.kinau.jvaro.JVaro;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class StartTask implements Runnable {

    private int counter;
    private String currentPlayer;
    private final List<String> CLAIMS = List.of("%s hat den Start verpennt", "%s wird als erstes sterben", "%s wird durch eine Falle sterben",
            "%s versteht 1.9er PvP nicht", "%s findet die letzte Ente", "%s <TODO: Joke über Leon einfügen>",
            "%s diskutiert nur über die Regeln", "fretoger plant schon ein neues Projekt", "%s freut sich schon auf den Ledaria Release", "MinecraftiUndCo ist \"kurz\" afk",
            "%s stirbt an der Border", "%s wird nur Fallen bauen", "%s wird in der Hölle verglühen");

    public StartTask() {
        this.counter = 60;
    }

    public StartTask(int counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        if (counter < 0) {
            if (JVaro.getInstance().getStartTask() != null)
                JVaro.getInstance().getStartTask().cancel();
            return;
        }

        if (counter == 0)
            Bukkit.getScheduler().runTask(JVaro.getInstance(), () -> {
                try {
                    JVaro.getInstance().getVaroManager().start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        if (counter % 5 == 0)
            currentPlayer = getRandomPlayer();

        Bukkit.getOnlinePlayers().forEach(player -> {
            String claim = CLAIMS.get((int)Math.ceil(counter / 5.0));
            if (claim.contains("%s"))
                claim = String.format(claim, currentPlayer);

            if (counter == 0) {
                if (JVaro.getInstance().getStartTask() != null)
                    JVaro.getInstance().getStartTask().cancel();
                player.sendTitle("§cLOS!", "§7" + claim, 0, 44, 7);
                return;
            }
            player.sendTitle("§c" + counter, "§7" + claim, 0, 24, 0);
        });
        counter--;
    }

    private String getRandomPlayer() {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
                .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                .findAny()
                .get();
    }
}
