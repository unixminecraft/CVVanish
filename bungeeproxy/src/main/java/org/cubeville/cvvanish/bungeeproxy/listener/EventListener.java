/*
 * CVVanish Copyright (C) 2019 Cubeville
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package org.cubeville.cvvanish.bungeeproxy.listener;

import org.cubeville.cvplayerdata.bungeeproxy.CVPlayerData;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public final class EventListener implements Listener {
    
    private static final String SILENT_JOIN_PERMISSION = "cvvanish.silent.join";
    private static final String SILENT_LEAVE_PERMISSION = "cvvanish.silent.leave";
    private static final String SILENT_VIEW_PERMISSION = "cvvanish.silent.notify";
    
    private final CVVanish vanishPlugin;
    private final CVPlayerData playerDataPlugin;
    private final ProxyServer proxyServer;
    
    public EventListener(final CVVanish vanishPlugin, final CVPlayerData playerDataPlugin) {
        
        this.vanishPlugin = vanishPlugin;
        this.playerDataPlugin = playerDataPlugin;
        this.proxyServer = vanishPlugin.getProxy();
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        
    	final ProxiedPlayer player = event.getPlayer();
        
    	final TextComponent playerNameValue = new TextComponent();
    	final TextComponent leftTheGame = new TextComponent();
        
        playerNameValue.setText(player.getName());
        
        if(player.hasPermission(SILENT_LEAVE_PERMISSION)) {
            
            leftTheGame.setText(" left the game silently.");
            
            playerNameValue.setColor(ChatColor.DARK_AQUA);
            leftTheGame.setColor(ChatColor.DARK_AQUA);
            
            for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                if(onlinePlayer.hasPermission(SILENT_VIEW_PERMISSION)) {
                    onlinePlayer.sendMessage(playerNameValue, leftTheGame);
                }
            }
        }
        else {
            
            leftTheGame.setText(" left the game.");
            
            playerNameValue.setColor(ChatColor.YELLOW);
            leftTheGame.setColor(ChatColor.YELLOW);
            
            for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                onlinePlayer.sendMessage(playerNameValue, leftTheGame);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(final PostLoginEvent event) {
        
    	final ProxiedPlayer player = event.getPlayer();
    	final String playerName = player.getName();
        
        boolean isPlayerNew = playerDataPlugin.isPlayerNew(player);
        boolean canJoinSilently = player.hasPermission(SILENT_JOIN_PERMISSION);
        boolean hasNameChanged = false;
        
        if(!isPlayerNew) {
        	
            hasNameChanged = playerDataPlugin.hasNameChanged(player);
        }
        
        if(isPlayerNew) {
            
            if(canJoinSilently) {
                
            	final TextComponent playerNameValue = new TextComponent();
            	final TextComponent isANewPlayer = new TextComponent();
            	final TextComponent joinedTheGame = new TextComponent();
                
                playerNameValue.setText(playerName);
                isANewPlayer.setText(" is a new player ");
                joinedTheGame.setText("and has joined the game silently.");
                
                playerNameValue.setColor(ChatColor.DARK_AQUA);
                isANewPlayer.setColor(ChatColor.AQUA);
                joinedTheGame.setColor(ChatColor.DARK_AQUA);
                
                for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    if(onlinePlayer.hasPermission(SILENT_VIEW_PERMISSION)) {
                        onlinePlayer.sendMessage(playerNameValue, isANewPlayer, joinedTheGame);
                    }
                }
            }
            else {
                
            	final TextComponent everyoneWelcome = new TextComponent();
            	final TextComponent playerNameValue = new TextComponent();
            	final TextComponent exclamationMark = new TextComponent();
                
                everyoneWelcome.setText("Everyone welcome Cubeville's newest player, ");
                playerNameValue.setText(playerName);
                exclamationMark.setText("!");
                
                everyoneWelcome.setColor(ChatColor.YELLOW);
                playerNameValue.setColor(ChatColor.GOLD);
                exclamationMark.setColor(ChatColor.YELLOW);
                
                for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    onlinePlayer.sendMessage(everyoneWelcome, playerNameValue, exclamationMark);
                }
            }
        }
        else if(hasNameChanged) {
            
        	final String oldPlayerName = playerDataPlugin.changePlayerName(player);
            
            if(canJoinSilently) {
                
            	final TextComponent newPlayerNameValue = new TextComponent();
            	final TextComponent formerlyKnownAs = new TextComponent();
            	final TextComponent oldPlayerNameValue = new TextComponent();
            	final TextComponent closeParenthesis = new TextComponent();
            	final TextComponent joinedTheGame = new TextComponent();
                
                newPlayerNameValue.setText(playerName);
                formerlyKnownAs.setText(" (formerly known as ");
                oldPlayerNameValue.setText(oldPlayerName);
                closeParenthesis.setText(")");
                joinedTheGame.setText(" joined the game silently.");
                
                newPlayerNameValue.setColor(ChatColor.DARK_AQUA);
                formerlyKnownAs.setColor(ChatColor.AQUA);
                oldPlayerNameValue.setColor(ChatColor.DARK_AQUA);
                closeParenthesis.setColor(ChatColor.AQUA);
                joinedTheGame.setColor(ChatColor.DARK_AQUA);
                
                for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    if(onlinePlayer.hasPermission(SILENT_VIEW_PERMISSION)) {
                        onlinePlayer.sendMessage(newPlayerNameValue, formerlyKnownAs, oldPlayerNameValue, closeParenthesis, joinedTheGame);
                    }
                }
            }
            else {
                
            	final TextComponent newPlayerNameValue = new TextComponent();
            	final TextComponent formerlyKnownAs = new TextComponent();
            	final TextComponent oldPlayerNameValue = new TextComponent();
            	final TextComponent joinedTheGame = new TextComponent();
                
                newPlayerNameValue.setText(playerName);
                formerlyKnownAs.setText(" (formerly known as ");
                oldPlayerNameValue.setText(oldPlayerName);
                joinedTheGame.setText(") joined the game silently.");
                
                newPlayerNameValue.setColor(ChatColor.YELLOW);
                formerlyKnownAs.setColor(ChatColor.YELLOW);
                oldPlayerNameValue.setColor(ChatColor.YELLOW);
                joinedTheGame.setColor(ChatColor.YELLOW);
                
                for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    onlinePlayer.sendMessage(newPlayerNameValue, formerlyKnownAs, oldPlayerNameValue, joinedTheGame);
                }
            }
        }
        else {
            if(canJoinSilently) {
                
            	final TextComponent playerNameValue = new TextComponent();
            	final TextComponent joinedTheGame = new TextComponent();
                
                playerNameValue.setText(playerName);
                joinedTheGame.setText(" joined the game silently.");
                
                playerNameValue.setColor(ChatColor.DARK_AQUA);
                joinedTheGame.setColor(ChatColor.DARK_AQUA);
                
                for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    if(onlinePlayer.hasPermission(SILENT_VIEW_PERMISSION)) {
                        onlinePlayer.sendMessage(playerNameValue, joinedTheGame);
                    }
                }
            }
            else {
                
            	final TextComponent playerNameValue = new TextComponent();
            	final TextComponent joinedTheGame = new TextComponent();
                
                playerNameValue.setText(playerName);
                joinedTheGame.setText(" joined the game.");
                
                playerNameValue.setColor(ChatColor.YELLOW);
                joinedTheGame.setColor(ChatColor.YELLOW);
                
                for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
                    onlinePlayer.sendMessage(playerNameValue, joinedTheGame);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyPing(final ProxyPingEvent event) {
        
    	final ServerPing serverPing = event.getResponse();
        int playerCount = 0;
        
        for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
            if(vanishPlugin.isFullyVisible(onlinePlayer.getUniqueId())) {
                playerCount++;
            }
        }
        
        serverPing.getPlayers().setOnline(playerCount);
        serverPing.getPlayers().setSample(new ServerPing.PlayerInfo[0]);
    }
}
