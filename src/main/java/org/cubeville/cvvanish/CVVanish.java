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
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
    public static Set<UUID> silentChest;
    public static Set<UUID> cantPickup;

    @Override
    public void onEnable() {
        vanishCommand = new VanishCommand(this);
        invisible = new HashSet<>();
        silentChest = new HashSet<>();
        cantPickup = new HashSet<>();
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
        UUID playerId = player.getUniqueId();
        if(invisible.contains(playerId) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Material clickedBlock = event.getClickedBlock().getType();
            if ((clickedBlock.equals(Material.CHEST) || clickedBlock.equals(Material.TRAPPED_CHEST)) && silentChest.contains(playerId)) {
                event.setCancelled(true);

                Chest chest = (Chest) event.getClickedBlock().getState();
                Inventory inventory = Bukkit.createInventory(null, chest.getInventory().getSize(), chest.getInventory().getName());

                inventory.setContents(chest.getInventory().getContents());
                player.openInventory(inventory);
            } else if(event.getAction().equals(Action.PHYSICAL)) {
                //if(clickedBlock == Material.WOOD_PLATE || clickedBlock == Material.GOLD_PLATE || clickedBlock == Material.IRON_PLATE || clickedBlock == Material.STONE_PLATE) {
                    event.setCancelled(true);
                    event.setUseInteractedBlock(Event.Result.DENY);
                //}
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityTarget(EntityTargetEvent event) {
        if(!(event.getTarget() instanceof Player)) return;
        Player player = (Player) event.getTarget();
        UUID playerId = player.getUniqueId();
        if(invisible.contains(playerId)) {
            event.setCancelled(true);
        }
    }

    //Needs Testing
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { //hide already invisible players from new joining players
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        for(Player p : getServer().getOnlinePlayers()) {
            if(invisible.contains(p.getUniqueId())) {
                player.hidePlayer(this, p); //player won't be able to see p
            }
        }
    }

    //TODO Test what happens when a regular player quits while someone is vanished,
    // and the regular player logs back in after the vanished player unvanishes.
}
