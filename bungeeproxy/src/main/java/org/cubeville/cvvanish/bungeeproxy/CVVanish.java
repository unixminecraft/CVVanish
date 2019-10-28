/*
 * CVVanish Copyright (C) 2019 Cubeville
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package org.cubeville.cvvanish.bungeeproxy;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeville.common.bungeecord.command.BaseCommand;
import org.cubeville.common.bungeecord.command.SubBaseCommand;
import org.cubeville.cvipc.bungeeproxy.CVIPC;
import org.cubeville.cvipc.bungeeproxy.IPCMessage;
import org.cubeville.cvipc.bungeeproxy.listener.IPCInterface;
import org.cubeville.cvtools.bungeecord.CVTools;
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
import org.cubeville.cvvanish.bungeeproxy.command.pickup.PickupOffCommand;
import org.cubeville.cvvanish.bungeeproxy.command.pickup.PickupOnCommand;
import org.cubeville.cvvanish.bungeeproxy.listener.EventListener;
import org.cubeville.cvvanish.bungeeproxy.listener.ProxyIPCInterface;
import org.cubeville.cvvanish.bungeeproxy.thread.HiddenNotifier;
import org.cubeville.cvvanish.bungeeproxy.thread.UnlistedNotifier;
import org.cubeville.cvvanish.bungeeproxy.thread.VanishedNotifier;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public final class CVVanish extends Plugin {
	
	private enum HiddenStatus {
		
		UNLISTED(VALUE_STATUS_UNLISTED), VANISHED(VALUE_STATUS_VANISHED), HIDDEN(VALUE_STATUS_HIDDEN);
		
		private final String key;
		
		private HiddenStatus(final String key) {
			
			this.key = key;
		}
		
		private final String getKey() {
			
			return key;
		}
		
		private static final HiddenStatus fromKey(final String key) throws IllegalArgumentException {
			
			if(key.equals(VALUE_STATUS_UNLISTED)) {
				return UNLISTED;
			}
			else if(key.equals(VALUE_STATUS_VANISHED)) {
				return VANISHED;
			}
			else if(key.equals(VALUE_STATUS_HIDDEN)) {
				return HIDDEN;
			}
			else {
				throw new IllegalArgumentException("HiddenStatus Key " + key + " is not a valid Key.");
			}
		}
	}
	
	public static final String PERMISSION_SILENT_JOIN = "cvvanish.silent.join";
	public static final String PERMISSION_SILENT_LEAVE = "cvvanish.silent.leave";
	public static final String PERMISSION_SILENT_NOTIFY = "cvvanish.silent.notify";
	
	public static final String PERMISSION_HIDDEN_JOIN = "cvvanish.hidden.join";
	
	public static final String PERMISSION_FAKEJOIN_USE = "cvvanish.fakejoin.use";
	public static final String PERMISSION_FAKEJOIN_NOTIFY = "cvvanish.fakejoin.notify";
	
	public static final String PERMISSION_FAKEQUIT_USE = "cvvanish.fakequit.use";
	public static final String PERMISSION_FAKEQUIT_NOTIFY = "cvvanish.fakequit.notify";
	
	public static final String PERMISSION_HIDE_USE = "cvvanish.hide.use";
	public static final String PERMISSION_HIDE_NOTIFY = "cvvanish.hide.notify";
	
	public static final String PERMISSION_LEGACYPV_USE = "cvvanish.legacypv.use";
	
	public static final String PERMISSION_LEGACYV_USE = "cvvanish.legacyv.use";
	
	public static final String PERMISSION_PICKUP_USE = "cvvanish.pickup.use";
	
	public static final String PERMISSION_PICKUP_OFF_USE = "cvvanish.pickup.off.use";
	
	public static final String PERMISSION_PICKUP_ON_USE = "cvvanish.pickup.on.use";
	
	public static final String PERMISSION_SHOW_USE = "cvvanish.show.use";
	public static final String PERMISSION_SHOW_NOTIFY = "cvvanish.show.notify";
	
	public static final String PERMISSION_TABOFF_USE = "cvvanish.taboff.use";
	public static final String PERMISSION_TABOFF_NOTIFY = "cvvanish.taboff.notify";
	
	public static final String PERMISSION_TABON_USE = "cvvanish.tabon.use";
	public static final String PERMISSION_TABON_NOTIFY = "cvvanish.tabon.notify";
	
	public static final String PERMISSION_VISIBILITYOFF_USE = "cvvanish.visibilityoff.use";
	public static final String PERMISSION_VISIBILITYOFF_NOTIFY = "cvvanish.visibilityoff.notify";
	
	public static final String PERMISSION_VISIBILITYON_USE = "cvvanish.visibilityon.use";
	public static final String PERMISSION_VISIBILITYON_NOTIFY = "cvvanish.visibilityon.notify";
	
	public static final String CHANNEL_CVVANISH_BUKKIT_READY = "CVVANISH_BUKKIT_READY";
	
	private static final String DEFAULT_STRING = "8SvDtQVF7q1Ycu3j6OsBL1Wcva8daYAO";
	
	private static final String HIDDEN_PLAYER_DIRECTORY_NAME = "Hidden-Players";
	private static final String PICKUP_PLAYER_DIRECTORY_NAME = "Pickup-Players";
	
	private static final String HIDDEN_FILE_NAME = "hidden.yml";
	private static final String PICKUP_FILE_NAME = "pickup.yml";
	
	private static final String VALUE_STATUS_UNLISTED = "STATUS_UNLISTED";
	private static final String VALUE_STATUS_VANISHED = "STATUS_VANISHED";
	private static final String VALUE_STATUS_HIDDEN = "STATUS_HIDDEN";
	
	private static final String VALUE_PICKUP_VALID = "PICKUP_VALID";
	
	private Logger logger;
	
	private File hiddenDirectory;
	private File pickupDirectory;
	
	private File hiddenFile;
	private File pickupFile;
	
	private ConfigurationProvider yamlProvider;
	
	private ConcurrentHashMap<UUID, HiddenStatus> playerIdToHiddenStatus;
	private HashSet<UUID> pickupPlayerIds;
	
	private CVIPC ipcPlugin;
	
	private HiddenNotifier hiddenNotifier;
	private UnlistedNotifier unlistedNotifier;
	private VanishedNotifier vanishedNotifier;
	
	@Override
	public void onEnable() {
		
		/*
		 * Logger setup
		 */
		
		logger = getLogger();
		
		/*
		 * License information
		 */
		
		displayLicenseInformation();
		
		/*
		 * Hidden and Pickup player directory and file setup
		 */
		
		hiddenDirectory = new File(getDataFolder(), HIDDEN_PLAYER_DIRECTORY_NAME);
		final String hiddenDirectoryPath = hiddenDirectory.getPath();
		
		try {
			
			if(!hiddenDirectory.exists()) {
				
				try {
					
					hiddenDirectory.mkdirs();
				}
				catch(SecurityException e) {
					
					logger.log(Level.SEVERE, "SecurityException thrown while attempting to create hidden directory at " + hiddenDirectoryPath + ", cannot start CVVanish.", e);
					throw new RuntimeException("SecurityException thrown while attempting to create hidden directory at " + hiddenDirectoryPath + ", cannot start CVVanish.", e);
				}
			}
		}
		catch(SecurityException e) {
			
			logger.log(Level.SEVERE, "SecurityException thrown while checking to see if hidden directory exists at " + hiddenDirectoryPath + ", cannot start CVVanish.", e);
			throw new RuntimeException("SecurityException thrown while checking to see if hidden directory exists at " + hiddenDirectoryPath + ", cannot start CVVanish.", e);
		}
		
		hiddenFile = new File(hiddenDirectory, HIDDEN_FILE_NAME);
		final String hiddenFilePath = hiddenFile.getPath();
		
		try {
			
			if(!hiddenFile.exists()) {
				
				try {
					
					hiddenFile.createNewFile();
				}
				catch(IOException e) {
					
					logger.log(Level.SEVERE, "IOException thrown while attempting to create hidden file at " + hiddenFilePath + ", cannot start CVVanish.", e);
					throw new RuntimeException("IOException thrown while attempting to create hidden file at " + hiddenFilePath + ", cannot start CVVanish.", e);
				}
				catch(SecurityException e) {
					
					logger.log(Level.SEVERE, "SecurityException thrown while attempting to create hidden file at " + hiddenFilePath + ", cannot start CVVanish.", e);
					throw new RuntimeException("SecurityException thrown while attempting to create hidden file at " + hiddenFilePath + ", cannot start CVVanish.", e);
				}
			}
		}
		catch(SecurityException e) {
			
			logger.log(Level.SEVERE, "SecurityException thrown while checking to see if hidden file exists at " + hiddenFilePath + ", cannot start CVVanish.", e);
			throw new RuntimeException("SecurityException thrown while checking to see if hidden file exists at " + hiddenFilePath + ", cannot start CVVanish.", e);
		}
		
		pickupDirectory = new File(getDataFolder(), PICKUP_PLAYER_DIRECTORY_NAME);
		final String pickupDirectoryPath = pickupDirectory.getPath();
		
		try {
			
			if(!pickupDirectory.exists()) {
				
				try {
					
					pickupDirectory.mkdirs();
				}
				catch(SecurityException e) {
					
					logger.log(Level.SEVERE, "SecurityException thrown while attempting to create pickup directory at " + pickupDirectoryPath + ", cannot start CVVanish.", e);
					throw new RuntimeException("SecurityException thrown while attempting to create pickup directory at " + pickupDirectoryPath + ", cannot start CVVanish.", e);
				}
			}
		}
		catch(SecurityException e) {
			
			logger.log(Level.SEVERE, "SecurityException thrown while checking to see if pickup directory exists at " + pickupDirectoryPath + ", cannot start CVVanish.", e);
			throw new RuntimeException("SecurityException thrown while checking to see if pickup directory exists at " + pickupDirectoryPath + ", cannot start CVVanish.", e);
		}
		
		pickupFile = new File(pickupDirectory, PICKUP_FILE_NAME);
		final String pickupFilePath = pickupFile.getPath();
		
		try {
			
			if(!pickupFile.exists()) {
				
				try {
					
					pickupFile.createNewFile();
				}
				catch(IOException e) {
					
					logger.log(Level.SEVERE, "IOException thrown while attempting to create pickup file at " + pickupFilePath + ", cannot start CVVanish.", e);
					throw new RuntimeException("IOException thrown while attempting to create pickup file at " + pickupFilePath + ", cannot start CVVanish.", e);
				}
				catch(SecurityException e) {
					
					logger.log(Level.SEVERE, "SecurityException thrown while attempting to create pickup file at " + pickupFilePath + ", cannot start CVVanish.", e);
					throw new RuntimeException("SecurityException thrown while attempting to create pickup file at " + pickupFilePath + ", cannot start CVVanish.", e);
				}
			}
		}
		catch(SecurityException e) {
			
			logger.log(Level.SEVERE, "SecurityException thrown while checking to see if pickup file exists at " + pickupFilePath + ", cannot start CVVanish.", e);
			throw new RuntimeException("SecurityException thrown while checking to see if pickup file exists at " + pickupFilePath + ", cannot start CVVanish.", e);
		}
		
		/*
		 * Hidden and pickup player configuration setup
		 */
		
		yamlProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		
		Configuration hiddenConfiguration = null;
		
		try {
			
			hiddenConfiguration = yamlProvider.load(hiddenFile);
		}
		catch(IOException e) {
			
			logger.log(Level.SEVERE, "IOException thrown while attempting to load hidden configuration file, cannot start CVVanish.", e);
			throw new RuntimeException("IOException thrown while attempting to load hidden configuration file, cannot start CVVanish.", e);
		}
		
		if(hiddenConfiguration == null) {
			
			logger.log(Level.SEVERE, "Hidden configuration file is still null, even after loading, cannot start CVVanish.");
			throw new RuntimeException("Hidden configuration file is still null, even after loading, cannot start CVVanish.");
		}
		
		Configuration pickupConfiguration = null;
		
		try {
			
			pickupConfiguration = yamlProvider.load(pickupFile);
		}
		catch(IOException e) {
			
			logger.log(Level.SEVERE, "IOException thrown while attempting to load pickup configuration file, cannot start CVVanish.", e);
			throw new RuntimeException("IOException thrown while attempting to load pickup configuration file, cannot start CVVanish.", e);
		}
		
		if(pickupConfiguration == null) {
			
			logger.log(Level.SEVERE, "Pickup configuration file is still null, even after loading, cannot start CVVanish.");
			throw new RuntimeException("Pickup configuration file is still null, even after loading, cannot start CVVanish.");
		}
		
		/*
		 * Hidden and pickup player data initialization
		 */
		
		playerIdToHiddenStatus = new ConcurrentHashMap<UUID, HiddenStatus>();
		pickupPlayerIds = new HashSet<UUID>();
		
		/*
		 * Hidden and pickup player data loading
		 */
		
		for(final String playerIdValue : hiddenConfiguration.getKeys()) {
			
			UUID playerId = null;
			
			try {
				
				playerId = UUID.fromString(playerIdValue);
			}
			catch(IllegalArgumentException e) {
				
				logger.log(Level.WARNING, "Unable to parse hidden UUID " + playerIdValue + ", skipping.", e);
				continue;
			}
			
			if(playerId == null) {
				
				logger.log(Level.WARNING, "Hidden UUID " + playerIdValue + " is still null, skipping.");
				continue;
			}
			
			String hiddenStatusValue = hiddenConfiguration.getString(playerIdValue, DEFAULT_STRING);
			
			if(hiddenStatusValue.equals(DEFAULT_STRING)) {
				
				logger.log(Level.WARNING, "Invalid hidden status value for UUID " + playerIdValue + ", skipping.");
				continue;
			}
			
			hiddenStatusValue = hiddenStatusValue.trim();
			
			if(hiddenStatusValue.isEmpty()) {
				
				logger.log(Level.WARNING, "Hidden status value for UUID " + playerIdValue + " cannot be blank, skipping.");
				continue;
			}
			
			HiddenStatus hiddenStatus = null;
			
			try {
				
				hiddenStatus = HiddenStatus.fromKey(hiddenStatusValue);
			}
			catch(IllegalArgumentException e) {
				
				logger.log(Level.WARNING, "Invalid hidden status value for UUID " + playerIdValue + ", skipping.", e);
				continue;
			}
			
			if(hiddenStatus == null) {
				
				logger.log(Level.WARNING, "Hidden status value for UUID " + playerIdValue + " is still null, skipping.");
				continue;
			}
			
			if(playerIdToHiddenStatus.containsKey(playerId)) {
				
				logger.log(Level.WARNING, "UUID " + playerIdValue + " already has hidden status value " + playerIdToHiddenStatus.get(playerId).getKey() + " assigned, please check the configuration file for duplicate entries, skipping.");
				continue;
			}
			
			playerIdToHiddenStatus.put(playerId, hiddenStatus);
		}
		
		for(final String playerIdValue : pickupConfiguration.getKeys()) {
			
			UUID playerId = null;
			
			try {
				
				playerId = UUID.fromString(playerIdValue);
			}
			catch(IllegalArgumentException e) {
				
				logger.log(Level.WARNING, "Unable to parse pickup UUID " + playerIdValue + ", skipping.", e);
				continue;
			}
			
			if(playerId == null) {
				
				logger.log(Level.WARNING, "Pickup UUID " + playerIdValue + " is still null, skipping.");
				continue;
			}
			
			final String validStatus = pickupConfiguration.getString(playerIdValue, DEFAULT_STRING);
			
			if(!validStatus.equals(VALUE_PICKUP_VALID)) {
				
				logger.log(Level.WARNING, "Pickup UUID " + playerIdValue + " does not have a vaild indicator, skipping.");
				continue;
			}
			
			if(!pickupPlayerIds.add(playerId)) {
				
				logger.log(Level.WARNING, "Pickup UUID " + playerIdValue + " was already added to the Set of pickup-enabled Players, please check the configuration for duplicate entries.");
			}
		}
		
		/*
		 * PluginManager setup
		 */
		
		final PluginManager pluginManager = getProxy().getPluginManager();
		
		/*
		 * Dependency plugin setup
		 */
		
		final Plugin possibleToolsPlugin = pluginManager.getPlugin("CVTools");
		if(possibleToolsPlugin == null) {
			
			logger.log(Level.SEVERE, "CVTools not found, cannot start CVVanish.");
			throw new RuntimeException("CVTools not found, cannot start CVVanish.");
		}
		if(!(possibleToolsPlugin instanceof CVTools)) {
			
			logger.log(Level.SEVERE, "CVTools not correct plugin type, cannot start CVVanish.");
			throw new RuntimeException("CVTools not correct plugin type, cannot start CVVanish.");
		}
		
		final Plugin possibleIPCPlugin = pluginManager.getPlugin("CVIPC");
		if(possibleIPCPlugin == null) {
			
			logger.log(Level.SEVERE, "CVIPC not found, cannot start CVVanish.");
			throw new RuntimeException("CVIPC not found, cannot start CVVanish.");
		}
		if(!(possibleIPCPlugin instanceof CVIPC)) {
			
			logger.log(Level.SEVERE, "CVIPC not correct plugin type, cannot start CVVanish.");
			throw new RuntimeException("CVIPC not correct plugin type, cannot start CVVanish.");
		}
		
		ipcPlugin = (CVIPC) possibleIPCPlugin;
		
		/*
		 * Command setup
		 */
		
		final BaseCommand fakeJoinCommand = new FakeJoinCommand(this);
		final BaseCommand fakeQuitCommand = new FakeQuitCommand(this);
		final BaseCommand hideCommand = new HideCommand(this);
		final BaseCommand legacyPVCommand = new LegacyPVCommand(this);
		final BaseCommand legacyVCommand = new LegacyVCommand(this);
		final BaseCommand pickupCommand = new PickupCommand(this);
		final BaseCommand showCommand = new ShowCommand(this);
		final BaseCommand tabOffCommand = new TabOffCommand(this);
		final BaseCommand tabOnCommand = new TabOnCommand(this);
		final BaseCommand visibilityOffCommand = new VisibilityOffCommand(this);
		final BaseCommand visibilityOnCommand = new VisibilityOnCommand(this);
		
		final SubBaseCommand pickupOffCommand = new PickupOffCommand(this);
		final SubBaseCommand pickupOnCommand = new PickupOnCommand(this);
		
		pickupCommand.addSubCommand(pickupOffCommand);
		pickupCommand.addSubCommand(pickupOnCommand);
		
		pluginManager.registerCommand(this, fakeJoinCommand);
		pluginManager.registerCommand(this, fakeQuitCommand);
		pluginManager.registerCommand(this, hideCommand);
		pluginManager.registerCommand(this, legacyPVCommand);
		pluginManager.registerCommand(this, legacyVCommand);
		pluginManager.registerCommand(this, pickupCommand);
		pluginManager.registerCommand(this, showCommand);
		pluginManager.registerCommand(this, tabOffCommand);
		pluginManager.registerCommand(this, tabOnCommand);
		pluginManager.registerCommand(this, visibilityOffCommand);
		pluginManager.registerCommand(this, visibilityOnCommand);
		
		/*
		 * IPCInterface setup
		 */
		
		final IPCInterface proxyIPCInterface = new ProxyIPCInterface(this);
		
		ipcPlugin.registerIPCInterface(CHANNEL_CVVANISH_BUKKIT_READY, proxyIPCInterface);
		
		/*
		 * Listener setup
		 */
		
		pluginManager.registerListener(this, new EventListener(this));
		
		/*
		 * Thread setup
		 */
		
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
		
		ipcPlugin.deregisterIPCInterface("CHANNEL_CVVANISH_BUKKIT_READY");
		
		final Configuration hiddenConfiguration = new Configuration();
		final Configuration pickupConfiguration = new Configuration();
		
		for(final UUID playerId : playerIdToHiddenStatus.keySet()) {
			
			final HiddenStatus hiddenStatus = playerIdToHiddenStatus.get(playerId);
			
			hiddenConfiguration.set(playerId.toString(), hiddenStatus.getKey());
		}
		
		for(final UUID playerId : pickupPlayerIds) {
			
			pickupConfiguration.set(playerId.toString(), VALUE_PICKUP_VALID);
		}
		
		try {
			yamlProvider.save(hiddenConfiguration, hiddenFile);
		}
		catch(IOException e) {
			
			logger.log(Level.WARNING, "IOException thrown while trying to save the hidden configuration upon shutdown.", e);
		}
		
		try {
			yamlProvider.save(pickupConfiguration, pickupFile);
		}
		catch(IOException e) {
			
			logger.log(Level.WARNING, "IOException thrown while trying to save the pickup configuration upon shutdown.", e);
		}
	}
	
	public void initializeServer(final String serverName) {
		
		final IPCMessage vanishIPCMessage = new IPCMessage(serverName, "VANISH_INITIALIZE");
		final IPCMessage pickupIPCMessage = new IPCMessage(serverName, "PICKUP_INITIALIZE");
		
		for(final UUID playerId : playerIdToHiddenStatus.keySet()) {
			
			if(isHidden(playerId) || isVanished(playerId)) {
				vanishIPCMessage.addMessage(playerId.toString());
			}
		}
		
		for(final UUID playerId : pickupPlayerIds) {
			
			pickupIPCMessage.addMessage(playerId.toString());
		}
		
		ipcPlugin.sendIPCMessage(vanishIPCMessage);
		ipcPlugin.sendIPCMessage(pickupIPCMessage);
	}
	
	public boolean canJoinSilent(ProxiedPlayer player) {
		
		return player.hasPermission(PERMISSION_SILENT_JOIN);
	}
	
	public boolean canLeaveSilent(ProxiedPlayer player) {
		
		return player.hasPermission(PERMISSION_SILENT_LEAVE);
	}
	
	public boolean canNotifySilent(ProxiedPlayer player) {
		
		return player.hasPermission(PERMISSION_SILENT_NOTIFY);
	}
	
	public HashSet<UUID> getUnlistedPlayerIds() {
		
		final HashSet<UUID> unlistedPlayerIds = new HashSet<UUID>();
		
		for(UUID playerId : playerIdToHiddenStatus.keySet()) {
			
			if(isUnlisted(playerId)) {
				unlistedPlayerIds.add(playerId);
			}
		}
		
		return unlistedPlayerIds;
	}
	
	public HashSet<UUID> getVanishedPlayerIds() {
		
		final HashSet<UUID> vanishedPlayerIds = new HashSet<UUID>();
		
		for(final UUID playerId : playerIdToHiddenStatus.keySet()) {
			
			if(isVanished(playerId)) {
				vanishedPlayerIds.add(playerId);
			}
		}
		
		return vanishedPlayerIds;
	}
	
	public HashSet<UUID> getHiddenPlayerIds() {
		
		final HashSet<UUID> hiddenPlayerIds = new HashSet<UUID>();
		
		for(final UUID playerId : playerIdToHiddenStatus.keySet()) {
			
			if(isHidden(playerId)) {
				hiddenPlayerIds.add(playerId);
			}
		}
		
		return hiddenPlayerIds;
	}
	
	public boolean isFullyVisible(UUID playerId) {
		
		if(playerId == null) {
			
			return false;
		}
		
		return !playerIdToHiddenStatus.containsKey(playerId);
	}
	
	public boolean isUnlisted(UUID playerId) {
		
		return playerIdToHiddenStatus.get(playerId) == HiddenStatus.UNLISTED;
	}
	
	public boolean unlist(UUID playerId) {
		
		if(playerId == null) {
			
			return false;
		}
		
		if(isHidden(playerId)) {
			
			return false;
		}
		else if(isUnlisted(playerId)) {
			
			return false;
		}
		else if(isVanished(playerId)) {
			
			playerIdToHiddenStatus.put(playerId, HiddenStatus.HIDDEN);
			return true;
		}
		else {
			
			playerIdToHiddenStatus.put(playerId, HiddenStatus.UNLISTED);
			return true;
		}
	}
	
	public boolean relist(final UUID playerId) {
		
		if(playerId == null) {
			
			return false;
		}
		
		if(isFullyVisible(playerId)) {
			
			return false;
		}
		else if(isVanished(playerId)) {
			
			return false;
		}
		else if(isHidden(playerId)) {
			
			playerIdToHiddenStatus.put(playerId, HiddenStatus.VANISHED);
			return true;
		}
		else {
			
			playerIdToHiddenStatus.remove(playerId);
			return true;
		}
	}
	
	public boolean isVanished(final UUID playerId) {
		
		return playerIdToHiddenStatus.get(playerId) == HiddenStatus.VANISHED;
	}
	
	public boolean vanish(final UUID playerId) {
		
		if(playerId == null) {
			
			return false;
		}
		
		if(isVanished(playerId)) {
			
			return false;
		}
		else if(isHidden(playerId)) {
			
			return false;
		}
		else if(isUnlisted(playerId)) {
			
			playerIdToHiddenStatus.put(playerId, HiddenStatus.HIDDEN);
			
			final IPCMessage ipcMessage = new IPCMessage(IPCMessage.CHANNEL_BROADCAST, "VANISH_ENABLE");
			
			ipcMessage.addMessage(playerId.toString());
			
			ipcPlugin.broadcastIPCMessage(ipcMessage);
			
			return true;
		}
		else {
			
			playerIdToHiddenStatus.put(playerId, HiddenStatus.VANISHED);
			
			final IPCMessage ipcMessage = new IPCMessage(IPCMessage.CHANNEL_BROADCAST, "VANISH_ENABLE");
			
			ipcMessage.addMessage(playerId.toString());
			
			ipcPlugin.broadcastIPCMessage(ipcMessage);
			
			return true;
		}
	}
	
	public boolean unvanish(final UUID playerId) {
		
		if(playerId == null) {
			
			return false;
		}
		
		if(isFullyVisible(playerId)) {
			
			return false;
		}
		else if(isUnlisted(playerId)) {
			
			return false;
		}
		else if(isHidden(playerId)) {
			
			playerIdToHiddenStatus.put(playerId, HiddenStatus.UNLISTED);
			
			final IPCMessage ipcMessage = new IPCMessage(IPCMessage.CHANNEL_BROADCAST, "VANISH_DISABLE");
			
			ipcMessage.addMessage(playerId.toString());
			
			ipcPlugin.broadcastIPCMessage(ipcMessage);
			
			return true;
		}
		else {
			
			playerIdToHiddenStatus.remove(playerId);
			
			final IPCMessage ipcMessage = new IPCMessage(IPCMessage.CHANNEL_BROADCAST, "VANISH_DISABLE");
			
			ipcMessage.addMessage(playerId.toString());
			
			ipcPlugin.broadcastIPCMessage(ipcMessage);
			
			return true;
		}
	}
	
	public boolean isHidden(final UUID playerId) {
		
		return playerIdToHiddenStatus.get(playerId) == HiddenStatus.HIDDEN;
	}
	
	public boolean hide(final UUID playerId) {
		
		if(playerId == null) {
			
			return false;
		}
		
		if(isHidden(playerId)) {
			
			return false;
		}
		
		playerIdToHiddenStatus.put(playerId, HiddenStatus.HIDDEN);
		
		final IPCMessage ipcMessage = new IPCMessage(IPCMessage.CHANNEL_BROADCAST, "VANISH_ENABLE");
		
		ipcMessage.addMessage(playerId.toString());
		
		ipcPlugin.broadcastIPCMessage(ipcMessage);
		
		return true;
	}
	
	public boolean show(final UUID playerId) {
		
		if(playerId == null) {
			
			return false;
		}
		
		if(isFullyVisible(playerId)) {
			
			return false;
		}
		
		playerIdToHiddenStatus.remove(playerId);
		
		final IPCMessage ipcMessage = new IPCMessage(IPCMessage.CHANNEL_BROADCAST, "VANISH_DISABLE");
		
		ipcMessage.addMessage(playerId.toString());
		
		ipcPlugin.broadcastIPCMessage(ipcMessage);
		
		return true;
	}
	
	public boolean isPickupEnabled(final UUID playerId) {
		
		return pickupPlayerIds.contains(playerId);
	}
	
	public boolean enablePickup(final UUID playerId) {
		
		if(playerId == null) {
			
			return false;
		}
		
		if(isPickupEnabled(playerId)) {
			
			return false;
		}
		
		pickupPlayerIds.add(playerId);
		
		final IPCMessage ipcMessage = new IPCMessage(IPCMessage.CHANNEL_BROADCAST, "PICKUP_ENABLE");
		
		ipcMessage.addMessage(playerId.toString());
		
		ipcPlugin.broadcastIPCMessage(ipcMessage);
		
		return true;
	}
	
	public boolean disablePickup(final UUID playerId) {
		
		if(playerId == null) {
			
			return false;
		}
		
		if(!isPickupEnabled(playerId)) {
			
			return false;
		}
		
		pickupPlayerIds.remove(playerId);
		
		final IPCMessage ipcMessage = new IPCMessage(IPCMessage.CHANNEL_BROADCAST, "PICKUP_DISABLE");
		
		ipcMessage.addMessage(playerId.toString());
		
		ipcPlugin.broadcastIPCMessage(ipcMessage);
		
		return true;
	}
	
	private void displayLicenseInformation() {
		
		/*
		 * CVVanish Copyright (C) 2019 Cubeville
		 * 
		 * This program is free software: you can redistribute it and/or modify it under the terms of the
		 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
		 * License, or (at your option) any later version.
		 * 
		 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
		 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
		 * General Public License for more details.
		 * 
		 * You should have received a copy of the GNU General Public License along with this program. If
		 * not, see <http://www.gnu.org/licenses/>.
		 */
		
		logger.log(Level.INFO, "//===========================================//");
		logger.log(Level.INFO, "//   CVVanish Copyright (C) 2019 Cubeville   //");
		logger.log(Level.INFO, "//                                           //");
		logger.log(Level.INFO, "// This program is free software: you can    //");
		logger.log(Level.INFO, "// redistribute it and/or modify it under    //");
		logger.log(Level.INFO, "// the terms of these GNU General Public     //");
		logger.log(Level.INFO, "// License as published by the Free Software //");
		logger.log(Level.INFO, "// Foundation, either version 3 of the       //");
		logger.log(Level.INFO, "// License, or (at your opinion) any later   //");
		logger.log(Level.INFO, "// version.                                  //");
		logger.log(Level.INFO, "//                                           //");
		logger.log(Level.INFO, "// This program is distributed in the hope   //");
		logger.log(Level.INFO, "// that is till be useful, but WITHOUT ANY   //");
		logger.log(Level.INFO, "// WARRANTY; without even the implied        //");
		logger.log(Level.INFO, "// warranty of MERCHANTABILITY or FITNESS    //");
		logger.log(Level.INFO, "// FOR A PARTICULAR PURPOSE. See GNU General //");
		logger.log(Level.INFO, "// Public License for more details.          //");
		logger.log(Level.INFO, "//                                           //");
		logger.log(Level.INFO, "// You should have received a copy of the    //");
		logger.log(Level.INFO, "// GNU General Public License along with     //");
		logger.log(Level.INFO, "// this program. If not, see                 //");
		logger.log(Level.INFO, "// <http://www.gnu.org/licenses/>            //");
		logger.log(Level.INFO, "//===========================================//");
	}
}
