package org.cubeville.cvvanish.bungeeproxy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.cubeville.cvipc.bungeeproxy.CVIPC;
import org.cubeville.cvipc.bungeeproxy.IPCMessage;
import org.cubeville.cvplayerdata.bungeecord.CVPlayerData;
import org.cubeville.cvvanish.bungeeproxy.command.FakeJoinCommand;
import org.cubeville.cvvanish.bungeeproxy.command.FakeQuitCommand;
import org.cubeville.cvvanish.bungeeproxy.command.HideCommand;
import org.cubeville.cvvanish.bungeeproxy.command.LegacyPVCommand;
import org.cubeville.cvvanish.bungeeproxy.command.LegacyVCommand;
import org.cubeville.cvvanish.bungeeproxy.command.PickupCommand;
import org.cubeville.cvvanish.bungeeproxy.command.ShowCommand;
import org.cubeville.cvvanish.bungeeproxy.command.TabOffCommand;
import org.cubeville.cvvanish.bungeeproxy.command.TabOnCommand;
import org.cubeville.cvvanish.bungeeproxy.command.VisibilityOffCommand;
import org.cubeville.cvvanish.bungeeproxy.command.VisibilityOnCommand;
import org.cubeville.cvvanish.bungeeproxy.listener.EventListener;
import org.cubeville.cvvanish.bungeeproxy.listener.VanishIPCReader;
import org.cubeville.cvvanish.bungeeproxy.thread.HiddenNotifier;
import org.cubeville.cvvanish.bungeeproxy.thread.UnlistedNotifier;
import org.cubeville.cvvanish.bungeeproxy.thread.VanishedNotifier;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class CVVanish extends Plugin {
    
    private enum HiddenStatus {
        
        UNLISTED,
        VANISHED,
        HIDDEN;
    }
    
    private static final String HIDDEN_FILE_NAME = "hidden.conf";
    private static final String PICKUP_FILE_NAME = "pickup.conf";
    
    private ConfigurationProvider yamlProvider;
    private CVIPC ipcPlugin;
    private CVPlayerData playerDataPlugin;
    private File configurationDirectory;
    private File hiddenFile;
    private File pickupFile;
    private HashMap<UUID, HiddenStatus> playerIdHiddenStatus;
    private HashSet<UUID> pickupPlayerIds;
    private HiddenNotifier hiddenNotifier;
    private UnlistedNotifier unlistedNotifier;
    private VanishedNotifier vanishedNotifier;
    
    
    @Override
    public void onEnable() {
        
        //TODO: Check for null on Plugins.
        //TODO: If null, log errors and throw RuntimeExceptions.
        
        PluginManager pluginManager = getProxy().getPluginManager();
        
        ipcPlugin = (CVIPC) pluginManager.getPlugin("CVIPC");
        if(ipcPlugin == null) {
            //TODO: Log error, throw better things than this.
            throw new RuntimeException("Can't start CVVanish proxy-side without CVIPC proxy-side.");
        }
        
        playerDataPlugin = (CVPlayerData) pluginManager.getPlugin("CVPlayerData");
        if(playerDataPlugin == null) {
            //TODO: Log error, throw better things than this.
            throw new RuntimeException("Can't start CVVanish proxy-size without CVPlayerData.");
        }
        
        if(pluginManager.getPlugin("CVTools") == null) {
            //TODO: Log error, throw better things than this.
            throw new RuntimeException("Can't start CVVanish proxy-side without CVTools proxy-side.");
        }
        
        VanishIPCReader vanishIPCReader = new VanishIPCReader(this);
        ipcPlugin.registerIPCReader("vanishInitializer", vanishIPCReader);
        
        configurationDirectory = new File(getDataFolder(), "config");
        
        try {
            configurationDirectory.mkdirs();
        }
        catch(SecurityException e) {
            //TODO: Log error. (better than this)
            String tempErrorMessage = "Error: can't start CVVanish, proxy-side.";
            System.out.print(tempErrorMessage + "\n");
            throw new RuntimeException(tempErrorMessage, e);
        }
        
        playerIdHiddenStatus = new HashMap<UUID, HiddenStatus>();
        pickupPlayerIds = new HashSet<UUID>();
        
        hiddenFile = new File(configurationDirectory, HIDDEN_FILE_NAME);
        pickupFile = new File(configurationDirectory, PICKUP_FILE_NAME);
        
        if(!hiddenFile.exists()) {
            try {
                hiddenFile.createNewFile();
            }
            catch(IOException e) {
                //TODO: Log error.
                //TODO: Change this up a bit with new logging.
                throw new RuntimeException("Can't create file hidden.conf, can't start CVVanish.", e);
            }
        }
        
        if(!pickupFile.exists()) {
            try {
                pickupFile.createNewFile();
            }
            catch(IOException e) {
                //TODO: Log error.
                //TODO: Change this up a bit with new logging.
                throw new RuntimeException("Can't create file pickup.conf, can't start CVVanish.", e);
            }
        }
        
        yamlProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
        
        Configuration hiddenConfiguration = null;
        Configuration pickupConfiguration = null;
        
        try {
            hiddenConfiguration = yamlProvider.load(hiddenFile);
        }
        catch(IOException e) {
            //TODO: Log error.
            //TODO: Change this with new logging and such.
            throw new RuntimeException("Can't load configuration file hidden.conf, can't start CVVanish.", e);
        }
        
        try {
            pickupConfiguration = yamlProvider.load(pickupFile);
        }
        catch(IOException e) {
            //TODO: Log error.
            //TODO: Change this with new logging and such.
            throw new RuntimeException("Can't load configuration file pickup.conf, can't start CVVanish.", e);
        }
        
        if(hiddenConfiguration == null) {
            //TODO: Log error.
            throw new RuntimeException("Configuration file hidden.conf is still null, can't start CVVanish.");
        }
        
        if(pickupConfiguration == null) {
            //TODO: Log error.
            throw new RuntimeException("Configuration file pickup.conf is still null, can't start CVVanish.");
        }
        
        for(String playerIdValue : hiddenConfiguration.getKeys()) {
            
            UUID playerId = null;
            try {
                playerId = UUID.fromString(playerIdValue);
            }
            catch(IllegalArgumentException e) {
                //TODO: Log error.
                continue;
            }
            
            if(playerId == null) {
                //TODO: Log error.
                continue;
            }
            
            String hiddenStatus = hiddenConfiguration.getString(playerIdValue);
            
            if(hiddenStatus.equals("HIDDEN")) {
                playerIdHiddenStatus.put(playerId, HiddenStatus.HIDDEN);
            }
            else if(hiddenStatus.equals("VANISHED")) {
                playerIdHiddenStatus.put(playerId, HiddenStatus.VANISHED);
            }
            else if(hiddenStatus.equals("UNLISTED")) {
                playerIdHiddenStatus.put(playerId, HiddenStatus.UNLISTED);
            }
            else {
                //TODO: Log error.
                // Example: "Unable to get hidden status for " + playerIdStatus + ", skipping."
            }
        }
        
        for(String playerIdValue : pickupConfiguration.getKeys()) {
            
            UUID playerId = null;
            try {
                playerId = UUID.fromString(playerIdValue);
            }
            catch(IllegalArgumentException e) {
                //TODO: Log error.
            }
            
            String validStatus = pickupConfiguration.getString(playerIdValue);
            
            if(validStatus.equals("VALID")) {
                if(!pickupPlayerIds.add(playerId)) {
                    //TODO: Log error about duplicate UUID.
                }
            }
            else {
                //TODO: Log error.
            }
        }
        
        pluginManager.registerListener(this, new EventListener(this, playerDataPlugin));
        
        pluginManager.registerCommand(this, new FakeJoinCommand(this));
        pluginManager.registerCommand(this, new FakeQuitCommand(this));
        pluginManager.registerCommand(this, new HideCommand(this));
        pluginManager.registerCommand(this, new LegacyPVCommand());
        pluginManager.registerCommand(this, new LegacyVCommand());
        pluginManager.registerCommand(this, new PickupCommand(this));
        pluginManager.registerCommand(this, new ShowCommand(this));
        pluginManager.registerCommand(this, new TabOffCommand(this));
        pluginManager.registerCommand(this, new TabOnCommand(this));
        pluginManager.registerCommand(this, new VisibilityOffCommand(this));
        pluginManager.registerCommand(this, new VisibilityOnCommand(this));
        
        hiddenNotifier = new HiddenNotifier(this);
        unlistedNotifier = new UnlistedNotifier(this);
        vanishedNotifier = new VanishedNotifier(this);
        
        hiddenNotifier.start();
        unlistedNotifier.start();
        vanishedNotifier.start();
    }
    
    @Override
    public void onDisable() {
        
        vanishedNotifier.stop();
        unlistedNotifier.stop();
        hiddenNotifier.stop();
        
        //TODO: Does this need to happen?
        //TODO: Or if it does, should everything possible (Listeners, Commands, etc.) be deregistered?
        ipcPlugin.deregisterIPCReader("vanishInitializer");
        
        Configuration hiddenConfiguration = new Configuration();
        Configuration pickupConfiguration = new Configuration();
        
        for(UUID queriedPlayerId : playerIdHiddenStatus.keySet()) {
            
            String queriedPlayerIdValue = queriedPlayerId.toString();
            HiddenStatus hiddenStatus = playerIdHiddenStatus.get(queriedPlayerId);
            
            if(hiddenStatus == HiddenStatus.HIDDEN) {
                hiddenConfiguration.set(queriedPlayerIdValue, "HIDDEN");
            }
            else if(hiddenStatus == HiddenStatus.VANISHED) {
                hiddenConfiguration.set(queriedPlayerIdValue, "VANISHED");
            }
            else if(hiddenStatus == HiddenStatus.UNLISTED) {
                hiddenConfiguration.set(queriedPlayerIdValue, "UNLISTED");
            }
            else {
                
                //TODO: Log error.
                // Somehow, a UUID was in the HashMap
                // without a valid status assigned.
            }
        }
        
        for(UUID pickupPlayerId : pickupPlayerIds) {
            
            String pickupPlayerIdValue = pickupPlayerId.toString();
            pickupConfiguration.set(pickupPlayerIdValue, "VALID");
        }
        
        try {
            yamlProvider.save(hiddenConfiguration, hiddenFile);
        }
        catch(IOException e) {
            
            //TODO: Log error.
            // Probably worth throwing an exception, too,
            // because important data isn't being saved.
        }
        
        try {
            yamlProvider.save(pickupConfiguration, pickupFile);
        }
        catch(IOException e) {
            
            //TODO: Log error.
            // Probably worth throwing an exception too,
            // because semi-important data isn't being saved.
        }
    }
    
    public void initializeServer(String serverName) {
        
        IPCMessage initializeVanishPlayerIPCMessage = new IPCMessage(serverName, "initializeVanishPlayer");
        IPCMessage initializePlayerPickupIPCMessage = new IPCMessage(serverName, "initializePlayerPickup");
        
        for(UUID queriedPlayerId : playerIdHiddenStatus.keySet()) {
            if(isPlayerHidden(queriedPlayerId) || isPlayerVanished(queriedPlayerId)) {
                initializeVanishPlayerIPCMessage.addMessage(queriedPlayerId.toString());
            }
        }
        
        for(UUID pickupPlayerId : pickupPlayerIds) {
            initializePlayerPickupIPCMessage.addMessage(pickupPlayerId.toString());
        }
        
        ipcPlugin.sendIPCMessage(initializeVanishPlayerIPCMessage);
        ipcPlugin.sendIPCMessage(initializePlayerPickupIPCMessage);
    }
    
    public boolean isPlayerFullyVisible(UUID playerToCheckId) {
        
        if(playerToCheckId == null) {
            
            //TODO: Log error.
            return false;
        }
        
        return !playerIdHiddenStatus.containsKey(playerToCheckId);
    }
    
    public HashSet<UUID> getUnlistedPlayerIds() {
        
        HashSet<UUID> unlistedPlayerIds = new HashSet<UUID>();
        
        for(UUID queriedPlayerId : playerIdHiddenStatus.keySet()) {
            if(isPlayerUnlisted(queriedPlayerId)) {
                unlistedPlayerIds.add(queriedPlayerId);
            }
        }
        
        return unlistedPlayerIds;
    }
    
    public boolean isPlayerUnlisted(UUID playerToCheckId) {
        return playerIdHiddenStatus.get(playerToCheckId) == HiddenStatus.UNLISTED;
    }
    
    public boolean unlistPlayer(UUID playerToUnlistId) {
        
        if(playerToUnlistId == null) {
            
            //TODO: Log error.
            return false;
        }
        
        if(isPlayerHidden(playerToUnlistId)) {
            
            //TODO: Log error.
            return false;
        }
        else if(isPlayerUnlisted(playerToUnlistId)) {
            
            //TODO: Log error.
            return false;
        }
        else if(isPlayerVanished(playerToUnlistId)) {
            
            playerIdHiddenStatus.put(playerToUnlistId, HiddenStatus.HIDDEN);
            return true;
        }
        else {
            
            playerIdHiddenStatus.put(playerToUnlistId, HiddenStatus.UNLISTED);
            return true;
        }
    }
    
    public boolean relistPlayer(UUID playerToRelistId) {
        
        if(playerToRelistId == null) {
            
            //TODO: Log error.
            return false;
        }
        
        if(isPlayerFullyVisible(playerToRelistId)) {
            
            //TODO: Log error.
            return false;
        }
        else if(isPlayerVanished(playerToRelistId)) {
            
            //TODO: Log error.
            return false;
        }
        else if(isPlayerHidden(playerToRelistId)) {
            
            playerIdHiddenStatus.put(playerToRelistId, HiddenStatus.VANISHED);
            return true;
        }
        else {
            
            playerIdHiddenStatus.remove(playerToRelistId);
            return true;
        }
    }
    
    public HashSet<UUID> getVanishedPlayerIds() {
        
        HashSet<UUID> vanishedPlayerIds = new HashSet<UUID>();
        
        for(UUID queriedPlayerId : playerIdHiddenStatus.keySet()) {
            if(isPlayerVanished(queriedPlayerId)) {
                vanishedPlayerIds.add(queriedPlayerId);
            }
        }
        
        return vanishedPlayerIds;
    }
    
    public boolean isPlayerVanished(UUID playerToCheckId) {
        return playerIdHiddenStatus.get(playerToCheckId) == HiddenStatus.VANISHED;
    }
    
    public boolean vanishPlayer(UUID playerToVanishId) {
        
        if(playerToVanishId == null) {
            
            //TODO: Log error.
            return false;
        }
        
        if(isPlayerVanished(playerToVanishId)) {
            
            //TODO: Log error.
            return false;
        }
        else if(isPlayerHidden(playerToVanishId)) {
            
            //TODO: Log error.
            return false;
        }
        else if(isPlayerUnlisted(playerToVanishId)) {
            
            playerIdHiddenStatus.put(playerToVanishId, HiddenStatus.HIDDEN);
            
            for(String proxyIPCConnectionName : ipcPlugin.getProxyIPCConnectionNames()) {
                
                IPCMessage unvanishPlayerIPCMessage = new IPCMessage(proxyIPCConnectionName, "vanishPlayer");
                unvanishPlayerIPCMessage.addMessage(playerToVanishId.toString());
                ipcPlugin.sendIPCMessage(unvanishPlayerIPCMessage);
            }
            
            return true;
        }
        else {
            
            playerIdHiddenStatus.put(playerToVanishId, HiddenStatus.VANISHED);
            
            for(String proxyIPCConnectionName : ipcPlugin.getProxyIPCConnectionNames()) {
                
                IPCMessage unvanishPlayerIPCMessage = new IPCMessage(proxyIPCConnectionName, "vanishPlayer");
                unvanishPlayerIPCMessage.addMessage(playerToVanishId.toString());
                ipcPlugin.sendIPCMessage(unvanishPlayerIPCMessage);
            }
            
            return true;
        }
    }
    
    public boolean unvanishPlayer(UUID playerToUnvanishId) {
        
        if(playerToUnvanishId == null) {
            
            //TODO: Log error.
            return false;
        }
        
        if(isPlayerFullyVisible(playerToUnvanishId)) {
            
            //TODO: Log error.
            return false;
        }
        else if(isPlayerUnlisted(playerToUnvanishId)) {
            
            //TODO: Log error.
            return false;
        }
        else if(isPlayerHidden(playerToUnvanishId)) {
            
            playerIdHiddenStatus.put(playerToUnvanishId, HiddenStatus.UNLISTED);
            
            for(String proxyIPCConnectionName : ipcPlugin.getProxyIPCConnectionNames()) {
                
                IPCMessage unvanishPlayerIPCMessage = new IPCMessage(proxyIPCConnectionName, "unvanishPlayer");
                unvanishPlayerIPCMessage.addMessage(playerToUnvanishId.toString());
                ipcPlugin.sendIPCMessage(unvanishPlayerIPCMessage);
            }
            
            return true;
        }
        else {
            
            playerIdHiddenStatus.remove(playerToUnvanishId);
            
            for(String proxyIPCConnectionName : ipcPlugin.getProxyIPCConnectionNames()) {
                
                IPCMessage unvanishPlayerIPCMessage = new IPCMessage(proxyIPCConnectionName, "unvanishPlayer");
                unvanishPlayerIPCMessage.addMessage(playerToUnvanishId.toString());
                ipcPlugin.sendIPCMessage(unvanishPlayerIPCMessage);
            }
            
            return true;
        }
    }
    
    public HashSet<UUID> getHiddenPlayerIds() {
        
        HashSet<UUID> hiddenPlayerIds = new HashSet<UUID>();
        
        for(UUID queriedPlayerId : playerIdHiddenStatus.keySet()) {
            if(isPlayerHidden(queriedPlayerId)) {
                hiddenPlayerIds.add(queriedPlayerId);
            }
        }
        
        return hiddenPlayerIds;
    }
    
    public boolean isPlayerHidden(UUID playerToCheckId) {
        return playerIdHiddenStatus.get(playerToCheckId) == HiddenStatus.HIDDEN;
    }
    
    public boolean hidePlayer(UUID playerToHideId) {
        
        if(playerToHideId == null) {
            
            //TODO: Log error.
            return false;
        }
        
        if(isPlayerHidden(playerToHideId)) {
            
            //TODO: Log error.
            return false;
        }
        else {
            
            playerIdHiddenStatus.put(playerToHideId, HiddenStatus.HIDDEN);
            
            for(String proxyIPCConnectionName : ipcPlugin.getProxyIPCConnectionNames()) {
                
                IPCMessage unvanishPlayerIPCMessage = new IPCMessage(proxyIPCConnectionName, "vanishPlayer");
                unvanishPlayerIPCMessage.addMessage(playerToHideId.toString());
                ipcPlugin.sendIPCMessage(unvanishPlayerIPCMessage);
            }
            
            return true;
        }
    }
    
    public boolean showPlayer(UUID playerToShowId) {
        
        if(playerToShowId == null) {
            
            //TODO: Log error.
            return false;
        }
        
        if(isPlayerFullyVisible(playerToShowId)) {
            
            //TODO: Log error.
            return false;
        }
        else {
            
            playerIdHiddenStatus.remove(playerToShowId);
            
            for(String proxyIPCConnectionName : ipcPlugin.getProxyIPCConnectionNames()) {
                
                IPCMessage unvanishPlayerIPCMessage = new IPCMessage(proxyIPCConnectionName, "unvanishPlayer");
                unvanishPlayerIPCMessage.addMessage(playerToShowId.toString());
                ipcPlugin.sendIPCMessage(unvanishPlayerIPCMessage);
            }
            
            return true;
        }
    }
    
    public boolean isPlayerPickupEnabled(UUID playerToCheckId) {
        return pickupPlayerIds.contains(playerToCheckId);
    }
    
    public boolean enablePlayerPickup(UUID playerToEnablePickupId) {
        
        if(playerToEnablePickupId == null) {
            //TODO: Log error.
            return false;
        }
        
        if(isPlayerPickupEnabled(playerToEnablePickupId)) {
            //TODO: Log error.
            return false;
        }
        
        pickupPlayerIds.add(playerToEnablePickupId);
        
        for(String proxyIPCConnectionName : ipcPlugin.getProxyIPCConnectionNames()) {
            
            IPCMessage enablePlayerPickupIPCMessage = new IPCMessage(proxyIPCConnectionName, "enablePlayerPickup");
            enablePlayerPickupIPCMessage.addMessage(playerToEnablePickupId.toString());
            ipcPlugin.sendIPCMessage(enablePlayerPickupIPCMessage);
        }
        
        return true;
    }
    
    public boolean disablePlayerPickup(UUID playerToDisablePickupId) {
        
        if(playerToDisablePickupId == null) {
            //TODO: Log error.
            return false;
        }
        
        if(!isPlayerPickupEnabled(playerToDisablePickupId)) {
            //TODO: Log error.
            return false;
        }
        
        pickupPlayerIds.remove(playerToDisablePickupId);
        
        for(String proxyIPCConnectionName : ipcPlugin.getProxyIPCConnectionNames()) {
            
            IPCMessage disablePlayerPickupIPCMessage = new IPCMessage(proxyIPCConnectionName, "disablePlayerPickup");
            disablePlayerPickupIPCMessage.addMessage(playerToDisablePickupId.toString());
            ipcPlugin.sendIPCMessage(disablePlayerPickupIPCMessage);
        }
        
        return true;
    }
}
