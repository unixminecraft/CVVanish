package org.cubeville.cvvanish.bungeeproxy.listener;

import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class EventListener implements Listener {
    
    private static final String SILENT_VIEW_PERMISSION = "cvvanish.silent.notify";
    
    private CVVanish vanishPlugin;
    
    public EventListener(CVVanish vanishPlugin) {
        
        this.vanishPlugin = vanishPlugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent playerDisconnectEvent) {
        
        ProxiedPlayer disconnectingPlayer = playerDisconnectEvent.getPlayer();
        
        TextComponent playerNameValue = new TextComponent();
        playerNameValue.setText(disconnectingPlayer.getName());
        
        if(disconnectingPlayer.hasPermission("cvvanish.silent.leave")) {
            
            playerNameValue.setColor(ChatColor.DARK_AQUA);
            
            TextComponent leftSilently = new TextComponent();
            leftSilently.setText(" left the game silently.");
            leftSilently.setColor(ChatColor.DARK_AQUA);
            
            vanishPlugin.sendMessageWithPermission(SILENT_VIEW_PERMISSION, playerNameValue, leftSilently);
        }
        else {
            
            playerNameValue.setColor(ChatColor.YELLOW);
            
            TextComponent left = new TextComponent();
            left.setText(" left the game.");
            left.setColor(ChatColor.YELLOW);
            
            vanishPlugin.sendMessageAll(playerNameValue, left);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLoginEvent(PostLoginEvent postLoginEvent) {
        
        ProxiedPlayer loggedInPlayer = postLoginEvent.getPlayer();
        
        TextComponent playerNameValue = new TextComponent();
        playerNameValue.setText(loggedInPlayer.getName());
        
        if(loggedInPlayer.hasPermission("cvvanish.silent.join")) {
            
            playerNameValue.setColor(ChatColor.DARK_AQUA);
            
            TextComponent joinedSilently = new TextComponent();
            joinedSilently.setText(" joined the game silently.");
            joinedSilently.setColor(ChatColor.DARK_AQUA);
            
            vanishPlugin.vanishPlayer(loggedInPlayer.getUniqueId());
            vanishPlugin.sendMessageWithPermission(SILENT_VIEW_PERMISSION, playerNameValue, joinedSilently);
        }
        else {
            
            playerNameValue.setColor(ChatColor.YELLOW);
            
            TextComponent joined = new TextComponent();
            joined.setText(" joined the game.");
            joined.setColor(ChatColor.YELLOW);
            
            vanishPlugin.sendMessageAll(playerNameValue, joined);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyPingEvent(ProxyPingEvent proxyPingEvent) {
        
        ServerPing serverPing = proxyPingEvent.getResponse();
        int playerCount = 0;
        
        for(ProxiedPlayer onlinePlayer : vanishPlugin.getProxy().getPlayers()) {
            if(vanishPlugin.isPlayerFullyVisible(onlinePlayer.getUniqueId())) {
                playerCount++;
            }
        }
        
        serverPing.getPlayers().setOnline(playerCount);
        serverPing.getPlayers().setSample(new ServerPing.PlayerInfo[0]);
    }
}
