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

package org.cubeville.cvvanish.bungeeproxy.listener;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.cvipc.bungeeproxy.IPCMessage;
import org.cubeville.cvipc.bungeeproxy.listener.IPCInterface;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

public final class ProxyIPCInterface implements IPCInterface {
    
    private final CVVanish vanishPlugin;
    private final Logger logger;
    
    public ProxyIPCInterface(final CVVanish vanishPlugin) {
        
        this.vanishPlugin = vanishPlugin;
        this.logger = vanishPlugin.getLogger();
    }
    
    @Override
    public void readIPCMessage(final IPCMessage ipcMessage) {
        
    	final String channel = ipcMessage.getChannel();
    	
        if(channel.equals(CVVanish.CHANNEL_CVVANISH_BUKKIT_READY)) {
        	
        	final String serverName = ipcMessage.getServerName();
        	final List<String> messages = ipcMessage.getMessages();
        	
        	logger.log(Level.INFO, "Vanish Bukkit initialize query received for server " + serverName + ".");
        	logger.log(Level.INFO, "IPCMessage data: " + ipcMessage.toString());
        	
            if(messages.size() != 1) {
                
            	logger.log(Level.INFO, "Bukkit server vanish initialization for server " + serverName + " does not have the correct amount of data.");
            	logger.log(Level.INFO, "The number of messages should be 1, it is currently " + String.valueOf(messages.size()) + ".");
            	logger.log(Level.INFO, "IPCMessage data: " + ipcMessage.toString());
                return;
            }
            
            if(!messages.get(0).equals("cvvanish_bukkit_ready")) {
                
            	logger.log(Level.INFO, "Bukkit server vanish initialization for server " + serverName + " does not have the correct data.");
            	logger.log(Level.INFO, "The single message should be \"cvvanish_bukkit_ready\".");
            	logger.log(Level.INFO, "IPCMessage data: " + ipcMessage.toString());
                return;
            }
            
            vanishPlugin.initializeServer(serverName);
        }
        else {
        	
        	logger.log(Level.INFO, "IPC channel " + channel + " is not registered to this IPCInterface. IPCMessage data: " + ipcMessage.toString());
        }
    }
}
