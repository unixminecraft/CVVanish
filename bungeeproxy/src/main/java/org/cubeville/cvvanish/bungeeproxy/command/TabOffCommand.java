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

public final class TabOffCommand extends PlayerCommand {
	
	private static final String SYNTAX = "&cSyntax: /toff&r";
	
	private final CVVanish vanishPlugin;
	
	private final Logger logger;
	
	public TabOffCommand(final CVVanish vanishPlugin) {
		
		super("toff", CVVanish.PERMISSION_TABOFF_USE, convertText(SYNTAX));
		
		this.vanishPlugin = vanishPlugin;
		
		this.logger = vanishPlugin.getLogger();
	}
	
	@Override
	public void execute(final ProxiedPlayer player, final Set<String> flags, final Map<String, String> parameters, final List<String> baseParameters) {
		
		final String logHeader = getClass().getSimpleName() + " (" + player.getName() + ") :";
		logger.log(Level.INFO, logHeader + "Execution starting.");
		
		UUID playerId = player.getUniqueId();
		if(vanishPlugin.isHidden(playerId) || vanishPlugin.isUnlisted(playerId)) {
			
			final TextComponent youAreAlreadyHiddenFromTab = new TextComponent();
			
			youAreAlreadyHiddenFromTab.setText("You are already hidden from tab.");
			youAreAlreadyHiddenFromTab.setColor(ChatColor.RED);
			
			player.sendMessage(youAreAlreadyHiddenFromTab);
			return;
		}
		
		if(!vanishPlugin.unlist(playerId)) {
			
			logger.log(Level.INFO, "Attempted to tab hide player " + player.getName() + " (UUID: " + playerId.toString() + "), but they were already hidden from tab, even after checking to make sure they were not hidden from tab.");
			
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
		
		final boolean hidden = vanishPlugin.isHidden(playerId);
		final boolean unlisted = vanishPlugin.isUnlisted(playerId);
		
		final ProxyServer proxy = vanishPlugin.getProxy();
		
		if(hidden) {
			
			hiddenStatusValue.setText("fully hidden");
			hiddenStatusValue.setColor(ChatColor.GREEN);
			
			player.sendMessage(youAreNow, hiddenStatusValue, period, timeNowValue);
			
			hiddenStatusValue.setColor(ChatColor.DARK_AQUA);
			period.setColor(ChatColor.DARK_AQUA);
			
			for(final ProxiedPlayer onlinePlayer : proxy.getPlayers()) {
				
				if(onlinePlayer.hasPermission(CVVanish.PERMISSION_TABOFF_NOTIFY)) {
					onlinePlayer.sendMessage(vanishNotification, playerNameValue, isNow, hiddenStatusValue, period);
				}
			}
		}
		else if(unlisted) {
			
			hiddenStatusValue.setText("not listed in tab, but visible");
			hiddenStatusValue.setColor(ChatColor.GREEN);
			
			player.sendMessage(youAreNow, hiddenStatusValue, period, timeNowValue);
			
			hiddenStatusValue.setColor(ChatColor.DARK_AQUA);
			period.setColor(ChatColor.DARK_AQUA);
			
			for(final ProxiedPlayer onlinePlayer : proxy.getPlayers()) {
				
				if(onlinePlayer.hasPermission(CVVanish.PERMISSION_TABOFF_NOTIFY)) {
					onlinePlayer.sendMessage(vanishNotification, playerNameValue, isNow, hiddenStatusValue, period);
				}
			}
		}
		else {
			
			logger.log(Level.INFO, "Player " + player.getName() + " (UUID: " + playerId.toString() + ") is not hidden nor unlisted after using the tab-off command.");
			
			final TextComponent unlikelyErrorOccurred = new TextComponent();
			
			unlikelyErrorOccurred.setText("An unlikely error has occurred. Please report this to a server administrator so they can look into it.");
			unlikelyErrorOccurred.setColor(ChatColor.RED);
			
			player.sendMessage(unlikelyErrorOccurred);
		}
	}
}
