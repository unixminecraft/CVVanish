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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.common.bungeecord.command.PlayerCommand;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class PickupCommand extends PlayerCommand {
    
	private static final String SYNTAX = "&cSyntax: /vpickup [on|off]&r";
	
    private static final String USE_PERMISSION = "cvvanish.pickup.use";
    
    private final CVVanish vanishPlugin;
    
    private final Logger logger;
    
    public PickupCommand(final CVVanish vanishPlugin) {
        
    	super("vpickup", USE_PERMISSION, convertSyntax(SYNTAX));
    	
    	addFlag("on");
    	addFlag("off");
    	
    	this.vanishPlugin = vanishPlugin;
    	
    	this.logger = vanishPlugin.getLogger();
    }
    
    @Override
    public void execute(final ProxiedPlayer player, final Set<String> flags, final Map<String, String> parameters, final List<String> baseParameters) {
        
		final String logHeader = getClass().getSimpleName() + " (" + player.getName() + ") :";
		logger.log(Level.INFO, logHeader + "Execution starting.");
    	
    	final UUID playerId = player.getUniqueId();
    	final boolean pickupEnabled = vanishPlugin.isPickupEnabled(playerId);
        
        if(flags.size() == 0) {
            
        	final TextComponent pickingUpItems = new TextComponent();
        	final TextComponent pickupStatusValue = new TextComponent();
        	final TextComponent period = new TextComponent();
        	final TextComponent toTurnIt = new TextComponent();
        	final TextComponent toggleValue = new TextComponent();
        	final TextComponent use = new TextComponent();
        	final TextComponent toggleCommandValue = new TextComponent();
            
            pickingUpItems.setText("Picking up items is currently ");
            period.setText(".");
            toTurnIt.setText("To turn it ");
            use.setText(", use ");
            
            pickingUpItems.setColor(ChatColor.YELLOW);
            period.setColor(ChatColor.YELLOW);
            toTurnIt.setColor(ChatColor.YELLOW);
            use.setColor(ChatColor.YELLOW);
            toggleCommandValue.setColor(ChatColor.AQUA);
            
            if(pickupEnabled) {
                
                pickupStatusValue.setText("ENABLED");
                toggleValue.setText("off");
                toggleCommandValue.setText("/vpickup off");
                
                pickupStatusValue.setColor(ChatColor.GREEN);
                toggleValue.setColor(ChatColor.RED);
            }
            else {
                
                pickupStatusValue.setText("DISABLED");
                toggleValue.setText("on");
                toggleCommandValue.setText("/vpickup on");
                
                pickupStatusValue.setColor(ChatColor.RED);
                toggleValue.setColor(ChatColor.GREEN);
            }
            
            player.sendMessage(pickingUpItems, pickupStatusValue, period);
            player.sendMessage(toTurnIt, toggleValue, use, toggleCommandValue, period);
        }
        else if(flags.size() == 1) {
            
            if(flags.contains("on")) {
            	
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
            else if(flags.contains("off")) {
            	
                if(!pickupEnabled) {
                    
                	final TextComponent pickupAlreadyDisabled = new TextComponent();
                    
                    pickupAlreadyDisabled.setText("You don't have item pickup enabled, disabling more would probably cause you to drop your items.");
                    pickupAlreadyDisabled.setColor(ChatColor.RED);
                    
                    player.sendMessage(pickupAlreadyDisabled);
                    return;
                }
                
                if(!vanishPlugin.disablePickup(playerId)) {
                    
                    logger.log(Level.INFO, "Attempted to disable pickup for player " + player.getName() + " (UUID: " + playerId.toString() + "), but they already had pickup disabled, even after checking to make sure they did not have pickup disabled.");
                    
                    player.sendMessage(getInternalError());
                    return;
                }
                
                final TextComponent itemPickupDisabled = new TextComponent();
                
                itemPickupDisabled.setText("Item pickup disabled.");
                itemPickupDisabled.setColor(ChatColor.GREEN);
                
                player.sendMessage(itemPickupDisabled);
            }
            else {
                
                logger.log(Level.INFO, "Player " + player.getName() + " (UUID: " + playerId.toString() + ") managed to use only 1 flag that isn't \"off\" or \"on\" while using the vpickup command.");
                logger.log(Level.INFO, "The flag was: " + flags.iterator().next());
                
                final TextComponent unlikelyErrorOccurred = new TextComponent();
                
                unlikelyErrorOccurred.setText("An unlikely error has occurred. Please report this to a server administrator so they can look into it.");
                unlikelyErrorOccurred.setColor(ChatColor.RED);
                
                player.sendMessage(unlikelyErrorOccurred);
            }
        }
        else {
            
        	final TextComponent pleaseOnlyUseOne = new TextComponent();
            
            pleaseOnlyUseOne.setText("Please only use 1 of the following with this command: [on|off]");
            pleaseOnlyUseOne.setColor(ChatColor.RED);
            
            player.sendMessage(pleaseOnlyUseOne);
        }
    }
}
