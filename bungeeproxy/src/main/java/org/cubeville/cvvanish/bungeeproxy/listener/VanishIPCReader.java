package org.cubeville.cvvanish.bungeeproxy.listener;

import java.util.List;

import org.cubeville.cvipc.bungeeproxy.IPCMessage;
import org.cubeville.cvipc.bungeeproxy.IPCReader;
import org.cubeville.cvvanish.bungeeproxy.CVVanish;

public class VanishIPCReader implements IPCReader {
    
    private CVVanish vanishPlugin;
    
    public VanishIPCReader(CVVanish vanishPlugin) {
        
        this.vanishPlugin = vanishPlugin;
    }
    
    @Override
    @Deprecated
    public void process(String server, String channel, String message) {
        // Don't use, out of date.
    }
    
    @Override
    public void readIPCMessage(IPCMessage ipcMessage) {
        
        if(ipcMessage.getChannel().equals("vanishInitializer")) {
            
            List<String> messages = ipcMessage.getMessages();
            if(messages.size() != 1) {
                //TODO: Log error.
                return;
            }
            if(!messages.get(0).equals("readyForVanishInitialization")) {
                //TODO: Log error.
                return;
            }
            
            vanishPlugin.initializeServer(ipcMessage.getProxyIPCConnectionName());
        }
    }
}
