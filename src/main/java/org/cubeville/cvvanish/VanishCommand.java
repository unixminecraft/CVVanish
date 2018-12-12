package org.cubeville.cvvanish;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;


public class VanishCommand {
    CVVanish plugin;

    public VanishCommand(CVVanish plugin) {
        this.plugin = plugin;
    }

    public void onVanishCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if(args.length == 0) {
            if(CVVanish.invisible.contains(playerId)) { //unvanishing
                unvanishPlayer(player, playerId);
            } else { //vanishing
                vanishPlayer(player, playerId);
            }
        } else if(args.length == 1 && args[0].equals("silentchest")) {
            if(CVVanish.invisible.contains(playerId)) {
                if(CVVanish.silentChest.contains(playerId)) {
                    CVVanish.silentChest.remove(playerId);
                    player.sendMessage(ChatColor.YELLOW + "[Vanish] Silent chests disabled!");
                } else {
                    CVVanish.silentChest.add(playerId);
                    player.sendMessage(ChatColor.AQUA + "[Vanish] Silent chests enabled!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "[Vanish] You must be vanished to toggle silent chests!");
            }
        } else if(args.length == 1 && args[0].equals("pickup")) {
            if(CVVanish.invisible.contains(playerId)) {
                if(CVVanish.cantPickup.contains(playerId)) {
                    CVVanish.cantPickup.remove(playerId);
                    player.setCanPickupItems(true);
                    player.sendMessage(ChatColor.YELLOW + "[Vanish] Item pickup enabled!");
                } else {
                    CVVanish.cantPickup.add(playerId);
                    player.setCanPickupItems(false);
                    player.sendMessage(ChatColor.AQUA + "[Vanish] Item pickup disabled!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "[Vanish] You must be vanished to toggle item pickup!");
            }
        }
    }

    public void vanishPlayer(Player player, UUID playerId) {
        if(!CVVanish.invisible.contains(playerId))CVVanish.invisible.add(playerId);
        if(!CVVanish.cantPickup.contains(playerId)) CVVanish.cantPickup.add(playerId);
        if(!CVVanish.silentChest.contains(playerId)) CVVanish.silentChest.add(playerId);
        for(Player p : getServer().getOnlinePlayers()) {
            p.hidePlayer(plugin, player);
        }
        player.setCollidable(false);
        player.setCanPickupItems(false);
        player.sendMessage(ChatColor.AQUA + "[Vanish] You have vanished!");
    }

    public void unvanishPlayer(Player player, UUID playerId) {
        if(CVVanish.invisible.contains(playerId))CVVanish.invisible.remove(playerId);
        if(CVVanish.cantPickup.contains(playerId)) CVVanish.cantPickup.remove(playerId);
        if(CVVanish.silentChest.contains(playerId)) CVVanish.silentChest.remove(playerId);
        for(Player p : getServer().getOnlinePlayers()) {
            p.showPlayer(plugin, player);
        }
        player.setCollidable(true);
        player.setCanPickupItems(true);
        player.sendMessage(ChatColor.AQUA + "[Vanish] You have unvanished!");
    }
}
