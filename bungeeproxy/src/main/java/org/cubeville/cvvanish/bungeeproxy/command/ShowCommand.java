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

public final class ShowCommand extends PlayerCommand {
	
	private static final String SYNTAX = "&cSyntax: /show [fj]&r";
	
	private final CVVanish vanishPlugin;
	
	private final Logger logger;
	
	public ShowCommand(final CVVanish vanishPlugin) {
		
		super("show", CVVanish.PERMISSION_SHOW_USE, convertText(SYNTAX));
		
		addFlag("fj");
		
		this.vanishPlugin = vanishPlugin;
		
		this.logger = vanishPlugin.getLogger();
	}
	
	@Override
	public void execute(final ProxiedPlayer player, final Set<String> flags, final Map<String, String> parameters, final List<String> baseParameters) {
		
		final String logHeader = getClass().getSimpleName() + " (" + player.getName() + ") :";
		logger.log(Level.INFO, logHeader + "Execution starting.");
		
		final boolean fakeJoin = flags.contains("fj");
		
		final UUID playerId = player.getUniqueId();
		if(vanishPlugin.isFullyVisible(playerId)) {
			
			final TextComponent youCantBecomeMoreVisible = new TextComponent();
			
			youCantBecomeMoreVisible.setText("No. We won't make you more visible than you already are. It's impossible.");
			youCantBecomeMoreVisible.setColor(ChatColor.RED);
			
			player.sendMessage(youCantBecomeMoreVisible);
			return;
		}
		
		if(!vanishPlugin.show(playerId)) {
			
			logger.log(Level.INFO, "Attempted to show player " + player.getName() + " (UUID: " + playerId.toString() + "), but they were already visible, even after checking to make sure they were not visible.");
			
			player.sendMessage(getInternalError());
			return;
		}
		
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
		final String formattedTimeNow = simpleDateFormat.format(new Date(System.currentTimeMillis()));
		
		final TextComponent youAreNoLongerHidden = new TextComponent();
		final TextComponent timeNowValue = new TextComponent();
		final TextComponent vanishNotification = new TextComponent();
		final TextComponent playerNameValue = new TextComponent();
		final TextComponent isNowFullyVisible = new TextComponent();
		
		youAreNoLongerHidden.setText("You are no longer hidden. ");
		timeNowValue.setText(formattedTimeNow);
		vanishNotification.setText("[CVVanish] ");
		playerNameValue.setText(player.getName());
		isNowFullyVisible.setText(" is now fully visible.");
		
		youAreNoLongerHidden.setColor(ChatColor.GREEN);
		timeNowValue.setColor(ChatColor.GREEN);
		vanishNotification.setColor(ChatColor.DARK_AQUA);
		playerNameValue.setColor(ChatColor.GOLD);
		isNowFullyVisible.setColor(ChatColor.DARK_AQUA);
		
		player.sendMessage(youAreNoLongerHidden, timeNowValue);
		
		final ProxyServer proxyServer = vanishPlugin.getProxy();
		for(final ProxiedPlayer onlinePlayer : proxyServer.getPlayers()) {
			
			if(onlinePlayer.hasPermission(CVVanish.PERMISSION_SHOW_NOTIFY)) {
				onlinePlayer.sendMessage(vanishNotification, playerNameValue, isNowFullyVisible);
			}
		}
		
		if(!fakeJoin) {
			return;
		}
		
		proxyServer.getPluginManager().dispatchCommand(player, "fj");
	}
}
