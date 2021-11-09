package tuwavy.tut;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.player.PlayerInventory;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Event implements Listener {
    // Detect all event in game

    static Main plugin;
    static Scoreboard scoreboard;

    Map<String, Integer> cooldownsToLobby = new HashMap<>();
    public Event(Main instance) {
        plugin = instance;
        scoreboard = new Scoreboard(plugin);
    }

//    เมื่อ Player เข้าเกม
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

//        if (!event.getPlayer().isOp()) {
//            event.getPlayer().setGameMode(GameMode.ADVENTURE);
//        } else {
//            event.getPlayer().setGameMode(GameMode.CREATIVE);
//        }

        if (plugin.onRegenWorld) {
            event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', "&bRegenerating world &f&l(" + plugin.worldRegen + " / 3)"));
        }
//        event.getPlayer().teleport(plugin.getConfig().getLocation("position.lobby"));

        if (plugin.waitList.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            plugin.backGUI(event.getPlayer());
            event.getPlayer().openInventory(plugin.backGUIInv);
        }

        if (plugin.allPlayers.contains(event.getPlayer().getUniqueId())) {
            if (!plugin.started)
                scoreboard.onConfigBoard(event.getPlayer());
        }

        if (!plugin.allPlayers.contains(event.getPlayer().getUniqueId()))
            scoreboard.onJoinBoard(event.getPlayer());

//        if (plugin.started) {
//            event.getPlayer().teleport(plugin.getConfig().getLocation("position.lobby"));
//            event.getPlayer().setGameMode(GameMode.ADVENTURE);
//        }
    }

//  เมื่อ Player ออกเกม
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (plugin.started)
            if (plugin.runnerTeam.contains(event.getPlayer().getUniqueId())) {
                plugin.runnerTeam.remove(event.getPlayer().getUniqueId());
                plugin.allPlayers.remove(event.getPlayer().getUniqueId());

                plugin.waitList.put(event.getPlayer().getUniqueId(), "runner");
                if (plugin.runnerTeam.size() == 0)
                    plugin.onStopGame(event.getPlayer());
            }

            if (plugin.hunterTeam.contains(event.getPlayer().getUniqueId())) {
                plugin.hunterTeam.remove(event.getPlayer().getUniqueId());
                plugin.allPlayers.remove(event.getPlayer().getUniqueId());

                plugin.waitList.put(event.getPlayer().getUniqueId(), "hunter");
            }
    }

    @EventHandler
    public void onPlayMove(PlayerMoveEvent event) {
        if (plugin.timeStopped)
//            if (plugin.hunterTeam.contains(event.getPlayer().getUniqueId()))
            if (plugin.clockStopperUser != null)
                if (plugin.clockStopperUser.getUniqueId() != event.getPlayer().getUniqueId()) {
                    event.setCancelled(true);
                }

        if (plugin.waitList.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerMoveInventoryItem(InventoryMoveItemEvent event) {
        if (plugin.gameReady) event.setCancelled(true);
    }

    // เมื่อ Player และทุก mobs ตีคนอื่น
    @EventHandler
    public void onHitPlayerDamage(EntityDamageByEntityEvent event) { // Player ตีคนอื่น
        Entity damager = event.getDamager(); //คนตี
        Entity subject = event.getEntity(); //คนถูกตี

        if (damager instanceof Player) {
            if (!plugin.started) {
                event.setCancelled(true);
            }

            if (plugin.hunterTeam.contains(damager.getUniqueId()) && plugin.hunterTeam.contains(subject.getUniqueId())) // Same team hit in team
                event.setCancelled(true);
            if (plugin.runnerTeam.contains(damager.getUniqueId()) && plugin.runnerTeam.contains(subject.getUniqueId()))
                event.setCancelled(true);

            if (subject instanceof Player) {
                if (((Player) damager).getLastDamage() - (((Player) subject).getHealth()) >= 0) {
                    ((Player) subject).playSound(subject.getLocation(), Sound.ENTITY_EXPERIENCE_BOTTLE_THROW, 1.0f, 1.0f);

                    for (UUID allPlayers : plugin.allPlayers) {
                        Bukkit.getPlayer(allPlayers).sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + subject.getName() + " &fwas ORAORA by &b" + damager.getName()));

                    }
                }
            }

//            if (plugin.hunterTeam.contains(subject.getUniqueId()) && plugin.runnerTeam.contains(damager.getUniqueId()))\
//                if (plugin.timeStopped) {
//                    event.setCancelled(true);
//                    ((Player) damager).getPlayer().playSound(damager.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
//                    plugin.attackDamageWhenStoppedTime.put(subject.getUniqueId(),
//                            (( (Double) plugin.attackDamageWhenStoppedTime.get(subject.getUniqueId()) == null ? 0 :
//                                    ( (Double) plugin.attackDamageWhenStoppedTime.get(subject.getUniqueId()) )
//                                            + ((Player) damager).getLastDamage()) ));
//                    ((Player) damager).getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Attacked \"" + subject.getName() +"\" " + plugin.attackDamageWhenStoppedTime.get(subject.getUniqueId()) + " damage"));
//                }

        }
    }

    @EventHandler
    public void onGoToOtherDimisions(PlayerChangedWorldEvent event) {
        Player playerThatGoToOtherDimisions = event.getPlayer();
        if (plugin.started) {
            for (UUID spectatorList : plugin.spectatorList) {
                Bukkit.getPlayer(spectatorList).sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + playerThatGoToOtherDimisions.getName() + " &fhas gone to " + event.getPlayer().getWorld().getName() + " dimisions"));
            }
        }
    }

    @EventHandler
    public void onChangeGameMode(PlayerGameModeChangeEvent event) {
        if (plugin.spectatorList.contains(event.getPlayer().getUniqueId())) {
            if (event.getNewGameMode() != GameMode.SPECTATOR) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerGotDamage(EntityDamageEvent event) {
        if (event.getEntity().hasMetadata("NPC")) return;

        EntityDamageEvent entity = event;
        Player player = Bukkit.getPlayer(entity.getEntity().getUniqueId());

        if (entity.getEntityType().equals(EntityType.PLAYER)) {
            if (plugin.spectatorList.contains(player.getUniqueId()))
                event.setCancelled(true);

            if (plugin.waitList.containsKey(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
            }

            if (entity.getDamage() - player.getHealth() >= 0) {
                event.setCancelled(true);
                for (ItemStack allItem : player.getInventory().getContents()) {
                    if (allItem != null) {
                        player.getLocation().getWorld().dropItemNaturally(player.getLocation(), allItem);
                    }
                }
                player.setHealth(20);

                if (plugin.runnerTeam.contains(player.getUniqueId())) {
                    plugin.runnerDiedCount++;
                    plugin.spectatorList.add(player.getUniqueId());
                    player.setGameMode(GameMode.SPECTATOR);

                    for (int i = 0; i < plugin.runnerTeam.size(); i++) {
                        Bukkit.getPlayer(plugin.runnerTeam.get(i)).sendMessage(ChatColor.YELLOW + "" + player.getName() + " was lost. Left " + plugin.runnerDiedCount + (plugin.runnerDiedCount > 1 ? " players" : " player"));
                    }
                    for (int i = 0; i < plugin.allPlayers.size(); i++) {
                        if (plugin.hunterTeam.contains(Bukkit.getPlayer(plugin.allPlayers.get(i)).getUniqueId()))
                            Bukkit.getPlayer(plugin.allPlayers.get(i)).sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + (player.getName()) + " was dead!"
                                    , "");

                        if (plugin.runnerDiedCount == plugin.runnerTeam.size()) {
                            if (plugin.runnerTeam.contains(Bukkit.getPlayer(plugin.allPlayers.get(i)).getUniqueId())) {
                                Bukkit.getPlayer(plugin.allPlayers.get(i)).sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "YOU LOSE!!"
                                        , ChatColor.DARK_GREEN + "Thank for playing. You can start again!");
                            }

                            cooldownsToLobby.put(Bukkit.getPlayer(plugin.allPlayers.get(i)).getName(), 10);
                            int finalI = i;
                            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                                if (cooldownsToLobby.get(Bukkit.getPlayer(plugin.allPlayers.get(finalI)).getName()) > 0) {
                                    // 10 Sec to waiting
                                    Bukkit.getPlayer(plugin.allPlayers.get(finalI)).sendMessage(ChatColor.YELLOW + "Teleporting to lobby in " + (cooldownsToLobby.get(Bukkit.getPlayer(plugin.allPlayers.get(finalI)).getName())) + " second");
                                    cooldownsToLobby.put(Bukkit.getPlayer(plugin.allPlayers.get(finalI)).getName(), cooldownsToLobby.get(Bukkit.getPlayer(plugin.allPlayers.get(finalI)).getName()) - 1);
                                } else {
                                    Bukkit.getPlayer(plugin.allPlayers.get(finalI)).sendMessage(ChatColor.GREEN + "Teleporting to lobby...");
                                    cooldownsToLobby.clear();

                                    plugin.onStopGame(player);
                                }
                            }, 0, 20);
                        }

                    }
                }

                if (plugin.hunterTeam.contains(player.getUniqueId())) {
                    player.teleport(plugin.spawnWorldLocation);
                }
            }

            }
        }


// เมื่อมังกรตาย
    @EventHandler
    public void onDragonDead(EntityDeathEvent event) { //ตรวจจับ Entity ที่ตาย
        Entity killer = event.getEntity().getKiller(); // เก็บค่าคนที่ฆ่า
        boolean isDragon = event.getEntity().getType().equals(EntityType.ENDER_DRAGON);// เก็บค่า boolean ที่เช็คว่าเป็นมังกรไหม

        if (isDragon)
            if (plugin.runnerTeam.equals(killer.getUniqueId())) {
                plugin.onVictoryGame(Bukkit.getPlayer(killer.getUniqueId()));
            }

    }

//    เมื่อ player ตาย
    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getEntity();
        boolean isDead = Bukkit.getServer().getPlayer(player.getUniqueId()).isDead();
        if (player.isDead()) {
            if (plugin.hunterTeam.contains(player.getUniqueId())) {
                player.teleport(plugin.spawnWorldLocation);
            }
        }
    }

    @EventHandler
    public void onPickUpItem(InventoryPickupItemEvent event) {
        if (!plugin.started)
            event.setCancelled(true);

        if (event.getItem().getItemStack().getItemMeta().isUnbreakable())
            event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!plugin.started) e.setCancelled(true);

        if (e.getCurrentItem() == null) return; // empty item
        if (e.getCurrentItem().getItemMeta() == null) return; // empty item meta
        if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;

        Player player = (Player) e.getWhoClicked();

        if (e.getInventory().equals(plugin.normalGUIInv)) {
            e.setCancelled(true);

            if (e.getSlot() == 11) {
                // Runner position item
                plugin.onJoinRunnerTeam(player);
                return;
            }

            if (e.getSlot() == 14) {
                // Runner position item
                plugin.onJoinHunterTeam(player);
                return;
            }
        }

        if (e.getInventory().equals(plugin.runnerGUIInv)) {
            e.setCancelled(true);
            if (e.getSlot() == 0) {
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.0f);
                player.closeInventory();
                return;
            }

            if (e.getSlot() == 11) {
                // Runner position item
                plugin.onJoinRunnerTeam(player);
                return;
            }

            if (e.getSlot() == 14) {
                // Runner position item'
                plugin.onJoinHunterTeam(player);
                return;
            }

            if (e.getSlot() == 17) {
//                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.0f);
                player.openInventory(plugin.configureGUIInv);
                return;
            }

            if (e.getSlot() == 26) {
                // Start Game
                player.closeInventory();
                plugin.onGameStarted(player);
                return;
            }
        }

        if (e.getInventory().equals(plugin.backGUIInv)) {
            e.setCancelled(true);
            if (e.getSlot() == 11) {
                if (Objects.equals(plugin.waitList.get(e.getWhoClicked().getUniqueId()), "runner")) {
//            Bukkit.broadcastMessage("Some runner has comeback!!");
                    plugin.allPlayers.add(e.getWhoClicked().getUniqueId());
                    plugin.runnerTeam.add(e.getWhoClicked().getUniqueId());

                    plugin.waitList.remove(e.getWhoClicked().getUniqueId());
                    for (UUID allPlayers : plugin.allPlayers) {
                        Bukkit.getPlayer(allPlayers).sendMessage(ChatColor.translateAlternateColorCodes('&', e.getWhoClicked().getName() + "&b was showen up to run!!."));
                    }
                }

                if (Objects.equals(plugin.waitList.get(e.getWhoClicked().getUniqueId()), "hunter")) {
//            Bukkit.broadcastMessage("Some hunter has comeback!!");
                    plugin.allPlayers.add(e.getWhoClicked().getUniqueId());
                    plugin.hunterTeam.add(e.getWhoClicked().getUniqueId());

                    plugin.waitList.remove(e.getWhoClicked().getUniqueId());

                    for (UUID allPlayers : plugin.allPlayers) {
                        Bukkit.getPlayer(allPlayers).sendMessage(ChatColor.translateAlternateColorCodes('&', e.getWhoClicked().getName() + "&b was showen up to kill runner!!."));
                    }
                }

                e.getWhoClicked().closeInventory();
            } else {
                plugin.waitList.remove(e.getWhoClicked().getUniqueId());
                e.getWhoClicked().teleport(plugin.getConfig().getLocation("position.lobby"));
                for (UUID allPlayers : plugin.allPlayers) {
                    Bukkit.getPlayer(allPlayers).sendMessage(ChatColor.translateAlternateColorCodes('&', e.getWhoClicked().getName() + "&e left the game completely."));
                }
            }
        }

        if (e.getInventory().equals(plugin.configureGUIInv)) {
            e.setCancelled(true);
            if (e.getSlot() == 10) {
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1.0f, 1.0f);
                plugin.onToggleRecord(player);
                plugin.configureGUI();

                for (UUID uuid : plugin.runnerTeam) {
                    Bukkit.getPlayer(uuid).openInventory(plugin.configureGUIInv);
                }
                return;
            }

            if (e.getSlot() == 13) {
                // Gm 1 event toggle btn
                plugin.onToggleGm1(player);
                plugin.configureGUI();
                for (UUID uuid : plugin.runnerTeam) {
                    Bukkit.getPlayer(uuid).openInventory(plugin.configureGUIInv);
                }

                return;
            }

            if (e.getSlot() == 14) {
                // Gm 1 event toggle btn
                plugin.onToggleTimeStopper(player);
                plugin.configureGUI();
                for (UUID uuid : plugin.runnerTeam) {
                    Bukkit.getPlayer(uuid).openInventory(plugin.configureGUIInv);
                }

                return;
            }

            if (e.getSlot() == 15) {
                // Glowing effect toggle btn
                plugin.onToggleGlowingFx(player);
                plugin.configureGUI();
                for (UUID uuid : plugin.runnerTeam) {
                    Bukkit.getPlayer(uuid).openInventory(plugin.configureGUIInv);
                }
                return;
            }

            if (e.getSlot() == 8) {
//                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.0f);
                player.openInventory(plugin.runnerGUIInv);
                return;
            }

            if (e.getSlot() == 26) {
//                player.closeInventory();
                plugin.errorSound(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSorry, It not available now."));
//                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.0f);
//                player.openInventory(plugin.selectTimeInv);
                return;
            }

            if (e.getSlot() == 11) {
                // Start Game
                player.closeInventory();
                plugin.onGameStarted(player);
                return;
            }
        }

        if (e.getInventory().equals(plugin.selectTimeInv)) {
            e.setCancelled(true);
            if (e.getSlot() == 11) {
                player.playSound(player.getLocation(), Sound.ENTITY_COW_AMBIENT, 1.0f, 1.0f);
                plugin.onChooseDay(player);
                plugin.selectTimeGUI();

                for (UUID uuid : plugin.runnerTeam) {
                    Bukkit.getPlayer(uuid).openInventory(plugin.selectTimeInv);
                }
                return;
            }

            if (e.getSlot() == 14) {
                // Gm 1 event toggle btn
                player.playSound(player.getLocation(), Sound.ENTITY_BAT_AMBIENT, 1.0f, 1.0f);
                plugin.onChooseNight(player);
                plugin.selectTimeGUI();

                for (UUID uuid : plugin.runnerTeam) {
                    Bukkit.getPlayer(uuid).openInventory(plugin.selectTimeInv);
                }

                return;
            }

            if (e.getSlot() == 8) {
//                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.0f);
                player.openInventory(plugin.runnerGUIInv);
                return;
            }

            if (e.getSlot() == 17) {
                // Start Game
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.0f);
                player.openInventory(plugin.configureGUIInv);
                return;
            }

            if (e.getSlot() == 26) {
//                player.closeInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.0f);
//                player.openInventory(plugin.selectTimeInv);
                return;
            }

            if (e.getSlot() == 11) {
                // Start Game
                player.closeInventory();
                plugin.onGameStarted(player);
                return;
            }
        }

    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CHEST))
//            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName() == "TEAM SELECTOR")
                if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                    Player player = (Player) e.getPlayer();

                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (plugin.allPlayers.contains(e.getPlayer().getUniqueId())) {
                            if (plugin.runnerTeam.contains(player.getUniqueId())) {
                                player.openInventory(plugin.runnerGUIInv);
                            } else {
                                player.openInventory(plugin.normalGUIInv);
                            }
                        } else {
                            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You must to join the game first"));
                            e.getPlayer().getInventory().clear();
                        }
                    }
                }

        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COMPASS))
            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                Player player = (Player) e.getPlayer();

                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if ( plugin.hunterTeam.contains(e.getPlayer().getUniqueId() )) {
                        plugin.runnerNoEachHunter.put(player.getUniqueId(), plugin.runnerNoEachHunter.get(player.getUniqueId()) + 1);
                        if (plugin.runnerNoEachHunter.get(player.getUniqueId()) > plugin.runnerTeam.size() - 1) plugin.runnerNoEachHunter.put(player.getUniqueId(), 0);
                        player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1.0f, 1.0f);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.AQUA + "Pointing to " + Bukkit.getPlayer( plugin.runnerTeam.get( plugin.runnerNoEachHunter.get(player.getUniqueId()) ) ).getName()));
                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Can't use runner tracker! DROP IT!!!"));
                    }

                }
            }

        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CLOCK))
            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                Player player = (Player) e.getPlayer();

                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if ( plugin.runnerTeam.contains(e.getPlayer().getUniqueId() )) {
                        plugin.timeStopped = true;
                        plugin.clockStopperUser = player;
                        plugin.onTimeStop(player);
                        player.getInventory().setItem(8, null);
                        plugin.cooldownAfterStoppedTime.put(player, Integer.parseInt(plugin.getConfig().getString("time-stopper.while-time-was-stopped")));


                    } else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Can't use SAWARUDO! DROP IT!!!"));
                    }

                }
            }
    }

    @EventHandler
    public void onDeath(EntityDamageEvent event) {
        if (!plugin.started) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CHEST))
//            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName() == "TEAM SELECTOR")
            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                e.setCancelled(true);
            }

        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BARRIER))
//            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName() == "TEAM SELECTOR")
            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (!plugin.started)
            e.setCancelled(true);

        ItemMeta meta = e.getItemDrop().getItemStack().getItemMeta();
            if (e.getPlayer().getInventory().getItemInMainHand().getAmount() == 0) {
                if (meta.isUnbreakable()) {
                    e.setCancelled(true);
                }
            }
    }

}
