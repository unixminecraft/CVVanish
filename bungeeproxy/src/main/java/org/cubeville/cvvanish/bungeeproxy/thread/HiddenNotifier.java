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

public final class HiddenNotifier implements Runnable {
	
	private final CVVanish vanishPlugin;
	private final TaskScheduler proxyTaskScheduler;
	
	private ScheduledTask hiddenNotificationScheduledTask;
	
	public HiddenNotifier(final CVVanish vanishPlugin) {
		
		this.vanishPlugin = vanishPlugin;
		proxyTaskScheduler = vanishPlugin.getProxy().getScheduler();
	}
	
	public void start() {
		
		hiddenNotificationScheduledTask = proxyTaskScheduler.schedule(vanishPlugin, this, 0, 2, TimeUnit.SECONDS);
	}
	
	public void stop() {
		
		proxyTaskScheduler.cancel(hiddenNotificationScheduledTask);
	}
	
	@Override
	public void run() {
		
		final HashSet<UUID> hiddenPlayerIds = vanishPlugin.getHiddenPlayerIds();
		final ProxyServer bungeeProxy = vanishPlugin.getProxy();
		
		for(final UUID hiddenPlayerId : hiddenPlayerIds) {
			
			final ProxiedPlayer hiddenPlayer = bungeeProxy.getPlayer(hiddenPlayerId);
			if(hiddenPlayer == null) {
				
				continue;
			}
			
			final TextComponent youAreInvisible = new TextComponent();
			
			youAreInvisible.setText("You are invisible to other players.");
			youAreInvisible.setColor(ChatColor.GREEN);
			
			hiddenPlayer.sendMessage(ChatMessageType.ACTION_BAR, youAreInvisible);
		}
	}
}
