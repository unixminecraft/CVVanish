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
		
		vanishPlugin.getServer().getScheduler().runTaskAsynchronously(vanishPlugin, this);
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
