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
    private List<String> claims = List.of("%s hat den Start verpennt", "%s wird als erstes sterben", "%s wird den Stegi machen",
            "%s versteht 1.9er PvP nicht", "%s überlebt die 2. Nacht nicht", "%s <TODO: Joke über Leon einfügen>",
            "%s ist vom Himmel gefallen", "fretoger plant schon ein neues Projekt", "%s wird gewinnen", "MinecraftiUndCo ist \"kurz\" afk",
            "%s hat seinen Teampartner vergessen", "%s findet niemals Dias", "%s wird in der Hölle verglühen");

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
            String claim = claims.get((int)Math.ceil(counter / 5.0));
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
