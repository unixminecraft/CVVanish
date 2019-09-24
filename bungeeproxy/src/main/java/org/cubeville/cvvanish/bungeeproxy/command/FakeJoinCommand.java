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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.common.bungeecord.command.PlayerCommand;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class FakeJoinCommand extends PlayerCommand {
    
	private static final String SYNTAX = "&cSyntax: /fj&r";
	
    private static final String USE_PERMISSION = "cvvanish.fakejoin.use";
    private static final String NOTIFY_PERMISSION = "cvvanish.fakejoin.notify";
    
    private final CVVanish vanishPlugin;
    
    private final Logger logger;
    
    public FakeJoinCommand(final CVVanish vanishPlugin) {
        
    	super("fj", USE_PERMISSION, convertSyntax(SYNTAX));
    	
    	this.vanishPlugin = vanishPlugin;
    	
    	this.logger = vanishPlugin.getLogger();
    }

    @Override
    public void execute(ProxiedPlayer player, Set<String> flags, Map<String, String> parameters, List<String> baseParameters) {
        
		final String logHeader = getClass().getSimpleName() + " (" + player.getName() + ") :";
		logger.log(Level.INFO, logHeader + "Execution starting.");
    	
		final TextComponent playerNameValueAll = new TextComponent();
		final TextComponent joinedTheGame = new TextComponent();
		final TextComponent vanishNotification = new TextComponent();
		final TextComponent playerNameValueNotify = new TextComponent();
		final TextComponent wasAlreadyHere = new TextComponent();
        
        playerNameValueAll.setText(player.getName());
        joinedTheGame.setText(" joined the game.");
        vanishNotification.setText("[CVVanish] ");
        playerNameValueNotify.setText(player.getName());
        wasAlreadyHere.setText(" was already here, they acutally joined earlier.");
        
        playerNameValueAll.setColor(ChatColor.YELLOW);
        joinedTheGame.setColor(ChatColor.YELLOW);
        vanishNotification.setColor(ChatColor.DARK_AQUA);
        playerNameValueNotify.setColor(ChatColor.GOLD);
        wasAlreadyHere.setColor(ChatColor.DARK_AQUA);
        
        for(final ProxiedPlayer onlinePlayer : vanishPlugin.getProxy().getPlayers()) {
            
            onlinePlayer.sendMessage(playerNameValueAll, joinedTheGame);
            
            if(onlinePlayer.hasPermission(NOTIFY_PERMISSION)) {
                onlinePlayer.sendMessage(vanishNotification, playerNameValueNotify, wasAlreadyHere);
            }
        }
    }
}
