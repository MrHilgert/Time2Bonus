package ru.hilgert.t2b;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin implements Listener {

	public static SQLUtil sql;

	public static FileConfiguration config;

	private static HashMap<String, BonusTimer> players = new HashMap<String, BonusTimer>();

	public static File dataFolder;

	public void onEnable() {

		dataFolder = getDataFolder();

		config = getConfig();

		sql = new SQLUtil();

		initCfg();

		if (!connect()) {
			setEnabled(false);
			return;
		}

		initDB();

		Bukkit.getPluginManager().registerEvents(this, this);

	}

	public static boolean connect() {
		return sql.connect(config.getString("mysql.host", "localhost"),
				config.getString("mysql.user", "root"),
				config.getString("mysql.pass", ""),
				config.getInt("mysql.port", 3306),
				config.getString("mysql.dbname", "minecraft"));
	}

	public void onDisable() {
		players.clear();
		sql.close();
	}
	

	private void initDB() {
		sql.exec("CREATE TABLE IF NOT EXISTS `t2b` (`player` varchar(255) NOT NULL, `bonus` int(255) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
	}

	private void initCfg() {
		config.addDefault("giveBonus", 1);
		config.addDefault("time", 1L);

		config.addDefault("mysql.host", "localhost");
		config.addDefault("mysql.port", 3306);
		config.addDefault("mysql.dbname", "minecraft");

		config.addDefault("mysql.user", "root");
		config.addDefault("mysql.pass", "");

		config.addDefault("lang.afk",
				"&cВы не получили бонусы т.к находитесь АФК");
		config.addDefault("lang.bonusGived",
				"&6Вы получили: {BONUS} бонус(ов) за проведённое время на сервере");
		
		config.options().copyDefaults(true);
		saveConfig();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().hasPermission("time2bonus.timer")) {
			players.put(e.getPlayer().getName(), new BonusTimer(e.getPlayer()
					.getName()));
			players.get(e.getPlayer().getName()).afk.put(e.getPlayer(), e
					.getPlayer().getLocation());
			players.get(e.getPlayer().getName()).runTaskTimerAsynchronously(
					this, config.getLong("time") * 20,
					config.getLong("time") * 20);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		try {
			players.get(e.getPlayer().getName()).cancel();
			players.remove(e.getPlayer().getName());
		} catch (NullPointerException e2) {
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (cmd.getName().equalsIgnoreCase("t2b")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					configReload();
					sender.sendMessage(ChatColor.GREEN
							+ "Time2Bonus перезагружен");
					return true;
				}
			} else {
				return false;
			}
		}

		return false;
	}

	public FileConfiguration configReload() {
		return config = YamlConfiguration.loadConfiguration(new File(dataFolder,
				"config.yml"));
	}

	public static void configSave() {
		try {
			config.save(new File(dataFolder, "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String lang(String string) {
		return ChatColor.translateAlternateColorCodes('&',
				config.getString("lang." + string));
	}

}
