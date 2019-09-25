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

package org.cubeville.cvvanish.bungeebukkit.listener;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.cvipc.bungeebukkit.listener.IPCInterface;
import org.cubeville.cvipc.bungeebukkit.IPCMessage;
import org.cubeville.cvvanish.bungeebukkit.CVVanish;

public final class BukkitIPCInterface implements IPCInterface {

    private final CVVanish vanishPlugin;
    
    private final Logger logger;
    
    public BukkitIPCInterface(final CVVanish plugin) {
    	
        this.vanishPlugin = plugin;
        
        this.logger = vanishPlugin.getLogger();
    }
    
    @Override
    public void readIPCMessage(final IPCMessage ipcMessage) {
        
        final String channel = ipcMessage.getChannel();
        final List<String> messages = ipcMessage.getMessages();
        
        if(channel.equals(CVVanish.CHANNEL_VANISH_INITIALIZE)) {
        	
        	logger.log(Level.INFO, "Vanish initialize response received.");
        	logger.log(Level.INFO, "Vanish IPCMessage data: " + ipcMessage.toString());
        	
        	for(final String message : messages) {
        		
        		try {
        			UUID.fromString(message);
        		}
        		catch(IllegalArgumentException e) {
        			
        			logger.log(Level.INFO, "Unable to parse UUID from " + message + " for vanish initialization.", e);
        			continue;
        		}
        		
        		vanishPlugin.enableVanish(UUID.fromString(message));
        	}
        }
        else if(channel.equals(CVVanish.CHANNEL_VANISH_ENABLE)) {
        	
        	if(messages.size() != 1) {
        		
        		logger.log(Level.INFO, "Incorrect message format for vanish enable.");
        		logger.log(Level.INFO, "IPCMessage size should be 1.");
        		logger.log(Level.INFO, "IPCMessage data: " + ipcMessage.toString());
        	}
        	
        	final String playerIdValue = messages.get(0);
        	
        	try {
        		UUID.fromString(playerIdValue);
        	}
        	catch(IllegalArgumentException e) {
        		
        		logger.log(Level.INFO, "Unable to parse UUID from " + playerIdValue + " for enabling vanish.", e);
        		return;
        	}
        	
        	vanishPlugin.enableVanish(UUID.fromString(playerIdValue));
        }
        else if(channel.equals(CVVanish.CHANNEL_VANISH_DISABLE)) {
        	
        	if(messages.size() != 1) {
        		
        		logger.log(Level.INFO, "Incorrect message format for vanish disable.");
        		logger.log(Level.INFO, "IPCMessage size should be 1.");
        		logger.log(Level.INFO, "IPCMessage data: " + ipcMessage.toString());
        	}
        	
        	final String playerIdValue = messages.get(0);
        	
        	try {
        		UUID.fromString(playerIdValue);
        	}
        	catch(IllegalArgumentException e) {
        		
        		logger.log(Level.INFO, "Unable to parse UUID from " + playerIdValue + " for disabling vanish.", e);
        		return;
        	}
        	
        	vanishPlugin.disableVanish(UUID.fromString(playerIdValue));
        }
        else if(channel.equals(CVVanish.CHANNEL_PICKUP_INITIALIZE)) {
        	
        	logger.log(Level.INFO, "Pickup initialize response received.");
        	logger.log(Level.INFO, "Vanish IPCMessage data: " + ipcMessage.toString());
        	
        	for(final String message : messages) {
        		
        		try {
        			UUID.fromString(message);
        		}
        		catch(IllegalArgumentException e) {
        			
        			logger.log(Level.INFO, "Unable to parse UUID from " + message + " for pickup initialization.", e);
        			continue;
        		}
        		
        		vanishPlugin.enablePickup(UUID.fromString(message));
        	}
        }
        else if(channel.equals(CVVanish.CHANNEL_PICKUP_ENABLE)) {
        	
        	if(messages.size() != 1) {
        		
        		logger.log(Level.INFO, "Incorrect message format for pickup enable.");
        		logger.log(Level.INFO, "IPCMessage size should be 1.");
        		logger.log(Level.INFO, "IPCMessage data: " + ipcMessage.toString());
        	}
        	
        	final String playerIdValue = messages.get(0);
        	
        	try {
        		UUID.fromString(playerIdValue);
        	}
        	catch(IllegalArgumentException e) {
        		
        		logger.log(Level.INFO, "Unable to parse UUID from " + playerIdValue + " for enabling pickup.", e);
        		return;
        	}
        	
        	vanishPlugin.enablePickup(UUID.fromString(playerIdValue));
        }
        else if(channel.equals(CVVanish.CHANNEL_PICKUP_DISABLE)) {
        	
        	if(messages.size() != 1) {
        		
        		logger.log(Level.INFO, "Incorrect message format for pickup disable.");
        		logger.log(Level.INFO, "IPCMessage size should be 1.");
        		logger.log(Level.INFO, "IPCMessage data: " + ipcMessage.toString());
        	}
        	
        	final String playerIdValue = messages.get(0);
        	
        	try {
        		UUID.fromString(playerIdValue);
        	}
        	catch(IllegalArgumentException e) {
        		
        		logger.log(Level.INFO, "Unable to parse UUID from " + playerIdValue + " for disabling pickup.", e);
        		return;
        	}
        	
        	vanishPlugin.disableVanish(UUID.fromString(playerIdValue));
        }
        else {
        	
        	logger.log(Level.INFO, "Channel " + channel + " made it to the CVVanish BukkitIPCInterface.");
        	logger.log(Level.INFO, "That channel is not registered to this IPCInterface.");
        	logger.log(Level.INFO, "IPCMessage data: " + ipcMessage.toString());
        }
    }
}
