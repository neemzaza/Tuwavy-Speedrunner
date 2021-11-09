package tuwavy.tut;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {
    Main plugin;

    public Command(Main instance) {
        plugin = instance;
    }
    // เมื่อใช้คำสั่ง
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        // /speedrunner
        if (label.equalsIgnoreCase("speedrunner")
                || label.equalsIgnoreCase("manhunt")
                || label.equalsIgnoreCase("sr")
                || label.equalsIgnoreCase("mh")
                || label.equalsIgnoreCase("speedrun")) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "Usage: /speedrunner , /manhunt, /sr, /mh or /speedrun");
                player.sendMessage(ChatColor.AQUA + "/sr start (to start playing speedrunner)");
                player.sendMessage(ChatColor.AQUA + "/sr stop (to stop while speedruning)");
                player.sendMessage(ChatColor.AQUA + "/sr player (to check total player in each team and all player in game)");
                if (player.isOp()) {
                    player.sendMessage(ChatColor.GOLD + "/sr setlobby (move your body to location you want to set lobby location)");
                    player.sendMessage(ChatColor.GOLD + "/sr reload (reload config)");
                }
                return true;
            }

            // /speedrunner team
            if (args[0].equalsIgnoreCase("start")) {
                plugin.onJoinGame(player);

                return true;
            }

            if (args[0].equalsIgnoreCase("stop")) {
                plugin.onStopGame(player);

                return true;
            }

            if (args[0].equalsIgnoreCase("leave")) {
                plugin.onLeaveGame(player);

                return true;
            }

            if (args[0].equalsIgnoreCase("player")) {
                player.sendMessage(ChatColor.GREEN + "RUNNER: " + ChatColor.WHITE + (plugin.runnerTeam.size()));
                player.sendMessage(ChatColor.GOLD + "HUNTER: " + ChatColor.WHITE + plugin.hunterTeam.size());
                player.sendMessage(ChatColor.GOLD + "WAITLIST: " + ChatColor.WHITE + plugin.waitList.size());
                player.sendMessage("");
                player.sendMessage(ChatColor.YELLOW + "TOTAL (NOT WAITLIST): " + ChatColor.WHITE + (plugin.allPlayers.size()));

                return true;
            }


            // Op Cmd Section
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You can't use op command yet.");
                return true;
            }


            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
//                if (!plugin.getConfig().getString("more-settings.kick-on-world-regenerating").toLowerCase().equalsIgnoreCase("true") || !plugin.getConfig().getString("more-settings.kick-on-world-regenerating").toLowerCase().equalsIgnoreCase("false")) {
//                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
//                            "&eWarning: more-settings => kick-on-world-regenerating value in this section must be \"true\" or \"false\""));
//                }

                for (String msg : plugin.getConfig().getStringList("reload.msg")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            msg));
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("setlobby")) {
//                this.getConfig().set("position.lobby-world", player.getWorld());
                plugin.getConfig().set("position.lobby", player.getLocation());
                plugin.saveConfig();
                Location posSet = plugin.getConfig().getLocation("position.lobby");
                player.sendMessage(ChatColor.GREEN + "Set lobby spawn in world: " +
                        posSet.getWorld() + " X: " + posSet.getBlockX(), " Y: " + posSet.getBlockY() + "Z: " + posSet.getBlockZ());
//                player.sendMessage(ChatColor.GREEN + "Set world spawn at " + this.getConfig().getString("position.lobby-world"));
                return true;
            }

            if (args[0].equalsIgnoreCase("testconfig")) {
                plugin.getConfig().set("stats", player.getName() + ": ");
                plugin.getConfig().set("stats." + player.getName(), "win: ");
                plugin.getConfig().set("stats." + player.getName() + ".win", (plugin.getConfig().getString("stats." + player.getName() + ".win") == null ? 1 : plugin.getConfig().getString("stats." + player.getName() + ".win") + 1));
                player.sendMessage(ChatColor.GREEN + "This is result response: " + plugin.getConfig().getString("stats." + player.getName() + ".win"));
                return true;
            }

            if (args[0].equalsIgnoreCase("spawnplayer")) {
                plugin.spawnFakePlayer(player);
                player.sendMessage(ChatColor.GREEN + "Command was executed");
                return true;
            }
            if (args[0].equalsIgnoreCase("removeplayer")) {
                plugin.removeFakePlayer();
                player.sendMessage(ChatColor.GREEN + "Command was executed");
                return true;
            }
            else {
                player.sendMessage(ChatColor.RED + "\"" + args[0] + "\" not found");
                return true;
            }

        }
        return false;
    }
}
