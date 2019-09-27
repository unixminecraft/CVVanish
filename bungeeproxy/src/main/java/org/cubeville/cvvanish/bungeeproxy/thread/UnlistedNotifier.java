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

package org.cubeville.cvvanish.bungeeproxy.thread;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public final class UnlistedNotifier implements Runnable {
	
	private final CVVanish vanishPlugin;
	private final TaskScheduler proxyTaskScheduler;
	
	private ScheduledTask unlistedNotificationScheduledTask;
	
	public UnlistedNotifier(final CVVanish vanishPlugin) {
		
		this.vanishPlugin = vanishPlugin;
		this.proxyTaskScheduler = vanishPlugin.getProxy().getScheduler();
	}
	
	public void start() {
		
		unlistedNotificationScheduledTask = proxyTaskScheduler.schedule(vanishPlugin, this, 0, 2, TimeUnit.SECONDS);
	}
	
	public void stop() {
		
		proxyTaskScheduler.cancel(unlistedNotificationScheduledTask);
	}
	
	@Override
	public void run() {
		
		final HashSet<UUID> unlistedPlayerIds = vanishPlugin.getUnlistedPlayerIds();
		final ProxyServer bungeeProxy = vanishPlugin.getProxy();
		
		for(final UUID unlistedPlayerId : unlistedPlayerIds) {
			
			final ProxiedPlayer unlistedPlayer = bungeeProxy.getPlayer(unlistedPlayerId);
			if(unlistedPlayer == null) {
				
				continue;
			}
			
			final TextComponent youAreLocallyVisible = new TextComponent();
			
			youAreLocallyVisible.setText("You are able to be seen, but you are not listed in tab.");
			youAreLocallyVisible.setColor(ChatColor.GOLD);
			
			unlistedPlayer.sendMessage(ChatMessageType.ACTION_BAR, youAreLocallyVisible);
		}
	}
}
