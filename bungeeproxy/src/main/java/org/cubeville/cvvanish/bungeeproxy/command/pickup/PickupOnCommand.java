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

package org.cubeville.cvvanish.bungeeproxy.command.pickup;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.common.bungeecord.command.SubPlayerCommand;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class PickupOnCommand extends SubPlayerCommand {
	
	private static final String SYNTAX = "&cSyntax: /vpickup on&r";
	
	private final CVVanish vanishPlugin;
	
	private final Logger logger;
	
	public PickupOnCommand(CVVanish vanishPlugin) {
		
		super("on", CVVanish.PERMISSION_PICKUP_ON_USE, convertText(SYNTAX));
		
		this.vanishPlugin = vanishPlugin;
		
		this.logger = vanishPlugin.getLogger();
	}
	
	@Override
	public void execute(final ProxiedPlayer player, final Set<String> flags, final Map<String, String> parameters, final List<String> baseParameters) {
		
		final String logHeader = getClass().getSimpleName() + " (" + player.getName() + ") :";
		logger.log(Level.INFO, logHeader + "Execution starting.");
		
		final UUID playerId = player.getUniqueId();
		final boolean pickupEnabled = vanishPlugin.isPickupEnabled(playerId);
		
		if(pickupEnabled) {
			
			final TextComponent pickupAlreadyEnabled = new TextComponent();
			
			pickupAlreadyEnabled.setText("You already have item pickup enabled, you can't enable it more.");
			pickupAlreadyEnabled.setColor(ChatColor.RED);
			
			player.sendMessage(pickupAlreadyEnabled);
			return;
		}
		
		if(!vanishPlugin.enablePickup(playerId)) {
			
			logger.log(Level.INFO, "Attempted to enable pickup for player " + player.getName() + " (UUID: " + playerId.toString() + "), but they already had pickup enabled, even after checking to make sure they did not have pickup enabled.");
			
			player.sendMessage(getInternalError());
			return;
		}
		
		final TextComponent itemPickupEnabled = new TextComponent();
		
		itemPickupEnabled.setText("Item pickup enabled.");
		itemPickupEnabled.setColor(ChatColor.GREEN);
		
		player.sendMessage(itemPickupEnabled);
	}
}
