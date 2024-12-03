package systems.kinau.jvaro.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import systems.kinau.jvaro.JVaro;
import systems.kinau.jvaro.tasks.LogOffTask;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TimesManager {

    private Timestamp todayStart = new Timestamp(LocalDate.now(ZoneId.of("Europe/Berlin")).atTime(JVaro.getInstance().getConfig().getInt("startLoginTime"), 0).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).getOffset()).toEpochMilli());
    private Timestamp todayEnd = new Timestamp(LocalDate.now(ZoneId.of("Europe/Berlin")).atTime(JVaro.getInstance().getConfig().getInt("endLoginTime") - 1, 59).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).getOffset()).toEpochMilli());

    private final HashMap<UUID, Long> loginTimes = new HashMap<>();
    private final HashMap<UUID, BukkitTask> logOffBukkitTasks = new HashMap<>();
    private final HashMap<UUID, LogOffTask> logOffTasks = new HashMap<>();
    public List<UUID> damageDealt = new ArrayList<>();
    private boolean startDay = false;

    public TimesManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(JVaro.getInstance(), this::updateStartStop, 100, 100);

        Bukkit.getScheduler().runTaskTimerAsynchronously(JVaro.getInstance(), this::updateOnlineTime, 20, 20);
    }

    public boolean canJoin(Player player) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (!(timestamp.after(todayStart) && timestamp.before(todayEnd))) {
            return Bukkit.getOfflinePlayer(player.getUniqueId()).isOp();
        } else {
            JVaro.getInstance().getDiscordManager().sendLoginMessage(player);
            if (JVaro.getInstance().getDataConfig().getBoolean("started")) {
                ConfigurationSection section = JVaro.getInstance().getTimesConfig().getConfigurationSection("savedTimes");
                if (section != null && section.contains(player.getUniqueId().toString())) {
                    loginTimes.put(player.getUniqueId(), System.currentTimeMillis() - (JVaro.getInstance().getTimesConfig().getLong("savedTimes." + player.getUniqueId())));
                } else {
                    loginTimes.put(player.getUniqueId(), timestamp.getTime());
                }
            }
            return true;
        }
    }

    public void logout(Player player) {
        damageDealt.remove(player.getUniqueId());
        if (loginTimes.containsKey(player.getUniqueId())) {
            JVaro.getInstance().getTimesConfig().set("savedTimes." + player.getUniqueId(), System.currentTimeMillis() - loginTimes.get(player.getUniqueId()));
            try {
                JVaro.getInstance().getTimesConfig().save(JVaro.getInstance().getTimesFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            loginTimes.remove(player.getUniqueId());
        }
        if (logOffTasks.containsKey(player.getUniqueId())) {
            logOffBukkitTasks.get(player.getUniqueId()).cancel();
            logOffBukkitTasks.remove(player.getUniqueId());
            logOffTasks.remove(player.getUniqueId());
        }
    }

    public void resetTimes() {
        this.startDay = false;
        loginTimes.clear();
        JVaro.getInstance().getTimesConfig().set("savedTimes", null);
        try {
            JVaro.getInstance().getTimesConfig().save(JVaro.getInstance().getTimesFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        logOffBukkitTasks.forEach((uuid, bukkitTask) -> bukkitTask.cancel());
        logOffBukkitTasks.clear();
        logOffTasks.clear();
    }

    public void varoStart() {
        this.startDay = true;
        Bukkit.getOnlinePlayers().forEach(player -> loginTimes.put(player.getUniqueId(), System.currentTimeMillis()));
        JVaro.getInstance().getTimesConfig().set("savedTimes", null);
        try {
            JVaro.getInstance().getTimesConfig().save(JVaro.getInstance().getTimesFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStartStop() {
        todayStart = new Timestamp(LocalDate.now(ZoneId.of("Europe/Berlin")).atTime(JVaro.getInstance().getConfig().getInt("startLoginTime"), 0).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).getOffset()).toEpochMilli());
        todayEnd = new Timestamp(LocalDate.now(ZoneId.of("Europe/Berlin")).atTime(JVaro.getInstance().getConfig().getInt("endLoginTime") - 1, 59).toInstant(OffsetDateTime.now(ZoneId.of("Europe/Berlin")).getOffset()).toEpochMilli());
    }

    private void updateOnlineTime() {
        int checkTime = (JVaro.getInstance().getConfig().getInt("playTime") * 60 * 1000 * (startDay ? 2 : 1)) - 30_000;
        Bukkit.getOnlinePlayers().forEach(player -> {
                    Long time = loginTimes.get(player.getUniqueId());
                    if (time != null && !logOffBukkitTasks.containsKey(player.getUniqueId())) {
                        if ((time + checkTime) <= System.currentTimeMillis()) {
                            logOffTasks.put(player.getUniqueId(), new LogOffTask(player, aBoolean -> {
                                logOffTasks.remove(player.getUniqueId());
                                if (logOffBukkitTasks.get(player.getUniqueId()) != null)
                                    logOffBukkitTasks.get(player.getUniqueId()).cancel();
                                logOffBukkitTasks.remove(player.getUniqueId());
                                loginTimes.remove(player.getUniqueId());
                            }, 30));
                            logOffBukkitTasks.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(JVaro.getInstance(), logOffTasks.get(player.getUniqueId()), 20, 20));
                        }
                    }
                }
        );
    }
}
