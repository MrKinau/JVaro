package systems.kinau.jvaro.manager;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import systems.kinau.jvaro.JVaro;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Iterator;

public class VaroManager {

    private final int INITIAL_WORLDBORDER_SIZE = 3000;

    public VaroManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(JVaro.getInstance(), this::dealWorldBorderDamage, 0, 20);

        Bukkit.getScheduler().runTaskTimerAsynchronously(JVaro.getInstance(), this::updateWorldBorder, 100, 100);
    }

    public void start() throws IOException {
        JVaro plugin = JVaro.getInstance();
        plugin.getDataConfig().set("started", true);
        // set worldborder start in 2 days
        plugin.getDataConfig().set("nextWorldborderChange", LocalDate.now(ZoneId.of("Europe/Berlin")).atStartOfDay().plusDays(3).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).getOffset()).toEpochMilli());
        plugin.getDataConfig().save(plugin.getDataFile());

        setWorldBorder(INITIAL_WORLDBORDER_SIZE);

        Bukkit.getWorlds().forEach(world -> {
            world.setFullTime(0);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(5.0F);
            player.setLevel(0);
            player.setExp(0.0F);
            player.setStatistic(Statistic.TIME_SINCE_REST, 0);
            Iterator<Advancement> advancementIterator = Bukkit.getServer().advancementIterator();
            while (advancementIterator.hasNext()) {
                AdvancementProgress progress = player.getAdvancementProgress(advancementIterator.next());
                for (String criteria : progress.getAwardedCriteria())
                    progress.revokeCriteria(criteria);
            }
        });

        plugin.getTimesManager().varoStart();
        plugin.getDiscordManager().sendStartMessage();
    }

    private void setWorldBorder(int size) {
        setWorldBorder(size, 1000);
    }

    private void setWorldBorder(int size, int maxSize) {
        Bukkit.getWorlds().stream()
                .filter(world -> !world.getName().endsWith("nether") && !world.getName().endsWith("the_end"))
                .forEach(world -> {
                            Bukkit.getScheduler().runTask(JVaro.getInstance(), () -> {
                                WorldBorder worldBorder = world.getWorldBorder();
                                worldBorder.setDamageAmount(0.0);
                                worldBorder.setDamageBuffer(0.0);
                                worldBorder.setWarningTime(0);
                                worldBorder.setWarningTime(0);
                                if (size >= maxSize) worldBorder.setSize(size, 40);
                                Bukkit.getScheduler().runTaskLater(JVaro.getInstance(), () -> {
                                    worldBorder.setCenter(new Location(world, 0.0, 0.0, 0.0));
                                }, 40 * 20);
                            });
                        }
                );
    }

    private void changeWorldBorder(int sizeDiff) {
        changeWorldBorder(sizeDiff, 1000);
    }

    private void changeWorldBorder(int sizeDiff, int maxSize) {
        Bukkit.getWorlds().stream()
                .filter(world -> !world.getName().endsWith("nether") && !world.getName().endsWith("the_end"))
                .forEach(world -> {
                    Bukkit.getScheduler().runTask(JVaro.getInstance(), () -> {
                        WorldBorder worldBorder = world.getWorldBorder();
                        worldBorder.setDamageAmount(0.0);
                        worldBorder.setDamageBuffer(0.0);
                        worldBorder.setWarningDistance(0);
                        worldBorder.setWarningTime(0);
                        worldBorder.setCenter(new Location(world, 0.0, 0.0, 0.0));
                        double newSize = worldBorder.getSize() + sizeDiff;
                        if (newSize >= maxSize) {
                            worldBorder.setSize(newSize);
                            JVaro.getInstance().getDiscordManager().sendWorldBorderChange(world.getName(), (int) newSize);
                        }
                    });
                });
    }

    private void updateWorldBorder() {
        JVaro plugin = JVaro.getInstance();
        long nextChange = plugin.getDataConfig().getLong("nextWorldborderChange");
        if (nextChange < 0) return;
        if (System.currentTimeMillis() > nextChange) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                changeWorldBorder(plugin.getConfig().getInt("dailyBorderDiff"));
                plugin.getDataConfig().set("nextWorldborderChange", LocalDate.now().atStartOfDay().plusDays(2).toInstant(OffsetDateTime.now().getOffset()).toEpochMilli());
                try {
                    plugin.getDataConfig().save(plugin.getDataFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                plugin.getTimesManager().resetTimes();
            });
        }
    }

    private void dealWorldBorderDamage() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (isOutsideOfBorder(player)) {
                Bukkit.getScheduler().runTask(JVaro.getInstance(), () -> {
                    player.damage(1.3);
                });
            }
        });
    }

    private boolean isOutsideOfBorder(Player p) {
        Location loc = p.getLocation();
        WorldBorder border = p.getWorld().getWorldBorder();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX();
        double z = loc.getZ() - center.getZ();
        return x > size || -x > size || z > size || -z > size;
    }
}
