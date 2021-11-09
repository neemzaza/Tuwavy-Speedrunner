package tuwavy.tut;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Bar {
    // Boss bar
    private int taskID = -1;
    private final Main plugin;
    private BossBar bar;

    public Bar(Main plugin) {
        this.plugin = plugin;
    }

    private String runnerPlayerName(int i) {
        return Bukkit.getPlayer(plugin.runnerTeam.get(i)).getName();
    }

    public void addPlayer(Player player) {
        bar.addPlayer(player);
    }

    public void removePlayer(Player player) {
        bar.removePlayer(player);
    }

    public BossBar getBar() {
        return bar;
    }

    public void createBar(int who) {
        bar = Bukkit.createBossBar(ChatColor.GREEN + runnerPlayerName(who) + "'s Health", BarColor.GREEN, BarStyle.SEGMENTED_10);
    }

    public void setProgressBar(Double healthLevel, int who) {
        bar.setProgress(0.05 * healthLevel);

        if (healthLevel <= 6) {
            bar.setColor(BarColor.RED);
            bar.setTitle(ChatColor.RED + runnerPlayerName(who) + "'s Health");
        }
        else {
            bar.setColor(BarColor.GREEN);
            bar.setTitle(ChatColor.GREEN + runnerPlayerName(who) + "'s Health");
        }

    }

    public void setWorldRegenProgressBar(Integer done) {
        bar.setColor(BarColor.BLUE);
        bar.setStyle(BarStyle.SOLID);
        bar.setProgress(0.33 * done);

        switch (done) {
            case 0:
                bar.setTitle(ChatColor.BLUE + "Regenerating the new world...");
            case 1:
                bar.setTitle(ChatColor.BLUE + "" + done + " / 3 World Regenerated");
            case 2:
                bar.setTitle(ChatColor.BLUE + "" + done + " / 3 World Regenerated");
            case 3:
                bar.setTitle(ChatColor.BLUE + "" + done + " / 3 World Regenerated");
        }

    }
}
