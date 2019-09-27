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

import org.cubeville.cvipc.bungeebukkit.CVIPC;
import org.cubeville.cvipc.bungeebukkit.IPCMessage;
import org.cubeville.cvvanish.bungeebukkit.CVVanish;

public final class VanishInitializer implements Runnable {
	
	private final CVVanish vanishPlugin;
	private final CVIPC ipcPlugin;
	
	public VanishInitializer(final CVVanish vanishPlugin, final CVIPC ipcPlugin) {
		
		this.vanishPlugin = vanishPlugin;
		this.ipcPlugin = ipcPlugin;
	}
	
	public void start() {
		
		vanishPlugin.getServer().getScheduler().scheduleSyncDelayedTask(vanishPlugin, this);
	}
	
	@Override
	public void run() {
		
		while(!ipcPlugin.isClientReady()) {
			try {
				Thread.sleep(1);
			}
			catch(InterruptedException e) {
				// Do nothing.
			}
		}
		
		try {
			Thread.sleep(1);
		}
		catch(InterruptedException e) {
			// Do nothing.
		}
		
		final IPCMessage ipcMessage = new IPCMessage("CVVANISH_BUKKIT_READY");
		
		ipcMessage.addMessage("cvvanish_bukkit_ready");
		
		ipcPlugin.sendIPCMessage(ipcMessage);
	}
}
