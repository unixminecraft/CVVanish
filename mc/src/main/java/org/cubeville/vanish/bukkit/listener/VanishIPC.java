package org.cubeville.vanish.bukkit.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.cubeville.cvipc.IPCInterface;
import org.cubeville.vanish.bukkit.CVVanish;

public class VanishIPC implements IPCInterface {

    private CVVanish plugin;
    
    public VanishIPC(CVVanish plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void process(String channel, String message) {
        if(channel.equals("initializeCompleteInvisiblePlayers")) {
            plugin.initializeCompleteInvisiblePlayers(getIds(message));
        }
        else if(channel.equals("initalizePartialInvisiblePlayers")) {
            plugin.initializePartialInvisiblePlayers(getIds(message));
        }
        else if(channel.equals("initializePickupPlayers")) {
            plugin.initializePickupPlayers(getIds(message));
        }
        else if(channel.equals("addCompleteInvisiblePlayer")) {
            plugin.addCompleteInvisiblePlayer(getId(message));
        }
        else if(channel.equals("addPartialInvisiblePlayer")) {
            plugin.addPartialInvisiblePlayer(getId(message));
        }
        else if(channel.equals("addPickupPlayer")) {
            plugin.addPickupPlayer(getId(message));
        }
        else if(channel.equals("removeCompleteInvisiblePlayer")) {
            plugin.removeCompleteInvisiblePlayer(getId(message));
        }
        else if(channel.equals("removePartialInvisiblePlayer")) {
            plugin.removeCompleteInvisiblePlayer(getId(message));
        }
        else if(channel.equals("removePickupPlayer")) {
            plugin.removePickupPlayer(getId(message));
        }
    }
    
    private UUID getId(String message) {
        UUID playerID = null;
        try {
            playerID = UUID.fromString(message);
            return playerID;
        }
        catch(IllegalArgumentException e) {
            System.out.print("[CVVanish] ERROR: Unable to detect UUID: " + message + "\n");
            return null;
        }
    }
    
    private Set<UUID> getIds(String message) {
        Set<UUID> playerIDs = new HashSet<UUID>();
        int index = message.indexOf("|");
        while(index != -1) {
            String uuid = message.substring(0, index);
            message = message.substring(index + 1);
            index = message.indexOf("|");
            try {
                playerIDs.add(UUID.fromString(uuid));
            }
            catch(IllegalArgumentException e) {
                System.out.print("[CVVanish] ERROR: Cannot convert UUID: " + uuid + "\n");
            }
        }
        return playerIDs;
    }
}
