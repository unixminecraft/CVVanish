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

package org.cubeville.cvvanish.bungeeproxy.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.common.bungeecord.command.PlayerCommand;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class HideCommand extends PlayerCommand {
    
	private static final String SYNTAX = "&cSyntax: /hide [fq]&r";
	
    private static final String USE_PERMISSION = "cvvanish.hide.use";
    private static final String NOTIFY_PERMISSION = "cvvanish.hide.notify";
    
    private final CVVanish vanishPlugin;
    
    private final Logger logger;
    
    public HideCommand(final CVVanish vanishPlugin) {
    	
    	super("hide", USE_PERMISSION, convertSyntax(SYNTAX));
    	
    	addFlag("fq");
    	
    	this.vanishPlugin = vanishPlugin;
    	
    	this.logger = vanishPlugin.getLogger();
    }
    
    @Override
    public void execute(final ProxiedPlayer player, final Set<String> flags, final Map<String, String> parameters, final List<String> baseParameters) {
        
		final String logHeader = getClass().getSimpleName() + " (" + player.getName() + ") :";
		logger.log(Level.INFO, logHeader + "Execution starting.");
    	
        final boolean fakeQuit = flags.contains("fq");
        
        final UUID playerId = player.getUniqueId();
        if(vanishPlugin.isHidden(playerId)) {
            
            final TextComponent youCantHideMore = new TextComponent();
            
            youCantHideMore.setText("You can't be any more hidden than you already are.");
            youCantHideMore.setColor(ChatColor.RED);
            
            player.sendMessage(youCantHideMore);
            return;
        }
        
        if(!vanishPlugin.hide(playerId)) {
            
            logger.log(Level.INFO, "Attempted to hide player " + player.getName() + " (UUID: " + playerId.toString() + "), but they were already hidden, even after checking to make sure they were not hidden.");
            
            player.sendMessage(getInternalError());
            return;
        }
        
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
        final String formattedTimeNow = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        
        final TextComponent youAreHidden = new TextComponent();
        final TextComponent timeNowValue = new TextComponent();
        final TextComponent vanishNotification = new TextComponent();
        final TextComponent playerNameValue = new TextComponent();
        final TextComponent isNowHidden = new TextComponent();
        
        youAreHidden.setText("You are hidden. ");
        timeNowValue.setText(formattedTimeNow);
        vanishNotification.setText("[CVVanish] ");
        playerNameValue.setText(player.getName());
        isNowHidden.setText(" is now fully hidden.");
        
        youAreHidden.setColor(ChatColor.GREEN);
        timeNowValue.setColor(ChatColor.GREEN);
        vanishNotification.setColor(ChatColor.DARK_AQUA);
        playerNameValue.setColor(ChatColor.GOLD);
        isNowHidden.setColor(ChatColor.DARK_AQUA);
        
        player.sendMessage(youAreHidden, timeNowValue);
        
        final ProxyServer proxyServer = vanishPlugin.getProxy();
        for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
        	
            if(onlinePlayer.hasPermission(NOTIFY_PERMISSION)) {
                onlinePlayer.sendMessage(vanishNotification, playerNameValue, isNowHidden);
            }
        }
        
        if(!fakeQuit) {
            return;
        }
        
        proxyServer.getPluginManager().dispatchCommand(player, "fq");
    }
}
