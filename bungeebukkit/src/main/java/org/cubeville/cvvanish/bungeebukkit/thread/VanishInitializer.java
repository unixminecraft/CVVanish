package org.cubeville.cvvanish.bungeebukkit.thread;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.cvipc.bungeebukkit.CVIPC;
import org.cubeville.cvipc.bungeebukkit.IPCMessage;
import org.cubeville.cvvanish.bungeebukkit.CVVanish;

public final class VanishInitializer implements Runnable {
	
	private final CVVanish vanishPlugin;
	private final CVIPC ipcPlugin;
	
	private final Logger logger;
	
	public VanishInitializer(final CVVanish vanishPlugin, final CVIPC ipcPlugin) {
		
		this.vanishPlugin = vanishPlugin;
		this.ipcPlugin = ipcPlugin;
		
		this.logger = vanishPlugin.getLogger();
	}
	
	public void start() {
		
		vanishPlugin.getServer().getScheduler().runTaskAsynchronously(vanishPlugin, this);
		
		logger.log(Level.INFO, "Vanish Bukkit initialization scheduled.");
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
		
        final IPCMessage initializationReadyMessage = new IPCMessage("CVVANISH_BUKKIT_READY");
        
        initializationReadyMessage.addMessage("cvvanish_bukkit_ready");
        
        ipcPlugin.sendIPCMessage(initializationReadyMessage);
        
        logger.log(Level.INFO, "Vanish Bukkit initialization message sent.");
	}
}
