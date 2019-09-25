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

import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public final class EventListener implements Listener {
    
	private static final String INVISIBLE_JOIN_PERMISSION = "cvvanish.invisible.join";
    
    private final CVVanish vanishPlugin;
    
    private final ProxyServer proxyServer;
    
    public EventListener(final CVVanish vanishPlugin) {
        
        this.vanishPlugin = vanishPlugin;
        
        this.proxyServer = vanishPlugin.getProxy();
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        
    	final ProxiedPlayer player = event.getPlayer();
        
    	if(player.hasPermission(INVISIBLE_JOIN_PERMISSION)) {
    		
    		vanishPlugin.hide(player.getUniqueId());
    	}
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(final PostLoginEvent event) {
        
    	final ProxiedPlayer player = event.getPlayer();
    	
    	if(player.hasPermission(INVISIBLE_JOIN_PERMISSION)) {
    		
    		vanishPlugin.hide(player.getUniqueId());
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
