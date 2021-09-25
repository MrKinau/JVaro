package systems.kinau.jvaro.manager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import systems.kinau.jvaro.JVaro;

import java.util.List;
import java.util.stream.Collectors;

public class DiscordManager {

    private final String discordName;

    public DiscordManager() {
        discordName = JVaro.getInstance().getConfig().getString("discordName");
    }

    public void sendStartMessage() {
        List<WebhookClient> webHooks = JVaro.getInstance().getConfig().getStringList("startWebhooks").stream().map(WebhookClient::withUrl).collect(Collectors.toList());
        webHooks.forEach(webhook ->
                webhook.send(new WebhookMessageBuilder()
                        .addEmbeds(new WebhookEmbedBuilder()
                                .setColor(0x00d4ff)
                                .setTitle(new WebhookEmbed.EmbedTitle("Varo gestartet", null))
                                .setDescription("\nDas Spiel hat begonnen!")
                                .setThumbnailUrl("https://cdn.discordapp.com/icons/606506021221040148/578e19ba3eb7f0ed0c42990cf5f29ec3.webp?size=512")
                                .build())
                        .setUsername(discordName)
                        .build())
        );
    }

    public void sendLoginMessage(Player player) {
        List<WebhookClient> webHooks = JVaro.getInstance().getConfig().getStringList("loginWebhooks").stream().map(WebhookClient::withUrl).collect(Collectors.toList());
        webHooks.forEach(webhook ->
                webhook.send(new WebhookMessageBuilder()
                        .addEmbeds(new WebhookEmbedBuilder()
                                .setColor(0x3de018)
                                .setTitle(new WebhookEmbed.EmbedTitle(player.getName() + " beigetreten", null))
                                .setDescription("\n" + player.getName() + " hat den Server betreten!")
                                .setThumbnailUrl("https://cravatar.eu/helmhead/" + player.getUniqueId().toString().replace("-", "").toLowerCase() + "/256.png")
                                .build())
                        .setUsername(discordName)
                        .build())
        );
    }

    public void sendLogoutMessage(Player player) {
        List<WebhookClient> webHooks = JVaro.getInstance().getConfig().getStringList("logoutWebhooks").stream().map(WebhookClient::withUrl).collect(Collectors.toList());
        webHooks.forEach(webhook ->
                webhook.send(new WebhookMessageBuilder()
                        .addEmbeds(new WebhookEmbedBuilder()
                                .setColor(0xed3859)
                                .setTitle(new WebhookEmbed.EmbedTitle(player.getName() + " verlassen", null))
                                .setDescription("\n" + player.getName() + " hat den Server verlassen!")
                                .setThumbnailUrl("https://cravatar.eu/helmhead/" + player.getUniqueId().toString().replace("-", "").toLowerCase() + "/256.png")
                                .build())
                        .setUsername(discordName)
                        .build())
        );
    }

    public void sendShouldLogoutMessage(Player player) {
        List<WebhookClient> webHooks = JVaro.getInstance().getConfig().getStringList("shouldLogoutWebhooks").stream().map(WebhookClient::withUrl).collect(Collectors.toList());
        webHooks.forEach(webhook ->
                webhook.send(new WebhookMessageBuilder()
                        .addEmbeds(new WebhookEmbedBuilder()
                                .setColor(0x164ce0)
                                .setTitle(new WebhookEmbed.EmbedTitle(addGenetiveS(player.getName()) + " Zeit ist vorbei", null))
                                .setDescription("\n" + player.getName() + " sollte demnächst den Server verlassen!")
                                .setThumbnailUrl("https://cravatar.eu/helmhead/" + player.getUniqueId().toString().replace("-", "").toLowerCase() + "/256.png")
                                .build())
                        .setUsername(discordName)
                        .build())
        );
    }

    public void sendWorldBorderChange(String worldName, int size) {
        List<WebhookClient> webHooks = JVaro.getInstance().getConfig().getStringList("worldborderWebhooks").stream().map(WebhookClient::withUrl).collect(Collectors.toList());
        webHooks.forEach(webhook ->
                webhook.send(new WebhookMessageBuilder()
                        .addEmbeds(new WebhookEmbedBuilder()
                                .setColor(0xf99922)
                                .setTitle(new WebhookEmbed.EmbedTitle("Worldborder angepasst", null))
                                .setDescription("\nDie WorldBorder in \"" + worldName + "\" wurde auf " + size + "x" + size + " Blöcke angepasst!\n\nEcken bei: -" + (size / 2) + "/-" + (size / 2) + " und " + (size / 2) + "/" + (size / 2))
                                .setThumbnailUrl("https://cdn.discordapp.com/icons/606506021221040148/578e19ba3eb7f0ed0c42990cf5f29ec3.webp?size=512")
                                .build())
                        .setUsername(discordName)
                        .build())
        );
    }

    public void sendDeathMessage(String deathMessage) {
        List<WebhookClient> webHooks = JVaro.getInstance().getConfig().getStringList("deathsWebhooks").stream().map(WebhookClient::withUrl).collect(Collectors.toList());
        webHooks.forEach(webhook ->
                webhook.send(new WebhookMessageBuilder()
                        .addEmbeds(new WebhookEmbedBuilder()
                                .setColor(0xff0000)
                                .setTitle(new WebhookEmbed.EmbedTitle("TOOOD! Get rekt!", null))
                                .setDescription("\n" + deathMessage + "\n")
                                .setThumbnailUrl("https://cdn.pixabay.com/photo/2013/07/13/12/32/tombstone-159792_960_720.png")
                                .build())
                        .setUsername(discordName)
                        .build())
        );
    }

    public void sendLocationLeakMessage(OfflinePlayer player, String world, int x, int z) {
        List<WebhookClient> webHooks = JVaro.getInstance().getConfig().getStringList("locationLeakWebhooks").stream().map(WebhookClient::withUrl).collect(Collectors.toList());
        webHooks.forEach(webhook ->
                webhook.send(new WebhookMessageBuilder()
                        .addEmbeds(new WebhookEmbedBuilder()
                                .setColor(0xff0000)
                                .setTitle(new WebhookEmbed.EmbedTitle("Punishment: Koordinaten-Leak", null))
                                .setDescription("\n" + player.getName() + " ist in \"" + world + "\" bei\n\nX=" + x + "\nZ=" + z + "\n")
                                .setThumbnailUrl("https://cravatar.eu/helmhead/" + player.getUniqueId().toString().replace("-", "").toLowerCase() + "/256.png")
                                .build())
                        .setUsername(discordName)
                        .build())
        );
    }

    public void sendDamageDealt(Player damager, Player damaged) {
        List<WebhookClient> webHooks = JVaro.getInstance().getConfig().getStringList("damageDealtWebhooks").stream().map(WebhookClient::withUrl).collect(Collectors.toList());
        webHooks.forEach(webhook ->
                webhook.send(new WebhookMessageBuilder()
                        .addEmbeds(new WebhookEmbedBuilder()
                                .setColor(0xff7200)
                                .setTitle(new WebhookEmbed.EmbedTitle("Damageinfo", null))
                                .setDescription("\n" + damager.getName() + " hat " + damaged.getName() + " angegriffen!\n")
                                .setThumbnailUrl("https://cravatar.eu/helmhead/" + damager.getUniqueId().toString().replace("-", "").toLowerCase() + "/256.png")
                                .build())
                        .setUsername(discordName)
                        .build())
        );
    }

    private String addGenetiveS(String name) {
        if (name.endsWith("s") || name.endsWith("z") || name.endsWith("x") || Character.isDigit(name.charAt(name.length() - 1)))
            return name + "'";
        else
            return name + "s";
    }
}
