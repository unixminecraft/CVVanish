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

public final class VisibilityOnCommand extends PlayerCommand {
	
	private static final String SYNTAX = "&cSyntax: /von&r";
	
	private final CVVanish vanishPlugin;
	
	private final Logger logger;
	
	public VisibilityOnCommand(final CVVanish vanishPlugin) {
		
		super("von", CVVanish.PERMISSION_VISIBILITYON_USE, convertText(SYNTAX));
		
		this.vanishPlugin = vanishPlugin;
		
		this.logger = vanishPlugin.getLogger();
	}
	
	@Override
	public void execute(final ProxiedPlayer player, final Set<String> flags, final Map<String, String> parameters, final List<String> baseParameters) {
		
		final String logHeader = getClass().getSimpleName() + " (" + player.getName() + ") :";
		logger.log(Level.INFO, logHeader + "Execution starting.");
		
		UUID playerId = player.getUniqueId();
		if(vanishPlugin.isFullyVisible(playerId) || vanishPlugin.isUnlisted(playerId)) {
			
			final TextComponent youAreVisibleAlready = new TextComponent();
			
			youAreVisibleAlready.setText("You are already able to be seen.");
			youAreVisibleAlready.setColor(ChatColor.RED);
			
			player.sendMessage(youAreVisibleAlready);
			return;
		}
		
		if(!vanishPlugin.unvanish(playerId)) {
			
			logger.log(Level.INFO, "Attempted to unvanish player " + player.getName() + " (UUID: " + playerId.toString() + "), but they were already unvanished, even after checking to make sure they were not unvanished.");
			
			player.sendMessage(getInternalError());
			return;
		}
		
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
		final String formattedTimeNow = simpleDateFormat.format(new Date(System.currentTimeMillis()));
		
		final TextComponent youAreNow = new TextComponent();
		final TextComponent hiddenStatusValue = new TextComponent();
		final TextComponent period = new TextComponent();
		final TextComponent timeNowValue = new TextComponent();
		final TextComponent vanishNotification = new TextComponent();
		final TextComponent playerNameValue = new TextComponent();
		final TextComponent isNow = new TextComponent();
		
		youAreNow.setText("You are now ");
		period.setText(". ");
		timeNowValue.setText(formattedTimeNow);
		vanishNotification.setText("[CVVanish] ");
		playerNameValue.setText(player.getName());
		isNow.setText(" is now ");
		
		youAreNow.setColor(ChatColor.GREEN);
		period.setColor(ChatColor.GREEN);
		timeNowValue.setColor(ChatColor.GREEN);
		vanishNotification.setColor(ChatColor.DARK_AQUA);
		playerNameValue.setColor(ChatColor.GOLD);
		isNow.setColor(ChatColor.DARK_AQUA);
		
		final boolean fullyVisible = vanishPlugin.isFullyVisible(playerId);
		final boolean unlisted = vanishPlugin.isUnlisted(playerId);
		
		final ProxyServer proxy = vanishPlugin.getProxy();
		
		if(fullyVisible) {
			
			hiddenStatusValue.setText("fully visible");
			hiddenStatusValue.setColor(ChatColor.GREEN);
			
			player.sendMessage(youAreNow, hiddenStatusValue, period, timeNowValue);
			
			hiddenStatusValue.setColor(ChatColor.DARK_AQUA);
			period.setColor(ChatColor.DARK_AQUA);
			
			for(ProxiedPlayer onlinePlayer : proxy.getPlayers()) {
				
				if(onlinePlayer.hasPermission(CVVanish.PERMISSION_VISIBILITYON_NOTIFY)) {
					onlinePlayer.sendMessage(vanishNotification, playerNameValue, isNow, hiddenStatusValue, period);
				}
			}
		}
		else if(unlisted) {
			
			hiddenStatusValue.setText("able to be seen, but not listed in tab");
			hiddenStatusValue.setColor(ChatColor.GREEN);
			
			player.sendMessage(youAreNow, hiddenStatusValue, period, timeNowValue);
			
			hiddenStatusValue.setColor(ChatColor.DARK_AQUA);
			period.setColor(ChatColor.DARK_AQUA);
			
			for(ProxiedPlayer onlinePlayer : proxy.getPlayers()) {
				
				if(onlinePlayer.hasPermission(CVVanish.PERMISSION_VISIBILITYON_NOTIFY)) {
					onlinePlayer.sendMessage(vanishNotification, playerNameValue, isNow, hiddenStatusValue, period);
				}
			}
		}
		else {
			
			logger.log(Level.INFO, "Player " + player.getName() + " (UUID: " + playerId.toString() + ") is not fully visible nor unlisted after using the visibility-on command.");
			
			final TextComponent unlikelyErrorOccurred = new TextComponent();
			
			unlikelyErrorOccurred.setText("An unlikely error has occurred. Please report this to a server administrator so they can look into it.");
			unlikelyErrorOccurred.setColor(ChatColor.RED);
			
			player.sendMessage(unlikelyErrorOccurred);
		}
	}
}
