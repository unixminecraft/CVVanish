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

package org.cubeville.cvvanish.bungeebukkit.thread;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.cvipc.bungeebukkit.CVIPC;
import org.cubeville.cvipc.bungeebukkit.IPCMessage;
import org.cubeville.cvvanish.bungeebukkit.CVVanish;

public final class PickupInitializer implements Runnable {
	
	private final CVVanish vanishPlugin;
	private final CVIPC ipcPlugin;
	
	private final Logger logger;
	
	private int initializeTaskNumber;
	
	public PickupInitializer(final CVVanish vanishPlugin, final CVIPC ipcPlugin) {
		
		this.vanishPlugin = vanishPlugin;
		this.ipcPlugin = ipcPlugin;
		
		this.logger = vanishPlugin.getLogger();
	}
	
	public void start() {
		
		logger.log(Level.INFO, "Starting pickup initialization requests to the proxy.");
		
		initializeTaskNumber = vanishPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(vanishPlugin, this, 0L, 20L * 2L);
	}
	
	@Override
	public void run() {
		
		if(!ipcPlugin.isClientReady()) {
			
			logger.log(Level.INFO, "IPCClient is not ready, will try pickup initialization again.");
			return;
		}
		
		logger.log(Level.INFO, "IPCClient is ready, requesting pickup initialization.");
		
		final IPCMessage ipcMessage = new IPCMessage("CVVANISH_PICKUP_BUKKIT_READY");
		ipcMessage.addMessage("cvvanish_pickup_bukkit_ready");
		ipcPlugin.sendIPCMessage(ipcMessage);
	}
	
	public void stop() {
		
		logger.log(Level.INFO, "Cancelling pickup initialization requests.");
		
		vanishPlugin.getServer().getScheduler().cancelTask(initializeTaskNumber);
	}
}
