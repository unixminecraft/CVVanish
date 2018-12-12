package org.cubeville.cvvanish;

import java.util.HashSet;

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

        if(args.length == 0) {
            if(CVVanish.invisible.contains(player.getUniqueId())) {
                CVVanish.invisible.remove(player.getUniqueId());
                unvanishPlayer(player);
                player.sendMessage(ChatColor.AQUA + "You have unvanished!");
            } else {
                CVVanish.invisible.add(player.getUniqueId());
                vanishPlayer(player);
                player.sendMessage(ChatColor.AQUA + "You have vanished!");
            }
        }
    }

    public void vanishPlayer(Player player) {
        for(Player p : getServer().getOnlinePlayers()) {
            p.hidePlayer(plugin, player);
        }
        player.setCollidable(false);
    }

    public void unvanishPlayer(Player player) {
        for(Player p : getServer().getOnlinePlayers()) {
            p.showPlayer(plugin, player);
        }
        player.setCollidable(true);
    }
}
