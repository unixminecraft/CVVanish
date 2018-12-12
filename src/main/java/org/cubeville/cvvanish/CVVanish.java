package org.cubeville.cvvanish;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public final class CVVanish extends JavaPlugin implements Listener {
    private VanishCommand vanishCommand;

    public static Set<UUID> invisible;

    @Override
    public void onEnable() {
        vanishCommand = new VanishCommand(this);
        invisible = new HashSet<>();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player senderPlayer = null;
        //UUID senderId = null;
        if(sender instanceof Player) {
            senderPlayer = (Player) sender;
            //senderId = senderPlayer.getUniqueId();
        }
        if(command.getName().equals("v")) {
            if(senderPlayer == null) return true;
            vanishCommand.onVanishCommand(senderPlayer, args);
            return true;
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(invisible.contains(player.getUniqueId())) {
            Material clickedBlock = event.getClickedBlock().getType();
            if(clickedBlock.equals(Material.CHEST) || clickedBlock.equals(Material.TRAPPED_CHEST)) {
                event.setCancelled(true);

                Chest chest = (Chest) event.getClickedBlock().getState();
                Inventory inventory = Bukkit.createInventory(null, chest.getInventory().getSize(), chest.getInventory().getName());

                inventory.setContents(chest.getInventory().getContents());
                player.openInventory(inventory);
            } else if(event.getAction().equals(Action.PHYSICAL)) {
                if(clickedBlock == Material.WOOD_PLATE || clickedBlock == Material.GOLD_PLATE || clickedBlock == Material.IRON_PLATE || clickedBlock == Material.STONE_PLATE) {
                    event.setCancelled(true);
                    event.setUseInteractedBlock(Event.Result.DENY);
                }
            }
        }
    }
}
