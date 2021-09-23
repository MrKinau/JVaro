package systems.kinau.jvaro.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class LabymodKiller implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        player.kickPlayer("§cBrudi (oder Schwesti), was hatten wir zu Beginn gesagt?\n\n§4§lLabyMod ist nicht erlaubt");
    }
}
