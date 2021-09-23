package systems.kinau.jvaro;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import systems.kinau.jvaro.commands.LeakLocationCommand;
import systems.kinau.jvaro.commands.LocateCommand;
import systems.kinau.jvaro.commands.SimpleTeamCommand;
import systems.kinau.jvaro.commands.StartCommand;
import systems.kinau.jvaro.listener.LabymodKiller;
import systems.kinau.jvaro.listener.PlayerListener;
import systems.kinau.jvaro.manager.DiscordManager;
import systems.kinau.jvaro.manager.TimesManager;
import systems.kinau.jvaro.manager.VaroManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

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

    @Override
    public void onEnable() {
        instance = this;
        initConfig();
        registerListener();
        registerCommands();

        varoManager = new VaroManager();
        discordManager = new DiscordManager();
        timesManager = new TimesManager();
    }

    private void registerListener() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
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

        registerLocateCommand(leakLocationCommand);

        PluginCommand locateCommand = getCommand("locate");
        if (locateCommand != null) {
            LocateCommand cmd = new LocateCommand();
            locateCommand.setExecutor(cmd);
            locateCommand.setTabCompleter(leakLocationCommand);
        }
    }

    private void registerLocateCommand(TabCompleter tabCompleter) {
        if (getServer().getPluginManager() instanceof final SimplePluginManager manager) {
            LocateCommand locateCommand = new LocateCommand();
            try {
                final Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                CommandMap map = (CommandMap) field.get(manager);
                final Field field2 = SimpleCommandMap.class.getDeclaredField("knownCommands");
                field2.setAccessible(true);
                @SuppressWarnings("unchecked")
                final Map<String, Command> knownCommands = (Map<String, Command>) field2.get(map);
                for (final Map.Entry<String, Command> entry : knownCommands.entrySet()) {
                    if (entry.getKey().equals("locate")) {
                        PluginCommand pluginCommand = getCommand("locate");
                        if (pluginCommand != null) {
                            pluginCommand.setExecutor(locateCommand);
                            pluginCommand.setTabCompleter(tabCompleter);
                            entry.setValue(pluginCommand);
                        }
                    }
                }
            } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
                e.printStackTrace();
            }
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
