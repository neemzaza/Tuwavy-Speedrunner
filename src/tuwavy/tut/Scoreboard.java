package tuwavy.tut;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

public class Scoreboard {

    static Main plugin;

    public Scoreboard(Main instance) {
        plugin = instance;
    }

    // Scoreboard
    // First board
    public void onJoinBoard (Player player) {
        int playerHealth = (int) player.getHealth();
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("nsr-board", "dummy", ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("scoreboard.title-on-game-not-started")));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score space = obj.getScore(" ");
        space.setScore(3);
        Score score6 = obj.getScore(ChatColor.translateAlternateColorCodes('&',
                "&fStatus: " + (!plugin.gameReady ? (plugin.started ? "&bON PROGRESS" : "&eREGENERATING MAP...") : "&aREADY")));
        score6.setScore(2);
        Score score7 = obj.getScore("");
        score7.setScore(1);

        Score score8 = obj.getScore(ChatColor.translateAlternateColorCodes('&',
                "&7&oTuWavy | " + Main.version));
        score8.setScore(0);

        player.setScoreboard(board);
    }

    public String timeBarCount(double currentTime) {
        double constTime = Integer.parseInt(plugin.getConfig().getString("time-stopper.while-time-was-stopped"));

        double calTime10 = 10 * (constTime / 100);
        double calTime20 = 20 * (constTime / 100);
        double calTime30 = 30 * (constTime / 100);
        double calTime40 = 40 * (constTime / 100);
        double calTime50 = 50 * (constTime / 100);
        double calTime60 = 60 * (constTime / 100);
        double calTime70 = 70 * (constTime / 100);
        double calTime80 = 80 * (constTime / 100);
        double calTime90 = 90 * (constTime / 100);
        double calTime100 = 100 * (constTime / 100);

        if (currentTime <= calTime100 && currentTime >= calTime90)
            return ChatColor.translateAlternateColorCodes('&', "&b██████████");

        if (currentTime <= calTime90 && currentTime >= calTime80)
            return ChatColor.translateAlternateColorCodes('&', "&b█████████&7█");

        if (currentTime <= calTime80 && currentTime >= calTime70)
            return ChatColor.translateAlternateColorCodes('&', "&b████████&7██");

        if (currentTime <= calTime70 && currentTime >= calTime60)
            return ChatColor.translateAlternateColorCodes('&', "&b███████&7███");

        if (currentTime <= calTime60 && currentTime >= calTime50)
            return ChatColor.translateAlternateColorCodes('&', "&b██████&7████");

        if (currentTime <= calTime50 && currentTime >= calTime40)
            return ChatColor.translateAlternateColorCodes('&', "&b█████&7█████");

        if (currentTime <= calTime40 && currentTime >= calTime30)
            return ChatColor.translateAlternateColorCodes('&', "&b████&7██████");

        if (currentTime <= calTime30 && currentTime >= calTime20)
            return ChatColor.translateAlternateColorCodes('&', "&b███&7███████");

        if (currentTime <= calTime20 && currentTime >= calTime10)
            return ChatColor.translateAlternateColorCodes('&', "&b██&7████████");

        if (currentTime <= calTime10)
            return ChatColor.translateAlternateColorCodes('&', "&b█&7█████████");

        return ChatColor.translateAlternateColorCodes('&', "&cAn error ourruced");
    }

    // On during game board
    public void onGameBoard (Player player) {
        Player playerRunner = Bukkit.getServer().getPlayer(plugin.runnerTeam.get(plugin.runnerNoEachHunter.get(player.getUniqueId())));
        Location locRunner = playerRunner.getLocation();
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("nsr-board", "dummy", ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("scoreboard.title-on-game-started")));

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score space1 = obj.getScore(" ");
        space1.setScore(10);
        Score score = obj.getScore(ChatColor.DARK_AQUA + "Total Players: " + ChatColor.AQUA + plugin.allPlayers.size());
        score.setScore(9);
        Score score2 = obj.getScore(ChatColor. translateAlternateColorCodes('&', "&b◆ &1[&c&lTEAMS&1]"));
        score2.setScore(8);
        Score score3 = obj.getScore(ChatColor.DARK_GREEN + "⚔" + ChatColor.GREEN + " RUNNER TEAM: " + ChatColor.YELLOW + (plugin.runnerTeam.size() - plugin.runnerDiedCount) + (plugin.runnerTeam.contains(player.getUniqueId()) ? ChatColor.DARK_GREEN + " YOU" : ""));
        score3.setScore(7);
        Score score4 = obj.getScore(ChatColor.GOLD + "\uD83C\uDFF9" + ChatColor.GOLD + " HUNTER TEAM: " + ChatColor.YELLOW + plugin.hunterTeam.size() + (plugin.hunterTeam.contains(player.getUniqueId()) ? ChatColor.GOLD + " YOU" : ""));
        score4.setScore(6);
        Score space = obj.getScore(" ");
        space.setScore(5);
        if (plugin.hunterTeam.contains(player.getUniqueId())) {
            Score score5 = obj.getScore(ChatColor.DARK_PURPLE + "⚔" + ChatColor.LIGHT_PURPLE + " Runner Position (" + Bukkit.getPlayer(plugin.runnerTeam.get(plugin.runnerNoEachHunter.get(player.getUniqueId()))).getName() + ") : ");
            score5.setScore(4);
            Score score6 = obj.getScore(player.getWorld() == Bukkit.getPlayer(plugin.runnerTeam.get(plugin.runnerNoEachHunter.get(player.getUniqueId()))).getWorld() || !plugin.timeStopped ? ChatColor.WHITE + "X:" + locRunner.getBlockX() + " Y:" + locRunner.getBlockY() + " Z:" + locRunner.getBlockZ()
                    : ChatColor.WHITE + "X: " + "?" + " Y: " + "?" + " Z: " + "?");
            score6.setScore(3);
        }
        if (plugin.hasGm1) {
            Score score7 = obj.getScore(plugin.timeStopped ? (plugin.runnerTeam.contains(player.getUniqueId()) ? ChatColor.AQUA + "► Time will return in : " : ChatColor.AQUA + "Time status : " + ChatColor.GRAY + "|| PAUSE") : ChatColor.AQUA + "Creative mode in : " + ChatColor.WHITE + (plugin.cooldowns.get(player.getName())) + ChatColor.AQUA + "s");
            score7.setScore(2);

            if (plugin.timeStopped) {
                Score scoreTimeLeft = obj.getScore(timeBarCount(plugin.cooldownAfterStoppedTime.get(player)));
                scoreTimeLeft.setScore(1);
            }
        } else {
            Score score7 = obj.getScore((plugin.timeStopped ? (plugin.runnerTeam.contains(player.getUniqueId()) ? ChatColor.AQUA + "► Time will return in : " : ChatColor.AQUA + "Time status : " +  ChatColor.GRAY + "|| PAUSE") : ChatColor.AQUA + "Time : " + ChatColor.WHITE + plugin.gameTime + ChatColor.AQUA + "s"));
            score7.setScore(2);

            if (plugin.timeStopped) {
                Score scoreTimeLeft = obj.getScore(timeBarCount(plugin.cooldownAfterStoppedTime.get(player)));
                scoreTimeLeft.setScore(1);
            }
        }
        Score score8 = obj.getScore(ChatColor.translateAlternateColorCodes('&',
                "&7&oTuWavy | " + Main.version));
        score8.setScore(0);

        player.setScoreboard(board);
    }

    // On config game board (o selecting team)
    public void onConfigBoard (Player player) {

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("nsr-board", "dummy", ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("scoreboard.title-on-vote")));

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score space1 = obj.getScore(" ");
        space1.setScore(10);
        Score score = obj.getScore(ChatColor.WHITE + "");
        score.setScore(9);
        Score score2 = obj.getScore(ChatColor. translateAlternateColorCodes('&', "&b◆ &1[&c&lTEAMS&1]"));
        score2.setScore(8);
        Score score3 = obj.getScore(ChatColor.DARK_GREEN + "⚔" + ChatColor.GREEN + " RUNNER TEAM: " + ChatColor.YELLOW + (plugin.runnerTeam.size() - plugin.runnerDiedCount) + (plugin.runnerTeam.contains(player.getUniqueId()) ? ChatColor.DARK_GREEN + " YOU" : ""));
        score3.setScore(7);
        Score score4 = obj.getScore(ChatColor.GOLD + "\uD83C\uDFF9" + ChatColor.YELLOW +" HUNTER TEAM: " + ChatColor.YELLOW + plugin.hunterTeam.size() + (plugin.hunterTeam.contains(player.getUniqueId()) ? ChatColor.GOLD + " YOU" : ""));
        score4.setScore(6);
        Score space = obj.getScore(" ");
        space.setScore(5);
        Score score5 = obj.getScore(ChatColor.YELLOW + "⚔ glowing?: " + (plugin.hasGlowing ? ChatColor.GREEN + "✔" : ChatColor.RED + "✖"));
        score5.setScore(4);
        Score score8 = obj.getScore(ChatColor.DARK_AQUA + "⚔ can stop time?: " + (plugin.hasTimeStopper ? ChatColor.GREEN + "✔" : ChatColor.RED + "✖"));
        score8.setScore(4);
        Score score6 = obj.getScore(ChatColor.LIGHT_PURPLE + "Gamemode creative?: " + (plugin.hasGm1 ? ChatColor.GREEN + "✔" : ChatColor.RED + "✖"));
        score6.setScore(2);
        Score score7 = obj.getScore(ChatColor.translateAlternateColorCodes('&',
                "&7&oTuWavy | " + Main.version));
        score7.setScore(1);

        player.setScoreboard(board);
    }
}
