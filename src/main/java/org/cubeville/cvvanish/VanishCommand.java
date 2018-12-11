package org.cubeville.cvvanish;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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
                removePlayer(plugin, player);
            } else {
                addPlayer(plugin, player);
            }
        } else if(args.length == 1 && (args[0].equals("fq") || args[0].equals("fj"))) {
            if (args[0].equals("fq")) {
                if (CVVanish.invisible.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "You must be unvanished to fakequit!");
                } else {
                    addPlayer(plugin, player);
                    //TODO broadcast "player" left the game.
                }
            } else { //fj
                if (CVVanish.invisible.contains(player.getUniqueId())) {
                    removePlayer(plugin, player);
                    //TODO broadcast "player" joined the game.
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You must be vanished to fakejoin!");
                }
            }
        }
    }

    public void addPlayer(CVVanish plugin, Player player) {
        CVVanish.invisible.add(player.getUniqueId());
        player.hidePlayer(plugin, player);
        player.sendMessage(ChatColor.AQUA + "You have vanished!");
    }

    public void removePlayer(CVVanish plugin, Player player) {
        CVVanish.invisible.remove(player.getUniqueId());
        player.showPlayer(plugin, player);
        player.sendMessage(ChatColor.AQUA + "You have unvanished!");
    }
}
