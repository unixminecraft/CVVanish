package org.cubeville.cvvanish.bungeebukkit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvipc.bungeebukkit.CVIPC;
import org.cubeville.cvipc.bungeebukkit.IPCMessage;
import org.cubeville.cvvanish.bungeebukkit.listener.EventListener;
import org.cubeville.cvvanish.bungeebukkit.listener.VanishIPCReader;

public class CVVanish extends JavaPlugin {
    
    private CVIPC ipcPlugin;
    private Set<UUID> hiddenPlayers;
    private Set<UUID> pickupPlayers;
    
    @Override
    public void onEnable() {
        
        PluginManager pluginManager = getServer().getPluginManager();
        
        pluginManager.registerEvents(new EventListener(this), this);
        ipcPlugin = (CVIPC) pluginManager.getPlugin("CVIPC");
        VanishIPCReader vanishIPCInterface = new VanishIPCReader(this);
        
        ipcPlugin.registerIPCReader("initializeHidePlayer", vanishIPCInterface);
        ipcPlugin.registerIPCReader("initializePlayerPickup", vanishIPCInterface);
        
        ipcPlugin.registerIPCReader("hidePlayer", vanishIPCInterface);
        ipcPlugin.registerIPCReader("enablePlayerPickup", vanishIPCInterface);
        
        ipcPlugin.registerIPCReader("showPlayer", vanishIPCInterface);
        ipcPlugin.registerIPCReader("disablePlayerPickup", vanishIPCInterface);
        
        IPCMessage ipcMessage = new IPCMessage("vanishInitializer");
        ipcMessage.addMessage("readyForVanishInitialization");
        
        ipcPlugin.sendIPCMessage(ipcMessage);
        
        hiddenPlayers = new HashSet<UUID>();
        pickupPlayers = new HashSet<UUID>();
    }
    
    @Override
    public void onDisable() {
        
        ipcPlugin.deregisterIPCReader("initializeHidePlayer");
        ipcPlugin.deregisterIPCReader("initializePlayerPickup");
        
        ipcPlugin.deregisterIPCReader("hidePlayer");
        ipcPlugin.deregisterIPCReader("enablePlayerPickup");
        
        ipcPlugin.deregisterIPCReader("showPlayer");
        ipcPlugin.deregisterIPCReader("disablePlayerPickup");
    }
    
    public boolean isPlayerHidden(UUID playerIdToCheck) {
        return hiddenPlayers.contains(playerIdToCheck);
    }
    
    public boolean hidePlayer(UUID playerIdToHide) {
        
        if(playerIdToHide == null) {
            return false;
        }
        
        Player playerToHide = getServer().getPlayer(playerIdToHide);
        if(playerToHide == null) {
            return false;
        }
        
        if(isPlayerHidden(playerIdToHide)) {
            return false;
        }
        
        hiddenPlayers.add(playerIdToHide);
        
        for(Player otherOnlinePlayer : (Collection<? extends Player>) getServer().getOnlinePlayers()) {
            
            if(otherOnlinePlayer.getUniqueId().equals(playerIdToHide)) {
                continue;
            }
            if(otherOnlinePlayer.hasPermission("cvvanish.see")) {
                continue;
            }
            otherOnlinePlayer.hidePlayer(this, playerToHide);
        }
        
        return true;
    }
    
    public boolean showPlayer(UUID playerIdToShow) {
        
        if(playerIdToShow == null) {
            return false;
        }
        
        Player playerToShow = getServer().getPlayer(playerIdToShow);
        if(playerToShow == null) {
            return false;
        }
        
        if(!isPlayerHidden(playerIdToShow)) {
            return false;
        }
        
        hiddenPlayers.remove(playerIdToShow);
        
        for(Player otherOnlinePlayer : (Collection<? extends Player>) getServer().getOnlinePlayers()) {
            
            if(otherOnlinePlayer.getUniqueId().equals(playerIdToShow)) {
                continue;
            }
            otherOnlinePlayer.showPlayer(this, playerToShow);
        }
        
        return true;
    }
    
    public boolean isPlayerPickupEnabled(UUID playerIdToCheck) {
        return pickupPlayers.contains(playerIdToCheck);
    }
    
    public boolean enablePlayerPickup(UUID playerIdToEnable) {
        
        if(playerIdToEnable == null) {
            return false;
        }
        return pickupPlayers.add(playerIdToEnable);
    }
    
    public boolean disablePlayerPickup(UUID playerIdToDisable) {
        
        if(playerIdToDisable == null) {
            return false;
        }
        return pickupPlayers.remove(playerIdToDisable);
    }
}
