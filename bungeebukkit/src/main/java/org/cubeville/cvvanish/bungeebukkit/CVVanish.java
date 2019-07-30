package org.cubeville.cvvanish.bungeebukkit;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.cubeville.cvipc.bungeebukkit.CVIPC;
import org.cubeville.cvipc.bungeebukkit.IPCMessage;
import org.cubeville.cvvanish.bungeebukkit.listener.EventListener;
import org.cubeville.cvvanish.bungeebukkit.listener.VanishIPCReader;
import org.cubeville.cvvanish.bungeebukkit.thread.NightVisionEffectIssuer;

public class CVVanish extends JavaPlugin {
    
    private static final String VANISHED_TEAM_NAME = "VanishedTeam";
    
    private CVIPC ipcPlugin;
    private HashSet<UUID> vanishedPlayerIds;
    private HashSet<UUID> pickupPlayerIds;
    private NightVisionEffectIssuer nightVisionEffectIssuer;
    
    @Override
    public void onEnable() {
        
        PluginManager pluginManager = getServer().getPluginManager();
        
        pluginManager.registerEvents(new EventListener(this), this);
        ipcPlugin = (CVIPC) pluginManager.getPlugin("CVIPC");
        VanishIPCReader vanishIPCReader = new VanishIPCReader(this);
        
        ipcPlugin.registerIPCReader("initializeVanishPlayer", vanishIPCReader);
        ipcPlugin.registerIPCReader("initializePlayerPickup", vanishIPCReader);
        
        ipcPlugin.registerIPCReader("vanishPlayer", vanishIPCReader);
        ipcPlugin.registerIPCReader("enablePlayerPickup", vanishIPCReader);
        
        ipcPlugin.registerIPCReader("unvanishPlayer", vanishIPCReader);
        ipcPlugin.registerIPCReader("disablePlayerPickup", vanishIPCReader);
        
        IPCMessage ipcMessage = new IPCMessage("vanishInitializer");
        ipcMessage.addMessage("readyForVanishInitialization");
        
        ipcPlugin.sendIPCMessage(ipcMessage);
        
        vanishedPlayerIds = new HashSet<UUID>();
        pickupPlayerIds = new HashSet<UUID>();
        
        nightVisionEffectIssuer = new NightVisionEffectIssuer(this);
        nightVisionEffectIssuer.start();
    }
    
    @Override
    public void onDisable() {
        
        nightVisionEffectIssuer.stop();
        
        ipcPlugin.deregisterIPCReader("initializeVanishPlayer");
        ipcPlugin.deregisterIPCReader("initializePlayerPickup");
        
        ipcPlugin.deregisterIPCReader("vanishPlayer");
        ipcPlugin.deregisterIPCReader("enablePlayerPickup");
        
        ipcPlugin.deregisterIPCReader("unvanishPlayer");
        ipcPlugin.deregisterIPCReader("disablePlayerPickup");
    }
    
    public HashSet<UUID> getVanishedPlayerIds() {
        return vanishedPlayerIds;
    }
    
    public boolean isPlayerVanished(UUID playerToCheckId) {
        return vanishedPlayerIds.contains(playerToCheckId);
    }
    
    public boolean vanishPlayer(UUID playerToVanishId) {
        
        if(playerToVanishId == null) {
            return false;
        }
        
        Player playerToVanish = getServer().getPlayer(playerToVanishId);
        if(playerToVanish == null) {
            return false;
        }
        
        if(isPlayerVanished(playerToVanishId)) {
            return false;
        }
        
        vanishedPlayerIds.add(playerToVanishId);
        
        for(Player otherOnlinePlayer : (Collection<? extends Player>) getServer().getOnlinePlayers()) {
            
            if(otherOnlinePlayer.getUniqueId().equals(playerToVanishId)) {
                continue;
            }
            if(otherOnlinePlayer.hasPermission("cvvanish.vanish.view")) {
                continue;
            }
            otherOnlinePlayer.hidePlayer(this, playerToVanish);
        }
        
        //TODO: Check to make sure the invisible person doesn't push the visible
        //      people, and not the other way around (invisible pushes visible).
        playerToVanish.setCollidable(false);
        
        Team playerToVanishTeam = playerToVanish.getScoreboard().getTeam(VANISHED_TEAM_NAME);
        if(playerToVanishTeam == null) {
            playerToVanishTeam = playerToVanish.getScoreboard().registerNewTeam(VANISHED_TEAM_NAME);
        }
        
        playerToVanishTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        playerToVanishTeam.addEntry(playerToVanish.getName());
        
        return true;
    }
    
    public boolean unvanishPlayer(UUID playerToUnvanishId) {
        
        if(playerToUnvanishId == null) {
            return false;
        }
        
        Player playerToUnvanish = getServer().getPlayer(playerToUnvanishId);
        if(playerToUnvanish == null) {
            return false;
        }
        
        if(!isPlayerVanished(playerToUnvanishId)) {
            return false;
        }
        
        vanishedPlayerIds.remove(playerToUnvanishId);
        
        for(Player otherOnlinePlayer : (Collection<? extends Player>) getServer().getOnlinePlayers()) {
            
            if(otherOnlinePlayer.getUniqueId().equals(playerToUnvanishId)) {
                continue;
            }
            otherOnlinePlayer.showPlayer(this, playerToUnvanish);
        }
        
        playerToUnvanish.setCollidable(true);
        
        Team playerToUnvanishTeam = playerToUnvanish.getScoreboard().getTeam(VANISHED_TEAM_NAME);
        if(playerToUnvanishTeam != null) {
            playerToUnvanishTeam.removeEntry(playerToUnvanish.getName());
        }
        
        return true;
    }
    
    public boolean isPlayerPickupEnabled(UUID playerToCheckId) {
        return pickupPlayerIds.contains(playerToCheckId);
    }
    
    public boolean enablePlayerPickup(UUID playerToEnablePickupId) {
        
        if(playerToEnablePickupId == null) {
            return false;
        }
        return pickupPlayerIds.add(playerToEnablePickupId);
    }
    
    public boolean disablePlayerPickup(UUID playerToDisablePickupId) {
        
        if(playerToDisablePickupId == null) {
            return false;
        }
        return pickupPlayerIds.remove(playerToDisablePickupId);
    }
}
