package systems.kinau.jvaro.listener;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import systems.kinau.jvaro.JVaro;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PlayerListener implements Listener {

    private String kickReason;

    public PlayerListener() {
        int startLoginTime = JVaro.getInstance().getConfig().getInt("startLoginTime");
        int endLoginTime = JVaro.getInstance().getConfig().getInt("endLoginTime");
        kickReason = "§cDer Server ist nur zwischen " + startLoginTime + " und " + endLoginTime + " Uhr offen\n\nIst halt einfach dumm, wenn man es zu anderen Zeiten versucht\n\n§eFun fact: Jeder fünfte Österreicher hält sich für nicht kompetent!";
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (e.getResult() == PlayerLoginEvent.Result.ALLOWED && !JVaro.getInstance().getTimesManager().canJoin(e.getPlayer()))
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickReason);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (JVaro.getInstance().getDataConfig().getBoolean("started")) return;

        Player player = e.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        player.sendMessage("§c§lHALT! §bDas Varo hat noch nicht begonnen. Du bist nun im Adventuremode");
        String[] spawnData = Objects.requireNonNull(JVaro.getInstance().getConfig().getString("spawnPoint")).split("/");
        Location spawnPoint = new Location(Bukkit.getWorld(spawnData[0]), Double.parseDouble(spawnData[1]), Double.parseDouble(spawnData[2]), Double.parseDouble(spawnData[3]));
        player.teleport(spawnPoint);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        JVaro.getInstance().getDiscordManager().sendLogoutMessage(player);
        JVaro.getInstance().getTimesManager().logout(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!JVaro.getInstance().getDataConfig().getBoolean("started")) return;
        if (e.getDeathMessage() != null)
            JVaro.getInstance().getDiscordManager().sendDeathMessage(e.getDeathMessage());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!JVaro.getInstance().getDataConfig().getBoolean("started")) return;
        Bukkit.getScheduler().runTaskLater(JVaro.getInstance(), () ->  {
            Bukkit.getBanList(BanList.Type.NAME).addBan(e.getPlayer().getName(), "§cDu bist ausgeschieden, weil du zu schlecht warst!", null, null);
            e.getPlayer().kickPlayer("§cJa lol ey, weg vom Fenster. Schade aber auch!\n\n§e" + getRandomWord());
        }, 5L);
    }

    private String getRandomWord() {
        List<String> words = Arrays.asList("sheesh", "wyld", "Diggah", "sus", "Cringe", "akkurat", "same", "papatastisch", "Geringverdiener", "Mittwoch");
        Collections.shuffle(words);
        return words.get(0);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && !JVaro.getInstance().getDataConfig().getBoolean("started") && e.getCause() != EntityDamageEvent.DamageCause.VOID)
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntityPreStart(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && !JVaro.getInstance().getDataConfig().getBoolean("started") && !e.getDamager().isOp())
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntityPostStart(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player && JVaro.getInstance().getDataConfig().getBoolean("started")) {
            System.out.println(e.getDamager().getName() + " damaged " + e.getEntity().getName() + " with " + e.getDamage());
            if (!JVaro.getInstance().getTimesManager().damageDealt.contains(e.getDamager().getUniqueId())) {
                JVaro.getInstance().getTimesManager().damageDealt.add(e.getDamager().getUniqueId());
                JVaro.getInstance().getDiscordManager().sendDamageDealt((Player) e.getDamager(), (Player) e.getEntity());
            }
        } else if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player shooter && JVaro.getInstance().getDataConfig().getBoolean("started")) {
            System.out.println(e.getDamager().getClass().getSimpleName() + " from " + shooter.getName() + " damaged " + e.getEntity().getName() + " with " + e.getDamage());
            if (!JVaro.getInstance().getTimesManager().damageDealt.contains(shooter.getUniqueId())) {
                JVaro.getInstance().getTimesManager().damageDealt.add(shooter.getUniqueId());
                JVaro.getInstance().getDiscordManager().sendDamageDealt(shooter, (Player) e.getEntity());
            }
        }
    }
}
