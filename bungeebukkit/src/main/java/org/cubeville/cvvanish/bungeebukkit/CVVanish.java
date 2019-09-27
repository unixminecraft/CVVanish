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

package org.cubeville.cvvanish.bungeebukkit;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.cubeville.cvipc.bungeebukkit.CVIPC;
import org.cubeville.cvipc.bungeebukkit.listener.IPCInterface;
import org.cubeville.cvvanish.bungeebukkit.listener.EventListener;
import org.cubeville.cvvanish.bungeebukkit.listener.BukkitIPCInterface;
import org.cubeville.cvvanish.bungeebukkit.thread.NightVisionEffectIssuer;
import org.cubeville.cvvanish.bungeebukkit.thread.VanishInitializer;

public class CVVanish extends JavaPlugin {
	
	public static final String CHANNEL_VANISH_INITIALIZE = "VANISH_INITIALIZE";
	public static final String CHANNEL_VANISH_ENABLE = "VANISH_ENABLE";
	public static final String CHANNEL_VANISH_DISABLE = "VANISH_DISABLE";
	
	public static final String CHANNEL_PICKUP_INITIALIZE = "PICKUP_INITIALIZE";
	public static final String CHANNEL_PICKUP_ENABLE = "PICKUP_ENABLE";
	public static final String CHANNEL_PICKUP_DISABLE = "PICKUP_DISABLE";
	
	public static final String INVISIBLE_VIEW_PERMISSION = "cvvanish.invisible.view";
	
	private static final String VANISHED_TEAM_NAME = "VanishedTeam";
	
	private Logger logger;
	
	private HashSet<UUID> vanishedPlayerIds;
	private HashSet<UUID> pickupPlayerIds;
	
	private CVIPC ipcPlugin;
	
	private NightVisionEffectIssuer nightVisionEffectIssuer;
	
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
		 * Vanished and pickup player setup
		 */
		
		vanishedPlayerIds = new HashSet<UUID>();
		pickupPlayerIds = new HashSet<UUID>();
		
		/*
		 * Dependency plugin setup
		 */
		
		final PluginManager pluginManager = getServer().getPluginManager();
		
		final Plugin possibleIPCPlugin = pluginManager.getPlugin("CVIPC");
		if(possibleIPCPlugin == null) {
			
			logger.log(Level.SEVERE, "CVIPC plugin not found, cannot start CVVanish.");
			throw new RuntimeException("CVIPC plugin not found, cannot start CVVanish.");
		}
		if(!(possibleIPCPlugin instanceof CVIPC)) {
			
			logger.log(Level.SEVERE, "CVIPC plugin not of the correct type, cannot start CVVanish.");
			throw new RuntimeException("CVIPC plugin not of the correct type, cannot start CVVanish.");
		}
		
		ipcPlugin = (CVIPC) possibleIPCPlugin;
		
		/*
		 * IPC Interface setup
		 */
		
		final IPCInterface bukkitIPCInterface = new BukkitIPCInterface(this);
		
		ipcPlugin.registerIPCInterface(CHANNEL_VANISH_INITIALIZE, bukkitIPCInterface);
		ipcPlugin.registerIPCInterface(CHANNEL_VANISH_ENABLE, bukkitIPCInterface);
		ipcPlugin.registerIPCInterface(CHANNEL_VANISH_DISABLE, bukkitIPCInterface);
		
		ipcPlugin.registerIPCInterface(CHANNEL_PICKUP_INITIALIZE, bukkitIPCInterface);
		ipcPlugin.registerIPCInterface(CHANNEL_PICKUP_ENABLE, bukkitIPCInterface);
		ipcPlugin.registerIPCInterface(CHANNEL_PICKUP_DISABLE, bukkitIPCInterface);
		
		/*
		 * Event listener setup
		 */
		
		pluginManager.registerEvents(new EventListener(this), this);
		
		/*
		 * Thread setup
		 */
		
		nightVisionEffectIssuer = new NightVisionEffectIssuer(this);
		nightVisionEffectIssuer.start();
		
		VanishInitializer vanishInitializer = new VanishInitializer(this, ipcPlugin);
		vanishInitializer.start();
	}
	
	@Override
	public void onDisable() {
		
		nightVisionEffectIssuer.stop();
		
		ipcPlugin.deregisterIPCInterface(CHANNEL_PICKUP_DISABLE);
		ipcPlugin.deregisterIPCInterface(CHANNEL_PICKUP_ENABLE);
		ipcPlugin.deregisterIPCInterface(CHANNEL_PICKUP_INITIALIZE);
		
		ipcPlugin.deregisterIPCInterface(CHANNEL_PICKUP_INITIALIZE);
		ipcPlugin.deregisterIPCInterface(CHANNEL_VANISH_ENABLE);
		ipcPlugin.deregisterIPCInterface(CHANNEL_VANISH_INITIALIZE);
	}
	
	public boolean disappear(UUID playerId) {
		
		final Player player = getServer().getPlayer(playerId);
		if(player == null) {
			return false;
		}
		
		for(final Player onlinePlayer : (Collection<? extends Player>) getServer().getOnlinePlayers()) {
			
			if(onlinePlayer.getUniqueId().equals(playerId)) {
				continue;
			}
			if(onlinePlayer.hasPermission(INVISIBLE_VIEW_PERMISSION)) {
				continue;
			}
			onlinePlayer.hidePlayer(this, player);
		}
		
		// TODO: Check to make sure the invisible person doesn't push the visible
		// people, and not the other way around (invisible pushes visible).
		player.setCollidable(false);
		
		Team vanishTeam = player.getScoreboard().getTeam(VANISHED_TEAM_NAME);
		if(vanishTeam == null) {
			
			vanishTeam = player.getScoreboard().registerNewTeam(VANISHED_TEAM_NAME);
		}
		
		vanishTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		vanishTeam.addEntry(player.getName());
		
		return true;
	}
	
	public HashSet<UUID> getVanishedPlayerIds() {
		
		return vanishedPlayerIds;
	}
	
	public boolean isVanishEnabled(final UUID playerId) {
		
		return vanishedPlayerIds.contains(playerId);
	}
	
	public boolean enableVanish(final UUID playerId) {
		
		if(playerId == null) {
			return false;
		}
		
		if(isVanishEnabled(playerId)) {
			return false;
		}
		
		return vanishedPlayerIds.add(playerId);
	}
	
	public boolean disableVanish(final UUID playerId) {
		
		if(playerId == null) {
			return false;
		}
		
		if(!isVanishEnabled(playerId)) {
			return false;
		}
		
		vanishedPlayerIds.remove(playerId);
		
		final Player player = getServer().getPlayer(playerId);
		if(player == null) {
			return true;
		}
		
		for(final Player onlinePlayer : (Collection<? extends Player>) getServer().getOnlinePlayers()) {
			
			if(onlinePlayer.getUniqueId().equals(playerId)) {
				continue;
			}
			onlinePlayer.showPlayer(this, player);
		}
		
		player.setCollidable(true);
		
		final Team vanishTeam = player.getScoreboard().getTeam(VANISHED_TEAM_NAME);
		if(vanishTeam != null) {
			vanishTeam.removeEntry(player.getName());
		}
		
		player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		
		return true;
	}
	
	public boolean isPickupEnabled(final UUID playerId) {
		
		return pickupPlayerIds.contains(playerId);
	}
	
	public boolean enablePickup(final UUID playerId) {
		
		if(playerId == null) {
			return false;
		}
		
		return pickupPlayerIds.add(playerId);
	}
	
	public boolean disablePickup(final UUID playerId) {
		
		if(playerId == null) {
			return false;
		}
		
		return pickupPlayerIds.remove(playerId);
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
