package systems.kinau.jvaro;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import systems.kinau.jvaro.commands.LeakLocationCommand;
import systems.kinau.jvaro.commands.LocateCommand;
import systems.kinau.jvaro.commands.SimpleTeamCommand;
import systems.kinau.jvaro.commands.StartCommand;
import systems.kinau.jvaro.listener.BrandListener;
import systems.kinau.jvaro.listener.LabymodKiller;
import systems.kinau.jvaro.listener.PlayerListener;
import systems.kinau.jvaro.manager.DiscordManager;
import systems.kinau.jvaro.manager.LocationLeakManager;
import systems.kinau.jvaro.manager.TimesManager;
import systems.kinau.jvaro.manager.VaroManager;

import java.io.File;
import java.io.IOException;

@Getter
public final class JVaro extends JavaPlugin {

    @Getter
    private static JVaro instance;

    private File dataFile, configFile, timesFile;
    private FileConfiguration dataConfig, config, timesConfig;
    @Setter
    private BukkitTask startTask;

    private VaroManager varoManager;
    private DiscordManager discordManager;
    private TimesManager timesManager;
    private LocationLeakManager locationLeakManager;

    @Override
    public void onEnable() {
        instance = this;
        initConfig();
        registerListener();
        registerCommands();

        varoManager = new VaroManager();
        discordManager = new DiscordManager();
        timesManager = new TimesManager();
        locationLeakManager = new LocationLeakManager();
    }

    private void registerListener() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        BrandListener brandListener = new BrandListener();
        Bukkit.getServer().getPluginManager().registerEvents(brandListener, this);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "minecraft:brand", brandListener);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "labymod3:main", new LabymodKiller());
    }

    private void registerCommands() {
        PluginCommand startCommand = getCommand("start");
        if (startCommand != null)
            startCommand.setExecutor(new StartCommand());

        PluginCommand leakCommand = getCommand("leaklocation");
        LeakLocationCommand leakLocationCommand = new LeakLocationCommand();
        if (leakCommand != null) {
            leakCommand.setExecutor(leakLocationCommand);
            leakCommand.setTabCompleter(leakLocationCommand);
        }

        PluginCommand simpleTeamCommand = getCommand("simpleteam");
        if (simpleTeamCommand != null) {
            simpleTeamCommand.setExecutor(new SimpleTeamCommand());
        }

        PluginCommand locateCommand = getCommand("locate");
        if (locateCommand != null) {
            LocateCommand cmd = new LocateCommand();
            locateCommand.setExecutor(cmd);
            locateCommand.setTabCompleter(leakLocationCommand);
        }
    }

    private void initConfig() {
        dataFile = new File(getDataFolder(), "varoData.yml");
        configFile = new File(getDataFolder(), "varoConfig.yml");
        timesFile = new File(getDataFolder(), "timesData.yml");

        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            saveResource("varoData.yml", false);
        }
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("varoConfig.yml", false);
        }
        if (!timesFile.exists()) {
            timesFile.getParentFile().mkdirs();
            saveResource("timesData.yml", false);
        }

        dataConfig = new YamlConfiguration();
        try {
            dataConfig.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        timesConfig = new YamlConfiguration();
        try {
            timesConfig.load(timesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
