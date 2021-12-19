package tuwavy.tut;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GameGUI {
    static Main plugin;
    public GameGUI(Main instance) {
        plugin = instance;
    }
    // GUI (on select team or became hunter)
    public Inventory normalGUIInv;

    // GUI (to be runner)
    public Inventory runnerGUIInv;

    // Config GUI (only runner)
    public Inventory configureGUIInv;

    // Confirm to back to game
    public Inventory backGUIInv;

    // Confirm to back to game
    public Inventory selectTimeInv;

    // Deprecated
    public Inventory timeStoppedInv;

    // =======================[GUI]===========================
    public void normalGUI() {
        normalGUIInv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Select your team");

        ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
        ItemMeta meta = item.getItemMeta();

        // Runner Team
        meta.setDisplayName(ChatColor.GREEN + "Runner Team");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "" + plugin.runnerTeam.size() + " / 1");
        lore.add(ChatColor.GRAY + "Click to join runner team");
        meta.setLore(lore);
        item.setItemMeta(meta);
        normalGUIInv.setItem(11, item);

        //Hunter Team
        item.setType(Material.ARROW);
        meta.setDisplayName(ChatColor.GOLD + "Hunter Team");
        item.setItemMeta(meta);
        lore.clear();
        lore.add(ChatColor.WHITE + plugin.getHunterPlayer().toString() + " / Infinity");
        lore.add(ChatColor.GRAY + "Click to join hunter team");
        meta.setLore(lore);
        item.setItemMeta(meta);
        normalGUIInv.setItem(14, item);
    }

    public void backGUI(Player player) {
        backGUIInv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Back to game? (Beta)");

        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();

        // Runner Team
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "I want back to game!!");
        List<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Click to back game as " + plugin.waitList.get(player.getUniqueId()) + " team");
        meta.setLore(lore);
        item.setItemMeta(meta);
        backGUIInv.setItem(11, item);

        //Hunter Team
        item.setType(Material.ARROW);
        meta.setDisplayName(ChatColor.GOLD + "No, I don't");
        item.setItemMeta(meta);
        lore.clear();
        lore.add("");
        lore.add(ChatColor.GRAY + "Click to leave the game completely");
        meta.setLore(lore);
        item.setItemMeta(meta);
        backGUIInv.setItem(15, item);
    }

    // GUI
    public void runnerGUI() {
        runnerGUIInv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Select your team");

        ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
        ItemMeta meta = item.getItemMeta();

        // Runner Team
        meta.setDisplayName(ChatColor.GREEN + "Runner Team");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "" + plugin.runnerTeam.size() + " / 1");
        lore.add(ChatColor.GRAY + "Click to join runner team");
        meta.setLore(lore);
        item.setItemMeta(meta);
        runnerGUIInv.setItem(11, item);

        //Hunter Team
        item.setType(Material.ARROW);
        meta.setDisplayName(ChatColor.GOLD + "Hunter Team");
        lore.clear();
        lore.add(ChatColor.WHITE + plugin.getHunterPlayer().toString() + " / Infinity");
        lore.add(ChatColor.GRAY + "Click to join hunter team");
        meta.setLore(lore);
        item.setItemMeta(meta);
        runnerGUIInv.setItem(14, item);

        // ICON 1
        item.setType(Material.NETHERITE_BOOTS);
        meta.setDisplayName(ChatColor.AQUA + "You're runner team");
        lore.clear();
        meta.setLore(lore);
        item.setItemMeta(meta);
        runnerGUIInv.setItem(8, item);

        // ICON 2 Configuration
        item.setType(Material.COMPARATOR);
        meta.setDisplayName(ChatColor.WHITE + "Configuration");
        lore.clear();
        lore.add(ChatColor.GRAY + "Click to settings");
        meta.setLore(lore);
        item.setItemMeta(meta);
        runnerGUIInv.setItem(17, item);

        // ICON 3 Start Game
        item.setType(Material.ENDER_EYE);
        meta.setDisplayName(ChatColor.GREEN + "Start the game");
        lore.clear();
        lore.add(ChatColor.GRAY + "Click to start");
        meta.setLore(lore);
        item.setItemMeta(meta);
        runnerGUIInv.setItem(26, item);

        // Not Selected
        item.setType(Material.GRAY_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.WHITE + " ");
        lore.clear();
        meta.setLore(lore);
        item.setItemMeta(meta);
        runnerGUIInv.setItem(16, item);

        // Selected
        item.setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.AQUA + " > ");
        lore.clear();
        meta.setLore(lore);
        item.setItemMeta(meta);
        runnerGUIInv.setItem(7, item);
    }

    //GUI
    public void selectTimeGUI() {
        selectTimeInv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Select Time to Speedrun");

        ItemStack item = new ItemStack(Material.GLOWSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> lore = new ArrayList<String>();

        if (plugin.dayOrNight) {
            // Record btn
            meta.setDisplayName(ChatColor.YELLOW + "DAY");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.GREEN + "SELECTED");
            meta.setLore(lore);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
            item.setItemMeta(meta);
            selectTimeInv.setItem(11, item);
        } else {
            // Record btn
            meta.setDisplayName(ChatColor.YELLOW + "DAY");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.GRAY + "NOT SELECT");
            meta.setLore(lore);
            meta.removeEnchant(Enchantment.ARROW_DAMAGE);
            item.setItemMeta(meta);
            selectTimeInv.setItem(11, item);
        }

        if (!plugin.dayOrNight) {
            // Record btn
            item.setType(Material.SKELETON_SKULL);
            meta.setDisplayName(ChatColor.DARK_BLUE + "NIGHT");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.GREEN + "SELECTED");
            meta.setLore(lore);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
            item.setItemMeta(meta);
            selectTimeInv.setItem(14, item);
        } else {
            // Record btn
            item.setType(Material.SKELETON_SKULL);
            meta.setDisplayName(ChatColor.YELLOW + "NIGHT");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.GRAY + "NOT SELECT");
            meta.setLore(lore);
            meta.removeEnchant(Enchantment.ARROW_DAMAGE);
            item.setItemMeta(meta);
            selectTimeInv.setItem(14, item);
        }

        // ICON 1
        item.setType(Material.NETHERITE_BOOTS);
        meta.setDisplayName(ChatColor.AQUA + "You're runner team");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        selectTimeInv.setItem(8, item);

        // ICON 2 Configuration
        item.setType(Material.COMPARATOR);
        meta.setDisplayName(ChatColor.WHITE + "Configuration");
        lore.clear();
        lore.add(ChatColor.GRAY + "Click to settings");
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        selectTimeInv.setItem(17, item);

        // ICON 3 Start Game
        item.setType(Material.ENDER_EYE);
        meta.setDisplayName(ChatColor.GREEN + "Start the game");
        lore.clear();
        lore.add(ChatColor.GRAY + "Click to start");
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        selectTimeInv.setItem(11, item);

        // Not Selected
        item.setType(Material.GRAY_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.WHITE + " ");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        selectTimeInv.setItem(7, item);

        // Not Selected
        item.setType(Material.GRAY_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.WHITE + " ");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        selectTimeInv.setItem(16, item);

        // Selected
        item.setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.AQUA + " > ");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        selectTimeInv.setItem(25, item);

        // CUSTOMIZE TIME
        item.setType(Material.CLOCK);
        meta.setDisplayName(ChatColor.YELLOW + "Select Time");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        selectTimeInv.setItem(26, item);
    }

    // GUI
    public void configureGUI() {
        configureGUIInv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Configuration");

        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> lore = new ArrayList<String>();

        if (plugin.hasGm1) {
            // Runner Team
            meta.setDisplayName(ChatColor.GREEN + "I want gamemode creative!!");
            lore.add("");
            lore.add(ChatColor.GRAY + "Every 2 minutes. Each team will have \"Gamemode Creative\"");
            lore.add(ChatColor.GRAY + "but have 10 seconds to use it!");
            lore.add(ChatColor.GREEN + "ENABLED");
            meta.setLore(lore);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
            item.setItemMeta(meta);
            configureGUIInv.setItem(13, item);
        } else {
            // Runner Team
            meta.setDisplayName(ChatColor.GREEN + "I want gamemode creative!!");
            lore.add("");
            lore.add(ChatColor.GRAY + "Every 2 minutes. Each team will have \"Gamemode Creative\"");
            lore.add(ChatColor.GRAY + "but have 10 seconds to use it!");
            lore.add(ChatColor.RED + "DISABLED");
            meta.setLore(lore);
            meta.removeEnchant(Enchantment.ARROW_DAMAGE);
            item.setItemMeta(meta);
            configureGUIInv.setItem(13, item);
        }

        if (plugin.hasTimeStopper) {
            //Time stop mode
            item.setType(Material.CLOCK);
            meta.setDisplayName(ChatColor.DARK_AQUA + "I can control time!!");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.GRAY + "Make all runner has time stopper");
            lore.add(ChatColor.GREEN + "ENABLED");
            meta.setLore(lore);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
            item.setItemMeta(meta);
            configureGUIInv.setItem(14, item);
        } else {
            //Time stop mode
            item.setType(Material.CLOCK);
            meta.setDisplayName(ChatColor.DARK_AQUA + "I can control time!!");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.GRAY + "Make all runner has time stopper");
            lore.add(ChatColor.RED + "DISABLED");
            meta.setLore(lore);
            meta.removeEnchant(Enchantment.ARROW_DAMAGE);
            item.setItemMeta(meta);
            configureGUIInv.setItem(14, item);
        }

        if (plugin.hasGlowing) {
            //Hunter Team
            item.setType(Material.GLOWSTONE_DUST);
            meta.setDisplayName(ChatColor.GOLD + "Me Glowing!!");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.GRAY + "Make all runner has glowing!");
            lore.add(ChatColor.GREEN + "ENABLED");
            meta.setLore(lore);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
            item.setItemMeta(meta);
            configureGUIInv.setItem(15, item);
        } else {
            //
            item.setType(Material.GLOWSTONE_DUST);
            meta.setDisplayName(ChatColor.GOLD + "Me Glowing!!");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.GRAY + "Make all runner has glowing!");
            lore.add(ChatColor.RED + "DISABLED");
            meta.setLore(lore);
            meta.removeEnchant(Enchantment.ARROW_DAMAGE);
            item.setItemMeta(meta);
            configureGUIInv.setItem(15, item);
        }

        // ICON 1
        item.setType(Material.NETHERITE_BOOTS);
        meta.setDisplayName(ChatColor.AQUA + "You're runner team");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        configureGUIInv.setItem(8, item);

        // ICON 2 Configuration
        item.setType(Material.COMPARATOR);
        meta.setDisplayName(ChatColor.WHITE + "Configuration");
        lore.clear();
        lore.add(ChatColor.GRAY + "Click to settings");
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        configureGUIInv.setItem(17, item);

        // ICON 3 Start Game
        item.setType(Material.ENDER_EYE);
        meta.setDisplayName(ChatColor.GREEN + "Start the game");
        lore.clear();
        lore.add(ChatColor.GRAY + "Click to start");
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        configureGUIInv.setItem(11, item);

        // Not Selected
        item.setType(Material.GRAY_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.WHITE + " ");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        configureGUIInv.setItem(7, item);

        // Selected
        item.setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.AQUA + " > ");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        configureGUIInv.setItem(16, item);

        // Not Selected
        item.setType(Material.GRAY_STAINED_GLASS_PANE);
        meta.setDisplayName(ChatColor.WHITE + " ");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        configureGUIInv.setItem(25, item);

        // CUSTOMIZE TIME
        item.setType(Material.CLOCK);
        meta.setDisplayName(ChatColor.YELLOW + "Select Time");
        lore.clear();
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.ARROW_DAMAGE);
        item.setItemMeta(meta);
        configureGUIInv.setItem(26, item);

        if (plugin.hasRecordTime) {
            // Record btn
            item.setType(Material.REDSTONE_BLOCK);
            meta.setDisplayName(ChatColor.WHITE + "Record Time");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.GREEN + "ENABLED");
            meta.setLore(lore);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
            item.setItemMeta(meta);
            configureGUIInv.setItem(10, item);
        } else {
            // Record btn
            item.setType(Material.REDSTONE_BLOCK);
            meta.setDisplayName(ChatColor.WHITE + "Record Time");
            lore.clear();
            lore.add("");
            lore.add(ChatColor.RED + "DISABLED");
            meta.setLore(lore);
            meta.removeEnchant(Enchantment.ARROW_DAMAGE);
            item.setItemMeta(meta);
            configureGUIInv.setItem(10, item);
        }
    }

    // GUI (Deprecated)
    public void timeStoppedGUI() {
        timeStoppedInv = Bukkit.createInventory(null, 27, ChatColor.RED + "TIME WAS STOPPED!!");
    }

    // ==============================[END GUI]==================================
}
