package ru.hilgert.t2b;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BonusTimer extends BukkitRunnable {

	public String player;
	public int bonusPerMin;

	public HashMap<Player, Location> afk = new HashMap<Player, Location>();

	public BonusTimer(String player) {
		this.player = player;
		this.bonusPerMin = MainClass.config.getInt("giveBonus");
	}

	public void run() {
		if (MainClass.sql.isConnected) {
			Player p = null;
			try {
				p = Bukkit.getPlayer(player);

				Location loc = p.getLocation();

				this.bonusPerMin = MainClass.config.getInt("giveBonus");
				if (afk.get(p).equals(loc)) {
					p.sendMessage(MainClass.lang("afk"));
				} else {
					MainClass.sql
							.exec("INSERT INTO `t2b`(`player`,`bonus`) VALUES('"
									+ player
									+ "', bonus +"
									+ bonusPerMin
									+ ") ON DUPLICATE KEY UPDATE `player`='"
									+ player
									+ "', `bonus`=bonus +"
									+ bonusPerMin);
					p.sendMessage(MainClass.lang("bonusGived").replace("{BONUS}", bonusPerMin+""));
				}

				afk.put(p, loc);

			} catch (NullPointerException e) {
				cancel();
			}
		} else {
			MainClass.connect();
		}
	}

}
