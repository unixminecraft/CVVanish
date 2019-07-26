package org.cubeville.cvvanish.bungeebukkit.listener;

import java.util.List;
import java.util.UUID;

import org.cubeville.cvipc.bungeebukkit.IPCReader;
import org.cubeville.cvipc.bungeebukkit.IPCMessage;
import org.cubeville.cvvanish.bungeebukkit.CVVanish;

public class VanishIPCReader implements IPCReader {

    private CVVanish vanishPlugin;
    
    public VanishIPCReader(CVVanish plugin) {
        this.vanishPlugin = plugin;
    }
    
    @Override
    public void readIPCMessage(IPCMessage ipcMessage) {
        
        String channel = ipcMessage.getChannel();
        List<String> messages = ipcMessage.getMessages();
        if(messages.size() == 0) {
            //TODO: Log error.
            return;
        }
        
        if(channel.equals("initializeHidePlayer")) {
            
            for(String hiddenPlayerIdValue : messages) {
                if(!vanishPlugin.hidePlayer(UUID.fromString(hiddenPlayerIdValue))) {
                    //TODO: Log error.
                }
            }
        }
        else if(channel.equals("initializePlayerPickup")) {
            
            for(String pickupPlayerIdValue : messages) {
                if(!vanishPlugin.enablePlayerPickup(UUID.fromString(pickupPlayerIdValue))) {
                    //TODO: Log error.
                }
            }
        }
        else if(channel.equals("hidePlayer")) {
            
            if(!vanishPlugin.hidePlayer(UUID.fromString(messages.get(0)))) {
                //TODO: Log error.
            }
        }
        else if(channel.equals("enablePlayerPickup")) {
            
            if(!vanishPlugin.enablePlayerPickup(UUID.fromString(messages.get(0)))) {
                //TODO: Log error.
            }
        }
        else if(channel.equals("showPlayer")) {
            
            if(!vanishPlugin.showPlayer(UUID.fromString(messages.get(0)))) {
                //TODO: Log error.
            }
        }
        else if(channel.equals("disablePlayerPickup")) {
            
            if(!vanishPlugin.disablePlayerPickup(UUID.fromString(messages.get(0)))) {
                //TODO: Log error.
            }
        }
    }
    
    @Override
    @Deprecated
    public void process(String channel, String message) {
        //TODO: Get rid of, unused
    }
}
