package org.cubeville.cvvanish.bungeebukkit;

import java.util.Collection;
import java.util.HashSet;
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
    private HashSet<UUID> hiddenPlayerIds;
    private HashSet<UUID> pickupPlayerIds;
    
    @Override
    public void onEnable() {
        
        PluginManager pluginManager = getServer().getPluginManager();
        
        pluginManager.registerEvents(new EventListener(this), this);
        ipcPlugin = (CVIPC) pluginManager.getPlugin("CVIPC");
        VanishIPCReader vanishIPCReader = new VanishIPCReader(this);
        
        ipcPlugin.registerIPCReader("initializeHidePlayer", vanishIPCReader);
        ipcPlugin.registerIPCReader("initializePlayerPickup", vanishIPCReader);
        
        ipcPlugin.registerIPCReader("hidePlayer", vanishIPCReader);
        ipcPlugin.registerIPCReader("enablePlayerPickup", vanishIPCReader);
        
        ipcPlugin.registerIPCReader("showPlayer", vanishIPCReader);
        ipcPlugin.registerIPCReader("disablePlayerPickup", vanishIPCReader);
        
        IPCMessage ipcMessage = new IPCMessage("vanishInitializer");
        ipcMessage.addMessage("readyForVanishInitialization");
        
        ipcPlugin.sendIPCMessage(ipcMessage);
        
        hiddenPlayerIds = new HashSet<UUID>();
        pickupPlayerIds = new HashSet<UUID>();
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
        return hiddenPlayerIds.contains(playerIdToCheck);
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
        
        hiddenPlayerIds.add(playerIdToHide);
        
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
        
        hiddenPlayerIds.remove(playerIdToShow);
        
        for(Player otherOnlinePlayer : (Collection<? extends Player>) getServer().getOnlinePlayers()) {
            
            if(otherOnlinePlayer.getUniqueId().equals(playerIdToShow)) {
                continue;
            }
            otherOnlinePlayer.showPlayer(this, playerToShow);
        }
        
        return true;
    }
    
    public boolean isPlayerPickupEnabled(UUID playerIdToCheck) {
        return pickupPlayerIds.contains(playerIdToCheck);
    }
    
    public boolean enablePlayerPickup(UUID playerIdToEnable) {
        
        if(playerIdToEnable == null) {
            return false;
        }
        return pickupPlayerIds.add(playerIdToEnable);
    }
    
    public boolean disablePlayerPickup(UUID playerIdToDisable) {
        
        if(playerIdToDisable == null) {
            return false;
        }
        return pickupPlayerIds.remove(playerIdToDisable);
    }
}
