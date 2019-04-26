package org.cubeville.vanish.bukkit;

import java.util.Set;
import java.util.UUID;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvipc.CVIPC;
import org.cubeville.vanish.bukkit.listener.EventListener;
import org.cubeville.vanish.bukkit.listener.VanishIPC;

public class CVVanish extends JavaPlugin {

    private CVIPC ipc;
    private Set<UUID> completeInvisiblePlayers;
    private Set<UUID> partialInvisiblePlayers;
    private Set<UUID> pickupPlayers;
    
    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        EventListener eventListener = new EventListener(this);
        pluginManager.registerEvents(eventListener, this);
        ipc = (CVIPC) pluginManager.getPlugin("CVIPC");
        VanishIPC vanishIPC = new VanishIPC(this);
        
        ipc.registerInterface("initializeCompleteInvisiblePlayers", vanishIPC);
        ipc.registerInterface("initializePartialInvisiblePlayers", vanishIPC);
        ipc.registerInterface("initializePickupPlayers", vanishIPC);
        
        ipc.registerInterface("addCompleteInvisiblePlayer", vanishIPC);
        ipc.registerInterface("addPartialInvisiblePlayer", vanishIPC);
        ipc.registerInterface("addPickupPlayer", vanishIPC);
        
        ipc.registerInterface("removeCompleteInvisiblePlayer", vanishIPC);
        ipc.registerInterface("removePartialInvisiblePlayer", vanishIPC);
        ipc.registerInterface("removePickupPlayer", vanishIPC);
    }
    
    @Override
    public void onDisable() {
        ipc.deregisterInterface("initializeCompleteInvisiblePlayers");
        ipc.deregisterInterface("initializePartialInvisiblePlayers");
        ipc.deregisterInterface("initializePickupPlayers");
        
        ipc.deregisterInterface("addCompleteInvisiblePlayer");
        ipc.deregisterInterface("addPartialInvisiblePlayer");
        ipc.deregisterInterface("addPickupPlayer");
        
        ipc.deregisterInterface("removeCompleteInvisiblePlayer");
        ipc.deregisterInterface("removePartialInvisiblePlayer");
        ipc.deregisterInterface("removePickupPlayer");
    }
    
    public void initializeCompleteInvisiblePlayers(Set<UUID> playerIds) {
        completeInvisiblePlayers = playerIds;
    }
    
    public void initializePartialInvisiblePlayers(Set<UUID> playerIds) {
        partialInvisiblePlayers = playerIds;
    }
    
    public void initializePickupPlayers(Set<UUID> playerIds) {
        pickupPlayers = playerIds;
    }
    
    public void addCompleteInvisiblePlayer(UUID playerId) {
        completeInvisiblePlayers.add(playerId);
    }
    
    public void addPartialInvisiblePlayer(UUID playerId) {
        partialInvisiblePlayers.add(playerId);
    }
    
    public void addPickupPlayer(UUID playerId) {
        pickupPlayers.add(playerId);
    }
    
    public void removeCompleteInvisiblePlayer(UUID playerId) {
        completeInvisiblePlayers.remove(playerId);
    }
    
    public void removePartialInvisiblePlayer(UUID playerId) {
        partialInvisiblePlayers.remove(playerId);
    }
    
    public void removePickupPlayer(UUID playerId) {
        pickupPlayers.remove(playerId);
    }
    
    public Set<UUID> getCompleteInvisiblePlayers() {
        return completeInvisiblePlayers;
    }
    
    public Set<UUID> getPartialInvisiblePlayers() {
        return partialInvisiblePlayers;
    }
    
    public Set<UUID> getPickupPlayers() {
        return pickupPlayers;
    }
}
