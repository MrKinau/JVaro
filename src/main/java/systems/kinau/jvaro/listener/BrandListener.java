package systems.kinau.jvaro.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import systems.kinau.jvaro.JVaro;

import java.nio.charset.StandardCharsets;

public class BrandListener implements Listener, PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        JVaro.getInstance().getLogger().info(player.getName() + " joined using " + new String(message, StandardCharsets.UTF_8));
    }

    @EventHandler
    public void onChannelDebug(PlayerRegisterChannelEvent e) {
        JVaro.getInstance().getLogger().info(e.getPlayer().getName() + " registered channel " + e.getChannel());
    }

}
