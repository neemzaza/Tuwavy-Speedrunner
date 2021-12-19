package tuwavy.tut;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import tuwavy.tut.Files.DataManager;

import java.io.File;
import java.util.*;
import java.util.List;

public class Main extends JavaPlugin {

    // Custom config
    public DataManager data;

    // Version in main
    public static final String version = "PE 1.0 Release";
    public static final String prefix = "&b&l[Tuwavy] ";

    // Import Zone
    public Bar bar;
    public Scoreboard scoreboard;
    public GameGUI gameGUI;

    // Command label
    private static final String[] commandLabels = {"manhunt", "mh", "speedrunner", "sr", "speedrun"};

    // Task running (HashMap) (Schedule)
    public List<Integer> runningTasks = new ArrayList<Integer>();

    //ทีม Runner และ ทีม Hunter
    public List<UUID> runnerTeam = new ArrayList<>();
    public List<UUID> hunterTeam = new ArrayList<>();

    // Total player
    public List<UUID> allPlayers = new ArrayList<>(); //All Player

    // Wait list while not join (Leave game before) (waitlist for joining again)
    public Map<UUID, String> waitList = new HashMap<>();

    // Spectator list
    public List<UUID> spectatorList = new ArrayList<>();

    // Runner Order (Each hunter)
    Map<UUID, Integer> runnerNoEachHunter = new HashMap<UUID, Integer>();

    // spawnpoint of each speedrunning world
    public Location spawnWorldLocation;

    // runner died count (Null Solved)
    public int runnerDiedCount = 0;

    // User of sawarudo
    public Player clockStopperUser;

    // Time spend of that round
    public int gameTime = 0; //เวลาที่เล่นไปแล้ว

    // this plugin
    public JavaPlugin plugin = this; // ปลั๊กอินนี้แหละ

    // game has ready?
    public Boolean gameReady = true;

    // game has start?
    public Boolean started = false; // เช็คว่าเริ่มรึยัง

    // in preparing of that round (on selecting team)
    public Boolean onTeamSelect = false;

    // world has regenerating?
    public Boolean onRegenWorld = false;

    // world regenerated success count
    public Integer worldRegen = 0;

    // time was stopped?
    public Boolean timeStopped = false;

    // gamemode 0 cooldown
    Map<String, Integer> cooldowns = new HashMap<>(); // gm 0 cooldown

    // gamemode 1 cooldown
    Map<String, Integer> gmCooldowns = new HashMap<>(); //gm 1 cooldown

    // Deprecated
    Map<String, Long> cooldownsRemap = new HashMap<>(); //remap cooldown

    // on starting game cooldown
    Map<String, Integer> cooldownsStarting = new HashMap<>(); //game start cooldown

    // waiting stop time
    Map<Player, Integer> cooldownWaitStopTime = new HashMap<>();

    // period of during stopping time
    Map<Player, Integer> cooldownAfterStoppedTime = new HashMap<>();

    // That players are attacking that freezing player
    Map<UUID, Double> attackDamageWhenStoppedTime = new HashMap<>(); //attack time when time stopped

    // Runner has glowing?
    public boolean hasGlowing = false; //ให้มี Effect glowing ไหม (false)

    // All player has gamemode 1?
    public boolean hasGm1 = false; //ให้มี Effect gamemode 1 ไหม (false)

    // Runner has time stopper?
    public boolean hasTimeStopper = false;

    //Record Time?
    public boolean hasRecordTime = false;

    //Day = true, Night = false
    public boolean dayOrNight = true;

    // Fake Runner NPC
    private Entity fakeNpc;

    // red light toggle (Game Scoreboard)
    private boolean redLight = true;

    // Deprecated

//    // World Manager Section
//        // get core of multiverse (API)
//    MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
//
//    // get world manager of multiverse
//    MVWorldManager worldManager = core.getMVWorldManager();
//
//    // all multiverse world
//    Collection<MultiverseWorld> allMVWorld = worldManager.getMVWorlds();
//
//    // get netherportals API
//    MultiverseNetherPortals netherportals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");
//
//    // method that linking world
//    public void linkSMPWorlds(String overworld, String netherworld, String endworld) {
//        // Linking nether portals both ways
//        netherportals.addWorldLink(overworld, netherworld, PortalType.NETHER);
//        netherportals.addWorldLink(netherworld, overworld, PortalType.NETHER);
//
//        // Linking end portals both ways
//        netherportals.addWorldLink(overworld, endworld, PortalType.ENDER);
//        netherportals.addWorldLink(endworld, overworld, PortalType.ENDER);
//    }

    // on Initialize
    @Override
    public void onEnable() { //เมื่อถูก reload, เปิดเซิฟใหม่
        // Boss bar
        bar = new Bar(this);
        // Scoreboard
        scoreboard = new Scoreboard(this);
        // Game GUI
        gameGUI = new GameGUI(this);
        // Config.yml set
        this.saveDefaultConfig();
        // Registry Events (Event.java)
        this.getServer().getPluginManager().registerEvents(new Event(this), this);
        // Registry Commands (Command.java)
        for (String commandLabel : commandLabels) {
            this.getCommand(commandLabel).setExecutor(new Command(this));
            this.getCommand(commandLabel).setTabCompleter(new TabComplete());
        }

        // Registry Custom Config
        this.data = new DataManager(this);

        //regis สำหรับการเรียกใช้ Listener
        gameGUI.normalGUI();
        gameGUI.runnerGUI();
        gameGUI.configureGUI();
        gameGUI.timeStoppedGUI();
        gameGUI.selectTimeGUI();

//        try {
//            if (!allMVWorld.contains(getConfig().getString("world.default-world"))) {
//                getLogger().info(ChatColor.YELLOW + "Look like " + getConfig().getString("world.default-world") + " not found. We creating new world!");
//                if (worldManager.addWorld(getConfig().getString("world.default-world"), World.Environment.NORMAL, null, WorldType.NORMAL, true, null))
//                    getLogger().info(ChatColor.GREEN + "Created " + getConfig().getString("world.default-world") + " (1/3)");
//                if (worldManager.addWorld(getConfig().getString("world.nether-world").replace("!%world%!", getConfig().getString("world.default-world")), World.Environment.NETHER, null, WorldType.NORMAL, true, null))
//                    getLogger().info(ChatColor.GREEN + "Created " + getConfig().getString("world.nether-world").replace("!%world%!", getConfig().getString("world.default-world")) + " (2/3)");
//                if (worldManager.addWorld(getConfig().getString("world.the-end-world").replace("!%world%!", getConfig().getString("world.default-world")), World.Environment.THE_END, null, WorldType.NORMAL, true, null))
//                    getLogger().info(ChatColor.GREEN + "Created " + getConfig().getString("world.the-end-world").replace("!%world%!", getConfig().getString("world.default-world")) + " good luck!");
//            }
//        } catch(Exception e) {
//            getLogger().info(ChatColor.GREEN + "Look like " + getConfig().getString("world.default-world") + " is haven. No issues!");
//        }


//        linkSMPWorlds(getConfig().getString("world.default-world"),
//                getConfig().getString("world.nether-world").replace("!%world%!", getConfig().getString("world.default-world")),
//                getConfig().getString("world.the-end-world").replace("!%world%!", getConfig().getString("world.default-world"))
//        );

        createWorld(bar);
    }

    // Methods that creating world / regen world
    private void createWorld(Bar bar) {
        // Create World Variables
        WorldCreator wcNormal = new WorldCreator(getConfig().getString("world.default-world"));
        WorldCreator wcNether = new WorldCreator(getConfig().getString("world.nether-world").replace("!%world%!", getConfig().getString("world.default-world")));
        WorldCreator wcEnd = new WorldCreator(getConfig().getString("world.the-end-world").replace("!%world%!", getConfig().getString("world.default-world")));
        // Set environment
        wcNormal.environment(World.Environment.NORMAL);
        wcNether.environment(World.Environment.NETHER);
        wcEnd.environment(World.Environment.THE_END);

        // Set type of world
        wcNormal.type(WorldType.NORMAL);

        // Create world
        try {
            if (Bukkit.getServer().getWorld(getConfig().getString("world.default-world")) == null) {
                // Random seed
                long ramdomSeed = new Random().nextLong();
                // And set seed
                wcNormal.seed(ramdomSeed);
                // then creating world
                Bukkit.getServer().createWorld(wcNormal);
                // Broadcast into console
                getLogger().info(ChatColor.GREEN + "Success to create " + getConfig().getString("world.default-world"));
                // Broadcast into bar
                if (!Bukkit.getOnlinePlayers().isEmpty())
                    bar.setWorldRegenProgressBar(1);
            }

            if (Bukkit.getServer().getWorld(getConfig().getString("world.nether-world").replace("!%world%!", getConfig().getString("world.default-world"))) == null) {
                // Random seed
                long ramdomSeed = new Random().nextLong();
                // And set seed
                wcNether.seed(ramdomSeed);
                // then creating world
                Bukkit.getServer().createWorld(wcNether);
                // Broadcast into console
                getLogger().info(ChatColor.GREEN + "Success to create " + getConfig().getString("world.nether-world").replace("!%world%!", getConfig().getString("world.default-world")));
                // Broadcast into bar
                if (!Bukkit.getOnlinePlayers().isEmpty())
                    bar.setWorldRegenProgressBar(2);
            }

            if (Bukkit.getServer().getWorld(getConfig().getString("world.the-end-world").replace("!%world%!", getConfig().getString("world.default-world"))) == null) {
                // Random seed
                long ramdomSeed = new Random().nextLong();
                // And set seed
                wcEnd.seed(ramdomSeed);
                // then creating world
                Bukkit.getServer().createWorld(wcEnd);
                // Broadcast into console
                getLogger().info(ChatColor.GREEN + "Success to create " + getConfig().getString("world.the-end-world").replace("!%world%!", getConfig().getString("world.default-world")));
                getLogger().info(ChatColor.GREEN + "Processed (2/2) Done!");
                // Broadcast into bar
                if (!Bukkit.getOnlinePlayers().isEmpty())
                    bar.setWorldRegenProgressBar(3);
            }

        } catch (Exception e) {
            // Catch error
            getLogger().info(ChatColor.RED + "Error creating world! Try to remove world and run server yourself. Sorry for this issues. You can report that issue in https://github.com/neemzaza/Release-from-Tuwavy-Speedrunner/issues");
        }
    }

    // Regenerate world
    public void regenWorld(Bar bar) {
        // Unloading all worlds
        Bukkit.unloadWorld(Bukkit.getWorld(getConfig().getString("world.default-world")), false);
        Bukkit.unloadWorld(Bukkit.getWorld(getConfig().getString("world.nether-world").replace("!%world%!", getConfig().getString("world.default-world"))), false);
        Bukkit.unloadWorld(Bukkit.getWorld(getConfig().getString("world.the-end-world").replace("!%world%!", getConfig().getString("world.default-world"))), false);

        try {
            // Delete world directory
            FileUtils.deleteDirectory(new File(getConfig().getString("world.default-world")));
            FileUtils.deleteDirectory(new File(getConfig().getString("world.nether-world").replace("!%world%!", getConfig().getString("world.default-world"))));
            FileUtils.deleteDirectory(new File(getConfig().getString("world.the-end-world").replace("!%world%!", getConfig().getString("world.default-world"))));
            getLogger().info(ChatColor.GREEN + "Processed (1/2)");
        } catch (Exception e) {
            // Catch error
            getLogger().info(ChatColor.RED + "Error deleting world! Try to remove world and run server yourself. Sorry for this issues.");
        }
        // And create world again
        createWorld(bar);
    }

    // on disable this plugin (Reload or stop)
    @Override
    public void onDisable() { //เมื่อถูก reload, เปิดเซิฟใหม่ (แบบปิด)

    }

    // Count Hunter Player
    protected Integer getHunterPlayer() {
        return hunterTeam.size();
    }

    // Use when already case and error case (not exception)
    public void errorSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }


    // on select team method
    public void onJoinGame(Player player) {
        ItemStack selectItem = new ItemStack(Material.CHEST);
        ItemMeta meta = selectItem.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eTE&aAM &bSEL&6ECT&fOR"));
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.GRAY + "Click to select team");
        meta.setLore(lore);
        meta.setUnbreakable(true);
        selectItem.setItemMeta(meta);

        if (getConfig().getLocation("position.lobby") == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLobby location is not set yet. Please contact admin!"));
            return;
        }

        if (started) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cGame is already started."));
            return;
        }

        if (cooldownsRemap.size() > 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cMap is regenaring. Please wait until a green meesage appear yet."));
            return;
        }

        if (allPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou're already joined the game."));
            return;
        }

        allPlayers.add(player.getUniqueId());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aAuto selected 'Hunter team' to change team right click chest on ninth slot and select other teams."));
        onJoinHunterTeam(player);
        player.closeInventory();
        onTeamSelect = true;

        for (UUID allPlayer : allPlayers) {
            Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + player.getName() + " joined the game."));
        }

        player.getInventory().clear();
        scoreboard.onConfigBoard(player);
        player.getInventory().setItem(8, selectItem);
    }

    // Left from that round
    public void onLeaveGame(Player player) {
        onPlayerLeftGame(player);

        for (UUID allPlayer : allPlayers) {
            Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.AQUA + Bukkit.getPlayer(allPlayer).getName() + " left the game.");
        }

        player.getInventory().clear();
        scoreboard.onJoinBoard(player);
        allPlayers.remove(player.getUniqueId());
    }

    // Join to runner team
    public boolean onJoinRunnerTeam(Player player) {

        if (runnerTeam.contains(player.getUniqueId()) && runnerTeam.size() > 0) {
            player.sendMessage(ChatColor.RED + "You already joined 'Runner' team");
            errorSound(player);
            return true;
        }
//        if (runnerTeam.size() > 0) {
//            player.sendMessage(ChatColor.RED + "This team is full. Remember Runner team must only has 1 player.");
//            errorSound(player);
//            return true;
//        }
        // if passed above condition this team is null (Available)
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
        if (hunterTeam.contains(player.getUniqueId())) onPlayerLeftGame(player);

        runnerTeam.add(player.getUniqueId());
        player.openInventory(gameGUI.runnerGUIInv);
        for (UUID allPlayer : allPlayers) {
            scoreboard.onConfigBoard(Bukkit.getPlayer(allPlayer));
        }

        if (hunterTeam.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&fYou got leave &e'Hunter Team' &fby typing this command &b'/speedrunner leave' "));
            errorSound(player);
            return true;
        }

        bar.createBar(0);

        player.sendMessage(ChatColor.GREEN + "Joined " + ChatColor.DARK_GREEN + "Runner team!");
        player.sendMessage(ChatColor.WHITE + "Your team has " + ChatColor.AQUA + runnerTeam.size() + " players!");

        return true;
    }

    // Join to hunter team
    public boolean onJoinHunterTeam(Player player) {
        if (runnerTeam.contains(player.getUniqueId())) onPlayerLeftGame(player);
        if (hunterTeam.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You already joined 'Hunter' team");
            errorSound(player);
            return true;
        }

        if (runnerTeam.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&fYou got leave &e'Runner Team' &fby typing this command &b'/speedrunner leave' "));
            errorSound(player);
            return true;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
        hunterTeam.add(player.getUniqueId());
        runnerNoEachHunter.put(player.getUniqueId(), 0);
        player.openInventory(gameGUI.normalGUIInv);

        for (UUID allPlayer : allPlayers) {
            scoreboard.onConfigBoard(Bukkit.getPlayer(allPlayer));
        }

        player.sendMessage(ChatColor.GREEN + "Joined " + ChatColor.GOLD + "Hunter team!");
        player.sendMessage(ChatColor.WHITE + "Your team has " + ChatColor.AQUA + hunterTeam.size() + " players!");

        return true;
    }

    // Off on glowing (Toggle)
    public boolean onToggleGlowingFx(Player player) {
        if (!runnerTeam.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "เอ็งไม่ใช่ Runner นะจ๊ะ You can't start this game");
            errorSound(player);
            return true;
        }

        hasGlowing = !hasGlowing;
        for (UUID allPlayer : allPlayers) {
            scoreboard.onConfigBoard(Bukkit.getPlayer(allPlayer));
            if (hasGlowing) {
                player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0f, 1.0f);
                Bukkit.getPlayer(allPlayer).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "[Runner " + player.getName() + "] enabled \"Runner glowing\""));
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0f, 1.0f);
                Bukkit.getPlayer(allPlayer).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "[Runner " + player.getName() + "] disabled \"Runner glowing\""));
            }
        }

        return true;
    }

    public boolean onToggleRecord(Player player) {
        if (!runnerTeam.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "เอ็งไม่ใช่ Runner นะจ๊ะ You can't start this game");
            errorSound(player);
            return true;
        }

        hasRecordTime = !hasRecordTime;
        return true;
    }

    public boolean onChooseDay(Player player) {
        if (!runnerTeam.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "เอ็งไม่ใช่ Runner นะจ๊ะ You can't start this game");
            errorSound(player);
            return true;
        }

        dayOrNight = true;
        return true;
    }

    public boolean onChooseNight(Player player) {
        if (!runnerTeam.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "เอ็งไม่ใช่ Runner นะจ๊ะ You can't start this game");
            errorSound(player);
            return true;
        }

        dayOrNight = false;
        return true;
    }

    // Off on gamemode 1 (Toggle)
    public boolean onToggleGm1(Player player) {
        if (!runnerTeam.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "เอ็งไม่ใช่ Runner นะจ๊ะ You can't start this game");
            errorSound(player);
            return true;
        }

        hasGm1 = !hasGm1;
        for (UUID allPlayer : allPlayers) {
            if (hasGm1) {
                player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
                Bukkit.getPlayer(allPlayer).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "[Runner " + player.getName() + "] enabled \"gamemode creative\""));

            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0f, 1.0f);
                Bukkit.getPlayer(allPlayer).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "[Runner " + player.getName() + "] disabled \"Gamemode creative\""));

            }
            scoreboard.onConfigBoard(Bukkit.getPlayer(allPlayer));
        }
        return true;
    }

    // Off on Time stopper (Toggle)
    public boolean onToggleTimeStopper(Player player) {
        if (!runnerTeam.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "เอ็งไม่ใช่ Runner นะจ๊ะ You can't start this game");
            errorSound(player);
            return true;
        }

        hasTimeStopper = !hasTimeStopper;
        for (UUID allPlayer : allPlayers) {
            if (hasTimeStopper) {
                player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 1.0f);
                Bukkit.getPlayer(allPlayer).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "[Runner " + player.getName() + "] enabled \"Runner have time stopper\""));
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1.0f, 1.0f);
                Bukkit.getPlayer(allPlayer).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "[Runner " + player.getName() + "] disabled \"Runner have time stopper\""));
            }
            scoreboard.onConfigBoard(Bukkit.getPlayer(allPlayer));
        }
        return true;
    }

    // remove potion effect
    public void removePotion(Player player) {
        if (player == null) return;
        if (player.getActivePotionEffects().size() == 0) return;

        for (PotionEffect effect : player.getActivePotionEffects()) { //loop in active potion effect and remove them
            player.removePotionEffect(effect.getType());
        }
    }

    // clear game (preparing on next round) (on game end)
    private void clearGame() {
        started = false;
        gameReady = false;
        timeStopped = false;
        getServer().getScheduler().cancelTasks(plugin);
        Bukkit.broadcastMessage("total time: " + (gameTime / 60) + " minutes!");
        gameTime = 0;

        for (UUID uuid : allPlayers) {
            if (allPlayers.size() > 0) {
                removePotion(Bukkit.getPlayer(uuid));
                scoreboard.onJoinBoard(Bukkit.getPlayer(uuid));
                cooldowns.remove(Bukkit.getPlayer(uuid).getName());
                gmCooldowns.remove(Bukkit.getPlayer(uuid).getName());
            }
            Bukkit.getPlayer(uuid).getInventory().clear();
            Bukkit.getPlayer(uuid).setHealth(20);
            Bukkit.getPlayer(uuid).setFoodLevel(20);

            Bukkit.getPlayer(uuid).teleport(this.getConfig().getLocation("position.lobby"));


            Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 2F, 1F);
            bar.removePlayer(Bukkit.getPlayer(uuid));
            bar.addPlayer(Bukkit.getPlayer(uuid));
        }
        runnerTeam.clear();
        hunterTeam.clear();
        waitList.clear();
        spectatorList.clear();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/advancement revoke @a everything");

        allPlayers.clear();
        cooldownWaitStopTime.clear();
        cooldownAfterStoppedTime.clear();
        runnerDiedCount = 0;
        cooldownsRemap.put("Remap", System.currentTimeMillis() + (30 * 1000));
        Random r = new Random();
        Bukkit.broadcastMessage(ChatColor.YELLOW + "During regen map, Please waiting...");
        onRegenWorld = true;
//        if (this.getConfig().getString("more-settings.kick-on-world-regenerating").toLowerCase().equalsIgnoreCase("true")) {
//            for (Player players : Bukkit.getOnlinePlayers()) {
//                players.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&bRegenerating world &fplease waiting &e10 - 50 second &athen you can join again."));
//            }
//        }

        bar.setWorldRegenProgressBar(0);
        worldRegen = 0;                       // World Name                         // New Seed? //Random Seed? //Custom Seed
//        if (worldManager.regenWorld(getConfig().getString("world.default-world"), true, true, "")) {
//            bar.setWorldRegenProgressBar(1);
//            worldRegen = 1;
//
//            if (worldManager.regenWorld(getConfig().getString("world.nether-world")
//                    .replace("!%world%!", getConfig().getString("world.default-world")), true, true, "")) {
//
//                    bar.setWorldRegenProgressBar(2);
//                    worldRegen = 2;
//
//                if (worldManager.regenWorld(getConfig().getString("world.the-end-world")
//                    .replace("!%world%!", getConfig().getString("world.default-world")), true, true, "")) {
//
//                    bar.setWorldRegenProgressBar(3);
//                    worldRegen = 3;
//                    Bukkit.broadcastMessage(ChatColor.GREEN + "Map is complete. You can play next round!");
//
//                    for (Player players : getServer().getOnlinePlayers()) {
//                        players.setGameMode(GameMode.ADVENTURE);
//                        bar.removePlayer(players);
//                        scoreboard.onJoinBoard(players);
//                        players.sendMessage(this.getConfig().getString("more-settings.kick-on-world-regenerating").toLowerCase());
//                    }
//
//                    gameReady = true;
//                    onRegenWorld = false;
//                }
//            }
//        }

        regenWorld(bar);
        Bukkit.broadcastMessage(ChatColor.GREEN + "Map is complete. You can play next round!");
        for (Player players : getServer().getOnlinePlayers()) {
            players.setGameMode(GameMode.ADVENTURE);
            bar.removePlayer(players);
            scoreboard.onJoinBoard(players);
//                        players.sendMessage(this.getConfig().getString("more-settings.kick-on-world-regenerating").toLowerCase());
        }
        onRegenWorld = false;
        gameReady = true;

    }

    // on victory that round (Extends clearGame())
    public boolean onVictoryGame(Player player) {
        Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " won this speedrun in " + (gameTime / 60) + " minutes!!");
        getConfig().set("stats", player.getName() + ": ");
        getConfig().set("stats." + player.getName(), "win: ");
        getConfig().set("stats." + player.getName() + ".win", (getConfig().getString("stats." + player.getName() + ".win") == null ? 1 : getConfig().getString("stats." + player.getName() + ".win") + 1));

        clearGame();

        return true;
    }

    // on stopped game by player or hunter (Extends clearGame())
    public boolean onStopGame(Player player) {
        if (!started) {
            errorSound(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't stop game. Because game not started yet."));
            return true;
        }
        Bukkit.broadcastMessage(ChatColor.RED + "Speedrunner was stopped by " + player.getName());

        clearGame();

        return true;
    }

    // Left team method
    public Boolean onPlayerLeftGame(Player player) {
        if (runnerTeam.contains(player.getUniqueId())) {
            runnerTeam.remove(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "YOU LEFT! FROM THE RUNNER TEAM");

            return true;
        }

        if (hunterTeam.contains(player.getUniqueId())) {
            hunterTeam.remove(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "YOU LEAVE! FROM THE HUNTER TEAM");

            return true;
        }

        player.sendMessage(ChatColor.RED + "YOU NOT STAY IN SOMETEAM!");
        errorSound(player);
        return true;
    }


    // Toogle Time stop (Core of time stopper)
    public void onTimeStop(Player user) {
        for (UUID value : allPlayers) {
            if (user.getUniqueId() == value) continue;
            // If time was stopped
            if (timeStopped) {
                Bukkit.getPlayer(value).setGameMode(GameMode.ADVENTURE);
                Bukkit.getPlayer(value).playSound(Bukkit.getPlayer(value).getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);

                Bukkit.getPlayer(value).hidePlayer(this, user);

                Bukkit.getPlayer(value).addPotionEffect(PotionEffectType.BLINDNESS.createEffect(20, 20));
            } else {
                // If time was not stopped
                Bukkit.getPlayer(value).setGameMode(GameMode.SURVIVAL);
                Bukkit.getPlayer(value).playSound(Bukkit.getPlayer(value).getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);

                Bukkit.getPlayer(value).showPlayer(this, user);

                Bukkit.getPlayer(value).addPotionEffect(PotionEffectType.BLINDNESS.createEffect(20, 20));

                for (int ii = 0; ii < attackDamageWhenStoppedTime.size(); ii++) {
                    if (attackDamageWhenStoppedTime.containsKey(hunterTeam.get(ii))) {
                        for (UUID key : attackDamageWhenStoppedTime.keySet()) {
                            Bukkit.getPlayer(key).damage(attackDamageWhenStoppedTime.get(key));
                        }
                    }
                }
                attackDamageWhenStoppedTime.clear();
            }
        }

        if (timeStopped) {
            spawnFakePlayer(user);
            user.playSound(user.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);

            for (LivingEntity allEntity : user.getWorld().getLivingEntities()) {
                allEntity.setAI(false);
            }
        } else {
            removeFakePlayer();
            user.playSound(user.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);

            for (LivingEntity allEntity : user.getWorld().getLivingEntities()) {
                allEntity.setAI(true);
            }
        }
        user.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(20, 20));
        user.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Time " + (timeStopped ? "stopped" : "not stop")));
    }


    // Spawn fake runner NPC
    public void spawnFakePlayer(Player player) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.getName());
        npc.spawn(player.getLocation());
        if (npc.isSpawned()) {
            if (player.getInventory().getHelmet() != null)
                npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, new ItemStack(player.getInventory().getHelmet()));

            if (player.getInventory().getChestplate() != null)
                npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(player.getInventory().getChestplate()));

            if (player.getInventory().getLeggings() != null)
                npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(player.getInventory().getLeggings()));

            if (player.getInventory().getBoots() != null)
                npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, new ItemStack(player.getInventory().getBoots()));

            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(player.getInventory().getItemInMainHand()));

            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.OFF_HAND, new ItemStack(player.getInventory().getItemInOffHand()));

            npc.getEntity().setGravity(false);

            npc.setFlyable(true);

            fakeNpc = npc.getEntity();
        }
    }

    // Remove fake player NPC
    public void removeFakePlayer() {
        CitizensAPI.getNPCRegistry().getNPC(fakeNpc).destroy();
    }

    // On game continues (During play) 20 ticks
    public void onDuringPlay() {
        ItemStack compassWow = new ItemStack(Material.COMPASS);
        ItemMeta meta = compassWow.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Runner Tracker");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Right click to change player (If runner more 1)");
        meta.setLore(lore);
        meta.setUnbreakable(true);
        compassWow.setItemMeta(meta);

        ItemStack clockStop = new ItemStack(Material.CLOCK);
        meta = clockStop.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Clock Stopper for Runner");
        lore.clear();
        lore.add("");
        lore.add(ChatColor.GRAY + "Right click to stop time for " + (Integer.parseInt(getConfig().getString("time-stopper.while-time-was-stopped"))) + " seconds");
        meta.setLore(lore);
        meta.setUnbreakable(true);
        clockStop.setItemMeta(meta);

        ItemStack cannotUse = new ItemStack(Material.BARRIER);
        meta = cannotUse.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Can't use this slot");
        lore.clear();
        lore.add("");
        lore.add(ChatColor.GRAY + "Because you or another runner enabled \"Time stopper mode\"");
        meta.setLore(lore);
        meta.setUnbreakable(true);
        cannotUse.setItemMeta(meta);

        for (UUID allPlayer : allPlayers) {
            cooldowns.put(Bukkit.getPlayer(allPlayer).getName(), 120);
            gmCooldowns.put(Bukkit.getPlayer(allPlayer).getName(), 0);
            Bukkit.getPlayer(allPlayer).setGameMode(GameMode.SURVIVAL);
            Bukkit.getPlayer(allPlayer).setHealth(20);
            Bukkit.getPlayer(allPlayer).setFoodLevel(20);
        }

        if (hasTimeStopper)
            for (UUID runnerUUID : runnerTeam) {
                cooldownWaitStopTime.put(Bukkit.getPlayer(runnerUUID).getPlayer(), Integer.parseInt(getConfig().getString("time-stopper.cooldown")));
                Bukkit.getPlayer(runnerUUID).getInventory().setItem(8, cannotUse);
            }

        ItemStack Salt = new ItemStack(Material.SUGAR_CANE);
        ItemMeta saltMeta = Salt.getItemMeta();
        saltMeta.setDisplayName("เกลือ!");
        Salt.setItemMeta(saltMeta);

        started = true;
        gameReady = false;
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {

//            public void run() {
//                        while (started) {

            for (Player eachPlayerInServer : Bukkit.getOnlinePlayers()) {
                if (waitList.containsKey(eachPlayerInServer.getUniqueId())) {
                    gameGUI.backGUI(eachPlayerInServer.getPlayer());
                    eachPlayerInServer.openInventory(gameGUI.backGUIInv);
                }
            }

            // Only hunter
            for (UUID uuid : hunterTeam) {
                //get runner player
                bar.setProgressBar(Bukkit.getPlayer(runnerTeam.get(runnerNoEachHunter.get(uuid))).getHealth(), runnerNoEachHunter.get(uuid));
                Location locRunner = Bukkit.getPlayer(runnerTeam.get(runnerNoEachHunter.get(uuid))).getLocation();

                Bukkit.getPlayer(uuid).getPlayer().setCompassTarget(locRunner);
            }

            // Only runner
            for (UUID uuid : runnerTeam) {

                if (hasGlowing)
                    Bukkit.getPlayer(uuid).addPotionEffect(PotionEffectType.GLOWING.createEffect(99999, 1));

                if (hasTimeStopper) {
                    // Time out after used "Clock Stopper" out time 3 2 1 0
                    if (cooldownAfterStoppedTime.containsKey(Bukkit.getPlayer(uuid).getPlayer())) {
                        if (cooldownAfterStoppedTime.get(Bukkit.getPlayer(uuid).getPlayer()) != null) {
//                        Bukkit.getPlayer(uuid).sendMessage(ChatColor.YELLOW + "[Debug]: " + "cooldownAfterStoppedTime will assign (Null Variable), This is result: " + cooldownAfterStoppedTime.get(Bukkit.getPlayer(uuid).getPlayer()));
                            if (cooldownAfterStoppedTime.get(Bukkit.getPlayer(uuid).getPlayer()) <= Integer.parseInt(getConfig().getString("time-stopper.while-time-was-stopped"))
                                    && cooldownAfterStoppedTime.get(Bukkit.getPlayer(uuid).getPlayer()) > 0) {
                                cooldownAfterStoppedTime.put(Bukkit.getPlayer(uuid).getPlayer(), cooldownAfterStoppedTime.get(Bukkit.getPlayer(uuid).getPlayer()) - 1);
//                            Bukkit.getPlayer(uuid).sendMessage(ChatColor.AQUA + "UUID IS: " + fakeNpc);
                                Bukkit.getPlayer(uuid).getInventory().setItem(8, cannotUse);
                            }
                            if (cooldownAfterStoppedTime.get(Bukkit.getPlayer(uuid).getPlayer()) == 0) {
                                timeStopped = false;
//                            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GOLD + "[Debug]: " + "User: " + clockStopperUser);
                                onTimeStop(clockStopperUser);
                                cooldownWaitStopTime.put(Bukkit.getPlayer(uuid).getPlayer(), Integer.parseInt(getConfig().getString("time-stopper.cooldown")));
                                cooldownAfterStoppedTime.clear();
                            }
                        }
                    }

                    // Cooldown while waiting to use clock stopper in time
                    if (cooldownWaitStopTime.containsKey(Bukkit.getPlayer(uuid).getPlayer())) {
                        if (cooldownWaitStopTime.get(Bukkit.getPlayer(uuid).getPlayer()) != null) {
//                        Bukkit.getPlayer(uuid).sendMessage(ChatColor.GOLD + "[Debug]: " + "cooldownWaitStopTime will assign (Null Variable), This is result: " + cooldownWaitStopTime.get(Bukkit.getPlayer(uuid).getPlayer()));
                            if (cooldownWaitStopTime.get(Bukkit.getPlayer(uuid).getPlayer()) <= Integer.parseInt(getConfig().getString("time-stopper.cooldown"))
                                    && cooldownWaitStopTime.get(Bukkit.getPlayer(uuid).getPlayer()) > 0) { // 1 seconds - config (300 seconds)

                                cooldownWaitStopTime.put(Bukkit.getPlayer(uuid).getPlayer(), cooldownWaitStopTime.get(Bukkit.getPlayer(uuid).getPlayer()) - 1);
                            }
                            if (cooldownWaitStopTime.get(Bukkit.getPlayer(uuid).getPlayer()) < 1) {
                                Bukkit.getPlayer(uuid).getInventory().setItem(8, clockStop);
                                cooldownWaitStopTime.put(Bukkit.getPlayer(uuid).getPlayer(), null);

                            }
                        }
                    }

                    if (timeStopped) {
                        Bukkit.getPlayer(uuid).getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

                    } else {
                        Bukkit.getPlayer(uuid).getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

                    }
                }


            }

            // All players (GLOBAL)
            if (allPlayers.size() > 0) {
                for (UUID allPlayer : allPlayers) {


                    if (!Bukkit.getPlayer(allPlayer).getInventory().contains(Material.COMPASS, 8) && hunterTeam.contains(Bukkit.getPlayer(allPlayer).getUniqueId())) {
                        Bukkit.getPlayer(allPlayer).getInventory().setItem(8, compassWow);
                    }


                    if (Bukkit.getPlayer(allPlayer).getInventory().getItemInMainHand().getType() == Material.END_PORTAL_FRAME ||
                            Bukkit.getPlayer(allPlayer).getInventory().getItemInMainHand().getType() == Material.BEDROCK) {
                        Bukkit.getPlayer(allPlayer).getInventory().setItemInMainHand(Salt);
                    }

                    redLight = !redLight;
                    scoreboard.onGameBoard(Bukkit.getPlayer(allPlayer));

                    if (hasGm1) {

                        if (gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) <= 10 && gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) > 0) {
                            if (gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) == 10) {
                                Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.YELLOW + "OH NO! Gamemode 0 is coming in " + ChatColor.GOLD + gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) + " second(s)");
                            }

                            if (gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) <= 5 && gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) >= 4) {
                                Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.YELLOW + "OH NO! Gamemode 0 is coming in " + ChatColor.RED + gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) + " second(s)");
                            }

                            if (gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) <= 3 && gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) > 0) {
                                Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.YELLOW + "OH NO! Gamemode 0 is coming in " + ChatColor.DARK_RED + gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) + " second(s)");
                            }

                            gmCooldowns.put(Bukkit.getPlayer(allPlayer).getName(), gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) - 1);

                        }

                        if (gmCooldowns.get(Bukkit.getPlayer(allPlayer).getName()) == 0) {
                            if (Bukkit.getPlayer(allPlayer).getGameMode() == GameMode.CREATIVE) {
                                Bukkit.getPlayer(allPlayer).setGameMode(GameMode.SURVIVAL);
                                Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.GREEN + "Changed to Survival!" + ChatColor.WHITE + " ไปล่า / จบเกม ต่อเลย!!");

                                cooldowns.put(Bukkit.getPlayer(allPlayer).getName(), 120);
                            }
                        }


                        //วินาที


                        if (cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) <= 120 && cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) > 0) {
                            if (cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) == 60) {
                                Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.AQUA + "Gamemode 1 is coming in " + (cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) / 60) + " minute");
                            }

                            if (cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) == 30) {
                                Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.AQUA + "Gamemode 1 is coming in " + cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) + " second(s)");
                            }

                            if (cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) == 10) {
                                Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.AQUA + "Gamemode 1 is coming in " + cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) + " second(s)");
                            }

                            if (cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) <= 5 && cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) > 0) {
                                Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.AQUA + "Gamemode 1 is coming in " + cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) + " second(s)");
                            }

                            cooldowns.put(Bukkit.getPlayer(allPlayer).getName(), cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) - 1);
                        }

                        if (cooldowns.get(Bukkit.getPlayer(allPlayer).getName()) == 0) {
                            if (Bukkit.getPlayer(allPlayer).getGameMode() == GameMode.SURVIVAL) {
                                Bukkit.getPlayer(allPlayer).setGameMode(GameMode.CREATIVE);
                                Bukkit.getPlayer(allPlayer).sendMessage(ChatColor.GREEN + "Changed to Creative!" + ChatColor.WHITE + " ตอนนี้เสกของได้ตามที่ต้องการ!!");
                                gmCooldowns.put(Bukkit.getPlayer(allPlayer).getName(), 10);
                            }
                        }
                    }

                }

                gameTime++;

            }
//                    }                                         //1 Second
        }, 0 /* delay before first announcement */, 20 /* delay between announcements */); //ticks per second = 20 ticks
    }

    // cancel on starting cooldown (e.g. 15 14 ... 2 1 0 and cancel)
    public void cancelStartingCooldown() {
        for (UUID allPlayer : allPlayers) {
            Bukkit.getPlayer(allPlayer).playSound(Bukkit.getPlayer(allPlayer).getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            if (runnerTeam.contains(Bukkit.getPlayer(allPlayer).getUniqueId()))
                Bukkit.getPlayer(allPlayer).sendTitle("" + ChatColor.RED + ChatColor.BOLD + "RUNN!!", "kill ender dragon and escape from the end!", 1, 20, 1);

            if (hunterTeam.contains(Bukkit.getPlayer(allPlayer).getUniqueId()))
                Bukkit.getPlayer(allPlayer).sendTitle("" + ChatColor.RED + ChatColor.BOLD + "KILL!!", "try to kill runner!", 1, 20, 1);

            getServer().getScheduler().cancelTasks(plugin);
            onDuringPlay();
        }
    }

    // Cooldown before game start
    public void onGameStarted(Player player) {
        for (UUID allPlayer : allPlayers) {
            cooldownsStarting.put(Bukkit.getPlayer(allPlayer).getName(), 15);
            Bukkit.getPlayer(allPlayer).getInventory().clear();
            Bukkit.getPlayer(allPlayer).closeInventory();
        }

        onTeamSelect = false;
//
//        final int[] cooldownStarting = {15};
        int taskId = -1;

        for (UUID allPlayer : allPlayers) {

            Bukkit.getPlayer(allPlayer).playSound(Bukkit.getPlayer(allPlayer).getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        }

        for (UUID uuid : hunterTeam) {
            bar.addPlayer(Bukkit.getPlayer(uuid));
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "Speedrunner was started by " + ChatColor.AQUA + player.getName());


//        cooldowns.put(player.getName(), System.currentTimeMillis() + (120 * 1000));
        taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {

                if (!started) {
                    for (UUID allPlayer : allPlayers) {
                        // is equal 10 but dont more 5 (6s - 10s)

                        if (cooldownsStarting.get(Bukkit.getPlayer(allPlayer).getName()) <= 15) {

                            Bukkit.getPlayer(allPlayer).playSound(Bukkit.getPlayer(allPlayer).getLocation(), Sound.BLOCK_BASALT_BREAK, 1.0f, 1.0f);
                            Bukkit.getPlayer(allPlayer).sendTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "READY? " + ChatColor.GREEN + (cooldownsStarting.get(Bukkit.getPlayer(allPlayer).getName())), "", 1, 20, 1);
                            cooldownsStarting.put(Bukkit.getPlayer(allPlayer).getName(), cooldownsStarting.get(Bukkit.getPlayer(allPlayer).getName()) - 1);
                        }


                        // Start Warping
                        if (cooldownsStarting.get(Bukkit.getPlayer(allPlayer).getName()) == 5) { // is equal 0 (0s)
                            spawnWorldLocation = getServer().getWorld(getConfig().getString("world.default-world")).getSpawnLocation();
                            Bukkit.getPlayer(allPlayer).teleport(spawnWorldLocation);
//                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + players.getName() + " useworld");
                            Bukkit.getPlayer(allPlayer).playSound(Bukkit.getPlayer(allPlayer).getLocation(), Sound.BLOCK_BASALT_PLACE, 1.0f, 1.0f);
                            Bukkit.getPlayer(allPlayer).sendTitle("" + ChatColor.YELLOW + (cooldownsStarting.get(Bukkit.getPlayer(allPlayer).getName())), "For best experience, Please call to your friend for talking", 1, 20, 1);
                            cooldownsStarting.put(Bukkit.getPlayer(allPlayer).getName(), cooldownsStarting.get(Bukkit.getPlayer(allPlayer).getName()) - 1);

                        }


                        if (cooldownsStarting.get(Bukkit.getPlayer(allPlayer).getName()) == 0) {
                            cancelStartingCooldown();
                        }


                    }

                }

            }
        }, 0, 20);


        // FOREACH PLAYER
    }
}