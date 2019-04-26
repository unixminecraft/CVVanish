package org.cubeville.vanish.bukkit.listener;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.cubeville.vanish.bukkit.CVVanish;

public class EventListener implements Listener {

    private CVVanish plugin;
    
    public EventListener(CVVanish plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Set<UUID> invisibleIDs = plugin.getCompleteInvisiblePlayers();
        Player player = event.getPlayer();
        Server server = plugin.getServer();
        Collection<Player> onlinePlayers = (Collection<Player>) server.getOnlinePlayers();
        if(invisibleIDs.contains(player.getUniqueId())) {
            for(Player onlinePlayer: onlinePlayers) {
                if(!player.getUniqueId().equals(onlinePlayer.getUniqueId())) {
                    if(!onlinePlayer.hasPermission("cvvanish.see")) {
                        onlinePlayer.hidePlayer(plugin, player);
                    }
                }
            }
        }
        if(!player.hasPermission("cvvanish.see")) {
            for(UUID playerID: invisibleIDs) {
                Player onlinePlayer = server.getPlayer(playerID);
                player.hidePlayer(plugin, onlinePlayer);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        
    }
}
